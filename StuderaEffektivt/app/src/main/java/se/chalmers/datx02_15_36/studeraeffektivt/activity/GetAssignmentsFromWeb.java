/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.LabAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OtherAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ProblemAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentID;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.ServiceHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;

/**
 * Created by alexandraback on 06/05/15.
 * Updated by Soyapanda on 03/07/15.
 */
public class GetAssignmentsFromWeb {

    private String HandIN_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getHandInAssignments.php";
    private String lab_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getLabAssignments.php";
    private String problem_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getProblemAssignments.php";
    private String read_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getReadAssignments.php";
    private String obligatory_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getObligatoryAssignments.php";

    private Context context;


    public GetAssignmentsFromWeb(Context context){
        this.context = context;
    }

    public void addAssignmentsFromWeb(String courseCode){
        //courseCode = "TDA623";
        new GetHandInAssignments().execute(courseCode);
        new GetLabAssignments().execute(courseCode);
        new GetProblemAssignments().execute(courseCode);
        new GetReadAssignments().execute(courseCode);
        new GetObligatoryAssignments().execute(courseCode);
    }

    private class GetHandInAssignments extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String courseCode = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseCode", courseCode));

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(HandIN_URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    Log.d(jsonStr, "jsonStr");
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray handInAssignments = jsonObj.getJSONArray("handInAssignments");
                    // looping through All Contacts
                    for (int i = 0; i < handInAssignments.length(); i++) {
                        JSONObject c = handInAssignments.getJSONObject(i);

                        String nr = c.getString("nr");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        int id = AssignmentID.getID();

                        addToDatabase(courseCode, AssignmentType.HANDIN, id, nr, Integer.parseInt(week), assNr);

                        Log.i("GetAFWA", "HandInAsses ");
                        Log.i("GetAFWA", "nr " + nr);
                        Log.i("GetAFWA", "week " + week);
                        Log.i("GetAFWA", "assNr " + assNr);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String file_url) {

        }
    }

    private class GetLabAssignments extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String courseCode = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseCode", courseCode));

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(lab_URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    Log.d(jsonStr, "jsonStr " + jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray labAssignments = jsonObj.getJSONArray("labAssignments");
                    // looping through All Contacts
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String ccode = c.getString("ccode");
                        String nr = c.getString("nr");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        int id = AssignmentID.getID();

                        addToDatabase(courseCode, AssignmentType.LAB, id, nr, Integer.parseInt(week), assNr);


                        Log.i("GetAFWA", "Labasses ");
                        Log.i("GetAFWA", "ccode " + ccode);
                        Log.i("GetAFWA", "nr " + nr);
                        Log.i("GetAFWA", "week " + week);
                        Log.i("GetAFWA", "assNr " + assNr);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String file_url) {

        }
    }

    private class GetProblemAssignments extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String courseCode = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseCode", courseCode));

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(problem_URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    Log.d(jsonStr, "jsonStr " + jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray labAssignments = jsonObj.getJSONArray("problemAssignments");
                    // looping through All Contacts
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String ccode = c.getString("ccode");
                        String chapter = c.getString("chapter");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        int id = AssignmentID.getID();

                        //TODO: check if the assignment already exists in the db. if so, remove it.
                        addToDatabase(courseCode, AssignmentType.PROBLEM, id, chapter, Integer.parseInt(week), assNr);

                        Log.i("GetAFWA", "Problems ");
                        Log.i("GetAFWA", "ccode " + ccode);
                        Log.i("GetAFWA", "chapter " + chapter);
                        Log.i("GetAFWA", "week " + week);
                        Log.i("GetAFWA", "assNr " + assNr);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String file_url) {

        }
    }

    private class GetReadAssignments extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String courseCode = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseCode", courseCode));

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(read_URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    Log.d(jsonStr, "jsonStr " + jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray labAssignments = jsonObj.getJSONArray("readAssignments");
                    // looping through All Contacts
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String ccode = c.getString("ccode");
                        String chapter = c.getString("chapter");
                        String week = c.getString("week");
                        String startPage = c.getString("startPage");
                        String endPage = c.getString("endPage");
                        int id = AssignmentID.getID();

                        addToDatabase(courseCode, id, chapter, Integer.parseInt(week), Integer.parseInt(startPage), Integer.parseInt(endPage));

                        Log.i("GetAFWA", "Readasses ");
                        Log.i("GetAFWA", "ccode " + ccode);
                        Log.i("GetAFWA", "chapter " + chapter);
                        Log.i("GetAFWA", "week " + week);
                        Log.i("GetAFWA", "startPage " + startPage);
                        Log.i("GetAFWA", "endPage " + endPage);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String file_url) {

        }
    }

    private class GetObligatoryAssignments extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String courseCode = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseCode", courseCode));

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(obligatory_URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    Log.d(jsonStr, "jsonStr " + jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray labAssignments = jsonObj.getJSONArray("obligatoryAssignments");
                    // looping through All Contacts
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String ccode = c.getString("ccode");
                        String type = c.getString("type");
                        String date = c.getString("date");
                        int id = AssignmentID.getID();

                        Log.i("GetAFWA", "Oblasses ");
                        Log.i("GetAFWA", "ccode " + ccode);
                        Log.i("GetAFWA", "type " + type);
                        Log.i("GetAFWA", "date " + date);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String file_url) {

        }
    }


    public void addToDatabase(String courseCode, AssignmentType type, int id, String chapterOrNumber, int week, String assignment) {
        switch (type){
            case PROBLEM:
                ProblemAssignmentsDBAdapter problemDB = new ProblemAssignmentsDBAdapter(context);
                problemDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            case LAB:
                LabAssignmentsDBAdapter labDB = new LabAssignmentsDBAdapter(context);
                labDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            case HANDIN:
                HandInAssignmentsDBAdapter handInDB = new HandInAssignmentsDBAdapter(context);
                handInDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            default:
                //do nothing
        }
    }


        public void addToDatabase(String courseCode, int id, String chapter, int week, int startPage, int endPage){
        ReadAssignmentsDBAdapter readDB = new ReadAssignmentsDBAdapter(context);
        readDB.insertAssignment(courseCode, id, chapter, week, startPage, endPage, AssignmentStatus.UNDONE);
    }

    //TODO insert obligatory
    public void addToDatabase(String courseCode, int id, String type, String date) {
    }
}





