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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.LabAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ProblemAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentID;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.ServiceHandler;

/**
 * Created by alexandraback on 06/05/15.
 * Updated by Soyapanda on 03/07/15.
 */
public class GetAssignmentsFromWeb {

    //URL Connections
    private String HandIN_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getHandInAssignments.php";
    private String lab_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getLabAssignments.php";
    private String problem_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getProblemAssignments.php";
    private String read_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getReadAssignments.php";
    private String obligatory_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getObligatoryAssignments.php";

    //DBAdapter
    private LabAssignmentsDBAdapter labDB;
    private HandInAssignmentsDBAdapter handInDB;
    private ReadAssignmentsDBAdapter readDB;
    private ProblemAssignmentsDBAdapter problemDB;
    private CoursesDBAdapter coursesDB;


    private Context context;

    public GetAssignmentsFromWeb(Context context){
        this.context = context;
        initDBS();
    }

    public void addAssignmentsFromWeb(String courseCode){
        new GetHandInAssignments().execute(courseCode);
        new GetLabAssignments().execute(courseCode);
        new GetProblemAssignments().execute(courseCode);
        new GetReadAssignments().execute(courseCode);
        new GetObligatoryAssignments().execute(courseCode);
    }

    private void initDBS(){
        problemDB = new ProblemAssignmentsDBAdapter(context);
        labDB = new LabAssignmentsDBAdapter(context);
        handInDB = new HandInAssignmentsDBAdapter(context);
        readDB = new ReadAssignmentsDBAdapter(context);
        coursesDB = new CoursesDBAdapter(context);
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

                    //removing the assignments
                    handInDB.deleteAssignments(courseCode);

                    // looping through all obligatory assignments in the web
                    for (int i = 0; i < handInAssignments.length(); i++) {
                        JSONObject c = handInAssignments.getJSONObject(i);

                        String nr = c.getString("nr");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        int id = AssignmentID.getID();

                        long result = addToDatabase(courseCode, AssignmentType.HANDIN, id, nr, Integer.parseInt(week), assNr);
                        if(result < 0){
                            Toast.makeText(context, "Det gick inte att lägga till inlämningsuppgifter i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                        }

                        /*Log.i("GetAFWA", "HandInAsses ");
                        Log.i("GetAFWA", "nr " + nr);
                        Log.i("GetAFWA", "week " + week);
                        Log.i("GetAFWA", "assNr " + assNr);*/
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

                    //removing the assignments
                    labDB.deleteAssignments(courseCode);

                    // looping through all obligatory assignments in the web
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String nr = c.getString("nr");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        int id = AssignmentID.getID();

                        long result = addToDatabase(courseCode, AssignmentType.LAB, id, nr, Integer.parseInt(week), assNr);

                        if(result < 0){
                            Toast.makeText(context, "Det gick inte att lägga till labbuppgifterna i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                        }

                        /*Log.i("GetAFWA", "Labasses ");
                        Log.i("GetAFWA", "nr " + nr);
                        Log.i("GetAFWA", "week " + week);
                        Log.i("GetAFWA", "assNr " + assNr);*/
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

                    //removing the assignments
                    problemDB.deleteAssignments(courseCode);

                    // looping through all obligatory assignments in the web
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String chapter = c.getString("chapter");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        int id = AssignmentID.getID();

                        long result = addToDatabase(courseCode, AssignmentType.PROBLEM, id, chapter, Integer.parseInt(week), assNr);

                        if(result < 0){
                            Toast.makeText(context, "Det gick inte att lägga till övningsuppgiterna i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                        }

                        /*Log.i("GetAFWA", "Problems ");
                        Log.i("GetAFWA", "chapter " + chapter);
                        Log.i("GetAFWA", "week " + week);
                        Log.i("GetAFWA", "assNr " + assNr);*/
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

                    //removing the assignments
                    readDB.deleteAssignments(courseCode);

                    // looping through all obligatory assignments in the web
                     for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String chapter = c.getString("chapter");
                        String week = c.getString("week");
                        String startPage = c.getString("startPage");
                        String endPage = c.getString("endPage");
                        int id = AssignmentID.getID();

                        long result =  addToDatabase(courseCode, id, chapter, Integer.parseInt(week), Integer.parseInt(startPage), Integer.parseInt(endPage));

                        if(result < 0){
                            Toast.makeText(context, "Det gick inte att lägga till läsanvisningarna i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                        }

                       /* Log.i("GetAFWA", "Readasses ");
                        Log.i("GetAFWA", "chapter " + chapter);
                        Log.i("GetAFWA", "week " + week);
                        Log.i("GetAFWA", "startPage " + startPage);
                        Log.i("GetAFWA", "endPage " + endPage);*/

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

                    //removing the assignments
                    coursesDB.deleteObligatories(courseCode);

                    // looping through all obligatory assignments in the web
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String type = c.getString("type");
                        String date = c.getString("date");
                        int id = AssignmentID.getID();

                        long result = addToDatabase(courseCode, id, type, date, AssignmentStatus.UNDONE);

                        if(result < 0){
                            Toast.makeText(context, "Det gick inte att lägga till obligatoriska momenten i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                        }

                        /*Log.i("GetAFWA", "Oblasses ");
                        Log.i("GetAFWA", "type " + type);
                        Log.i("GetAFWA", "date " + date);*/
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


    public long addToDatabase(String courseCode, AssignmentType type, int id, String chapterOrNumber, int week, String assignment) {
        long result = -1L;
        switch (type){
            case PROBLEM:
                result = problemDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            case LAB:
                result = labDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                /*Cursor cur = labDB.getObligatories(courseCode);
                while(cur.moveToNext()){
                    Log.i("GetAsses", "Week: " + cur.getString(cur.getColumnIndex(LabAssignmentsDBAdapter.LABS_week)));
                    Log.i("GetAsses", "Nr: " + cur.getString(cur.getColumnIndex(LabAssignmentsDBAdapter.LABS_nr)));
                    Log.i("GetAsses", "AssNr: " + cur.getString(cur.getColumnIndex(LabAssignmentsDBAdapter.LABS_assNr)));
                }
                Log.i("GetAsses", "Cur null :(: ");*/
                break;

            case HANDIN:
                result = handInDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            default:
                //do nothing
        }
        return result;
    }


        public long addToDatabase(String courseCode, int id, String chapter, int week, int startPage, int endPage){
        return readDB.insertAssignment(courseCode, id, chapter, week, startPage, endPage, AssignmentStatus.UNDONE);
    }

    public long addToDatabase(String courseCode, int id, String type, String date, AssignmentStatus status) {
        return coursesDB.insertObligatory(courseCode, id, type, date, status);
    }
}





