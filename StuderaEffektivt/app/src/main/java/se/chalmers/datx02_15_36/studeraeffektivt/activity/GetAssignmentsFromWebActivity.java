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
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;

/**
 * Created by alexandraback on 06/05/15.
 */
public class GetAssignmentsFromWebActivity extends ActionBarActivity {

    private String courseCode;
    private String HandIN_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getHandInAssignments.php";
    private String lab_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getLabAssignments.php";
    private String problem_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getProblemAssignments.php";
    private String read_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getReadAssignments.php";
    private String obligatory_URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/getObligatoryAssignments.php";



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getassignmetsfromweb);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        courseCode = "TDA623";

        actionBar.setTitle("Hämta uppgifter " + courseCode);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));

        new GetHandInAssignments().execute(courseCode);
        new GetLabAssignments().execute(courseCode);
        new GetProblemAssignments().execute(courseCode);
        new GetReadAssignments().execute(courseCode);
        new GetObligatoryAssignments().execute(courseCode);

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

                        String ccode = c.getString("ccode");
                        String nr = c.getString("nr");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");

                        Log.i("GetAFWA", "HandInAsses ");
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


}





