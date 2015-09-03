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

package se.chalmers.datx02_15_36.studeraeffektivt.util.web;

import android.content.Context;
import android.database.Cursor;
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

import se.chalmers.datx02_15_36.studeraeffektivt.activity.CourseDetailedInfoActivity;
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

    //Results
    long addLabResult = 1L;
    long addHandInResult = 1L;
    long addReadResult = 1L;
    long addProblemResult = 1L;
    long addObligatoryResult = 1L;

    long getLabResult = 1L;
    long getHandInResult = 1L;
    long getReadResult = 1L;
    long getProblemResult = 1L;
    long getObligatoryResult = 1L;

    private Context context;
    private CourseDetailedInfoActivity courseDetInfoAct;

    //For testing
    String courseCode;

    public GetAssignmentsFromWeb(Context context){
        this.context = context;
        initDBS();
    }

    public GetAssignmentsFromWeb(CourseDetailedInfoActivity context){
        this.courseDetInfoAct = context;
        this.context = context;
        initDBS();
    }

    public void addAssignmentsFromWeb(String courseCode){
        new GetHandInAssignments().execute(courseCode);
        new GetLabAssignments().execute(courseCode);
        new GetProblemAssignments().execute(courseCode);
        new GetReadAssignments().execute(courseCode);
        new GetObligatoryAssignments().execute(courseCode);

        if (allAssignmentsAdded() && new ConnectionDetector(context).isConnectingToInternet()){
            Toast.makeText(context, "Kursen har uppdaterats med nya uppgifter", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Det gick inte att hämta uppgifter till kursen. Kontrollera att du har internet påslagen.", Toast.LENGTH_SHORT).show();
        }
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
            courseCode = args[0];

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
                    handInDB.deleteUndoneAssignments(courseCode);

                    // looping through all obligatory assignments in the web
                    for (int i = 0; i < handInAssignments.length(); i++) {
                        Cursor cursor = handInDB.getDoneAssignments(courseCode);
                        JSONObject c = handInAssignments.getJSONObject(i);
                        String nr = c.getString("nr");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        String date = c.getString("date");
                        int id = AssignmentID.getID();

                        boolean checkIfExists = handInDB.checkIfExists(courseCode,nr,week,assNr,date);
                            Log.d("checkIfExists",String.valueOf(checkIfExists));
                        if(!checkIfExists) {
                            addHandInResult = addToDatabase(courseCode, AssignmentType.HANDIN, id, nr, Integer.parseInt(week), date, assNr);
                            if (addHandInResult < 1L) {
                                Toast.makeText(context, "Det gick inte att lägga till inlämningsuppgifter i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                            }
                        }
                        Log.i("endOfIf", "checkIfExists = " + checkIfExists);
                        } } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getHandInResult = 0L;
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }


            return null;
        }


        protected void onPostExecute(String file_url) {

            courseDetInfoAct.updateComponents();

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
                    labDB.deleteUndoneAssignments(courseCode);

                    // looping through all obligatory assignments in the web
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String nr = c.getString("nr");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        String date = c.getString("date");
                        int id = AssignmentID.getID();

                        if (!labDB.checkIfExists(courseCode, nr, week, assNr, date)) {
                            addLabResult = addToDatabase(courseCode, AssignmentType.LAB, id, nr, Integer.parseInt(week), date, assNr);

                            if (addLabResult < 1L) {
                                Toast.makeText(context, "Det gick att lägga till labbuppgifterna i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getLabResult = 0L;
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            courseDetInfoAct.updateComponents();

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
                    problemDB.deleteUndoneAssignments(courseCode);

                    // looping through all obligatory assignments in the web
                    for (int i = 0; i < labAssignments.length(); i++) {
                        JSONObject c = labAssignments.getJSONObject(i);

                        String chapter = c.getString("chapter");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        int id = AssignmentID.getID();

                        if(!problemDB.checkIfExists(courseCode,assNr,week,chapter))

                       addProblemResult = addToDatabase(courseCode, AssignmentType.PROBLEM, id, chapter, Integer.parseInt(week), "", assNr);

                        if(addProblemResult < 1L){
                            Toast.makeText(context, "Det gick inte att lägga till problemlösningsuppgifterna i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getProblemResult = 0L;
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
                    JSONArray readAssignments = jsonObj.getJSONArray("readAssignments");

                    //removing the assignments
                    readDB.deleteUndoneAssignments(courseCode);

                    // looping through all obligatory assignments in the web
                     for (int i = 0; i < readAssignments.length(); i++) {
                         JSONObject c = readAssignments.getJSONObject(i);

                         String chapter = c.getString("chapter");
                         String week = c.getString("week");
                         String startPage = c.getString("startPage");
                         String endPage = c.getString("endPage");
                         int id = AssignmentID.getID();

                         if (!readDB.checkIfExists(courseCode, startPage, endPage, chapter, week)) {

                             addReadResult = addToDatabase(courseCode, id, chapter, Integer.parseInt(week), Integer.parseInt(startPage), Integer.parseInt(endPage));

                             if (addReadResult < 1L) {
                                 Toast.makeText(context, "Det gick inte att lägga till läsanvisningarna i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                             }
                         }
                     }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getReadResult = 0L;
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            courseDetInfoAct.updateComponents();

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
                    JSONArray obligatoryAssignments = jsonObj.getJSONArray("obligatoryAssignments");

                    //removing the assignments
                    coursesDB.deleteObligatories(courseCode);

                    // looping through all obligatory assignments in the web
                    for (int i = 0; i < obligatoryAssignments.length(); i++) {
                        JSONObject c = obligatoryAssignments.getJSONObject(i);

                        String type = c.getString("type");
                        String date = c.getString("date");
                        int id = AssignmentID.getID();

                        if(!coursesDB.checkIfExists(courseCode,type,date)){

                        addObligatoryResult = addToDatabase(courseCode, id, type, date, AssignmentStatus.UNDONE);

                        if(addObligatoryResult < 1L){
                            Toast.makeText(context, "Det gick inte att lägga till de obligatoriska momenten i kursen " + courseCode, Toast.LENGTH_SHORT).show();
                        }
                    }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getObligatoryResult = 0L;
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            courseDetInfoAct.updateComponents();

        }
    }


    public long addToDatabase(String courseCode, AssignmentType type, int id, String chapterOrNumber, int week, String date, String assignment) {
        long result = 0L;
        switch (type){
            case PROBLEM:
                result = problemDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            case LAB:
                result = labDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, date, AssignmentStatus.UNDONE);
                break;

            case HANDIN:
                result = handInDB.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, date, AssignmentStatus.UNDONE);
                break;

            default:
                //do nothing
        }
        return result;
    }

    private long addToDatabase(String courseCode, int id, String chapter, int week, int startPage, int endPage){
        return readDB.insertAssignment(courseCode, id, chapter, week, startPage, endPage, AssignmentStatus.UNDONE);
    }

    private long addToDatabase(String courseCode, int id, String type, String date, AssignmentStatus status) {
        return coursesDB.insertObligatory(courseCode, id, type, date, status);
    }

    private boolean allAssignmentsAdded(){
        return (getHandInResult + getProblemResult + getObligatoryResult + getLabResult + getReadResult) > 4L;
    }
}





