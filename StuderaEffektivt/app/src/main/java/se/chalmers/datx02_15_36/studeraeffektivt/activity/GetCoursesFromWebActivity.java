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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
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
    private EditText editTextCoursecode;
    private EditText editTextCoursename;

    private ActionButton actionButton;
    private ListView listViewCourses;
    public static List<Map<String, Course>> courseList = new ArrayList<Map<String, Course>>();
    SimpleAdapter simpleAdpt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcoursesfromweb);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Lägg till kurs");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));

        coursesDBAdapter = new CoursesDBAdapter(this);

        initComponents();
        new GetAllCourses().execute();
    }

    private void initComponents(){
        listViewCourses = (ListView) findViewById(R.id.list_courses_from_web);
        simpleAdpt = new SimpleAdapter(this, courseList, android.R.layout.simple_list_item_1, new String[]{"Courses"}, new int[]{android.R.id.text1});
        listViewCourses.setAdapter(simpleAdpt);

        View.OnClickListener myButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getTag() == actionButton.getTag()) {
                    addCourseDialog();
                }
            }
        };

        actionButton = (ActionButton) findViewById(R.id.plus_fab); //TODO add it in the XML
        actionButton.setTag(1);
        actionButton.setOnClickListener(myButtonHandler);
        actionButton.setType(ActionButton.Type.DEFAULT);
        actionButton.setButtonColor(Color.parseColor("#ffffff"));
        actionButton.setButtonColorPressed(Color.parseColor("#ffd6d7d7"));
        actionButton.setShadowXOffset(0);
        actionButton.setShadowYOffset(0);
        Drawable calendarIcon = getResources().getDrawable(R.drawable.ic_cal2).mutate();
        calendarIcon.setColorFilter(Color.parseColor(Colors.primaryColor), PorterDuff.Mode.SRC_ATOP);
        actionButton.setImageDrawable(calendarIcon);
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
            //addToDB(courseCode,courseName);
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

            Log.i("GetCW" ,"in doInBackgroun");

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(URL_CONNECTION, ServiceHandler.POST);
            Log.i("GetCW" ,"jsonStr: " + jsonStr);
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

    private long addToDB(String courseCode, String courseName){
        return coursesDBAdapter.insertCourse(courseCode, courseName);
    }


    public void addCourseDialog(){
        LayoutInflater inflater = getParent().getLayoutInflater();

        final AlertDialog d = new AlertDialog.Builder(getParent())
                .setView(inflater.inflate(R.layout.add_course_dialog, null))
                .setTitle("Lägg till kurs")
                .setPositiveButton("Lägg till", null) //Set to null. We override the onclick
                .setNegativeButton("Avbryt", null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button positive = d.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(editTextCoursecode.getText().toString().trim().length() == 0 || editTextCoursename.getText().toString().trim().length() == 0){
                            Toast toast = Toast.makeText(getParent(), "Både kursnamn och kurskod måste fylls i!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            long result = addToDB(editTextCoursecode.getText().toString(), editTextCoursename.getText().toString());
                            if(result > 0) {
                                Toast toast = Toast.makeText(getParent(), editTextCoursename.getText().toString() + " tillagd!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getParent(), editTextCoursename.getText().toString() + "s kurskod är upptagen!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            d.dismiss();
                        }

                    }
                });

                Button negative = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });
            }
        });

        d.show();


        Button okButton = d.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
        Button cancelButton = d.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));

        editTextCoursecode = (EditText) d.findViewById(R.id.codeEditText);
        editTextCoursename = (EditText) d.findViewById(R.id.nameEditText);
    }


}





