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
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.ServiceHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;

/**
 * Created by Soyapanda on 03/07/15.
 */
public class GetCoursesFromWebActivity extends ActionBarActivity {

    private String courseName;
    private String courseCode;
    private String URL_CONNECTION = "http://studiecoachchalmers.se/php/getCourses.php";

    private CoursesDBAdapter coursesDBAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcoursesfromweb);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Lägg till kurs");
        coursesDBAdapter = new CoursesDBAdapter(this);

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));

        new GetAllCourses().execute();
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


    private class GetAllCourses extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            Log.i("GetCoursesFromWebActivity" ,"in doInBackgroun");

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(URL_CONNECTION, ServiceHandler.POST);
            Log.i("GetCoursesFromWebActivity" ,"jsonStr: " + jsonStr);
            if (jsonStr != null) {
                try {
                    Log.d(jsonStr, "jsonStr");
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray coursesList = jsonObj.getJSONArray("courses");

                    // looping through All Contacts
                    for (int i = 0; i < coursesList.length(); i++) {
                        JSONObject c = coursesList.getJSONObject(i);

                        courseCode = c.getString("ccode");
                        courseName = c.getString("cname");

                        Log.i("GetCoursesFromWeb", "coursecode " + courseCode);
                        Log.i("GetCoursesFromWeb", "courseName " + courseName);
                        //Here they inserted to the database
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

            Log.i("GetCoursesFromWeb", "coursecode " + courseCode);
            Log.i("GetCoursesFromWeb", "courseName " + courseName);
            //Here they updated the view to the database

        }


    }


}





