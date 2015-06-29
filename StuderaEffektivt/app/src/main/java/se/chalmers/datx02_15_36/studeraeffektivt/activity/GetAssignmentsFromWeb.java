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
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.ServiceHandler;

/**
 * Created by alexandraback on 06/05/15.
 */
public class GetAssignmentsFromWeb extends ActionBarActivity {

    private String courseName;
    private String courseCode;
    private String URL_CONNECTION = "http://studiecoachchalmers.se/getassignmets2.php";

    private OldAssignmentsDBAdapter assDBAdapter;

    private OldFlowLayout taskListfromWebOther;
    private OldFlowLayout taskListfromWebRead;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getassignmetsfromweb);
        taskListfromWebOther = (OldFlowLayout) findViewById(R.id.taskListfromWebOther);
        taskListfromWebRead = (OldFlowLayout) findViewById(R.id.taskListfromWebRead);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        courseName = getIntent().getStringExtra("CourseName");
        courseCode = getIntent().getStringExtra("CourseCode");

        actionBar.setTitle("Hämta uppgifter " + courseName);
        if (this != null) {
            assDBAdapter = new OldAssignmentsDBAdapter(this);
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));


        new GetAllAssignments().execute(courseCode);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_event) {
            this.finish();
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStart() {
        super.onStart();


    }


    private class GetAllAssignments extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String courseCode = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseCode", courseCode));


            ServiceHandler sh = new ServiceHandler();
            int count = 0;


            String jsonStr = sh.makeServiceCall(URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    Log.d(jsonStr, "jsonStr");
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray assignmetsList = jsonObj.getJSONArray("assignments");

                    // looping through All Contacts
                    for (int i = 0; i < assignmetsList.length(); i++) {
                        JSONObject c = assignmetsList.getJSONObject(i);

                        String returnedCod = c.getString("course");

                        String chapter = c.getString("chapter");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        String startPage = c.getString("startPage");
                        String endPage = c.getString("endPage");
                        String type = c.getString("type");
                        String status = c.getString("status");

                        if (type.equals("OTHER")) {
                            taskListfromWebOther.addTasksFromWeb(returnedCod, Integer.parseInt(chapter),
                                    Integer.parseInt(week), assNr, Integer.parseInt(startPage), Integer.parseInt(endPage), status, "PROBLEM", assDBAdapter);
                            count++;
                            Log.d("count", String.valueOf(count));
                        }
                        else {
                            taskListfromWebRead.addTasksFromWeb(returnedCod, Integer.parseInt(chapter),
                                    Integer.parseInt(week), assNr, Integer.parseInt(startPage), Integer.parseInt(endPage), status, type, assDBAdapter);
                        }
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

            taskListfromWebOther.addOtherAssignmets();
            taskListfromWebRead.addReadAssignmets();

        }


    }


}





