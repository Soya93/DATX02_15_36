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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.ServiceHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;

/**
 * Created by Soyapanda on 03/07/15.
 */
public class GetCoursesFromWebActivity extends ActionBarActivity {

    //Web connection
    private String courseName;
    private String courseCode;
    private String URL_CONNECTION = "http://studiecoachchalmers.se/php/getCourses.php";

    //Database connection
    private CoursesDBAdapter coursesDBAdapter;

    //Dialog
    private EditText editTextCoursecode;
    private EditText editTextCoursename;

    //List
    private ListView listViewCourses;
    public static List<Map<String, Course>> courseList = new ArrayList<Map<String, Course>>();
    public SimpleAdapter simpleAdpt;

    //ActionButton
    private ActionButton actionButton;

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

        listViewCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                HashMap courseMap = (HashMap) parent.getItemAtPosition(position);
                Course course = (Course) courseMap.get("Courses");
                showDialog(course.getCourseCode(), course.getCourseName());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(final String courseCode, final String courseName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.confirm_dialog, null);

        TextView eventNameLabel = (TextView) dialogView.findViewById(R.id.confirm_text);
        if (eventNameLabel != null) {
            eventNameLabel.setText("Lägg till " + courseCode + " " + courseName + "?");
        }
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Ska lägga till
                Log.d("getCW", "lägg till kurs från webb");
                long bool = coursesDBAdapter.insertCourse(courseCode, courseName);

                if(bool > 0){
                    Toast.makeText(getApplicationContext(), ""+courseCode+" "+courseName+" tillagd.", Toast.LENGTH_SHORT).show();
                    new GetAllCourses().execute();
                }else{
                    Toast.makeText(getApplicationContext(), "Ett fel uppstod.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
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

        actionButton = (ActionButton) findViewById(R.id.add_fab);
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

    public void onStart() {
        super.onStart();

    }


    private class GetAllCourses extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            courseList.clear();
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

                        //Update list here
                        if( !coursesDBAdapter.exists(courseCode) ){
                            addToList(courseCode, courseName);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                addToList("Hittade inga kurser att lägga till.", "");
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            simpleAdpt.notifyDataSetChanged();
        }
    }

    private void addToList(String courseCode, String courseName){
        HashMap<String, Course> mapForCourse = new HashMap<String, Course>();
        mapForCourse.put("Courses", new Course(courseName, courseCode));
        courseList.add(mapForCourse);
    }

    private long addToDB(String courseCode, String courseName){
        return coursesDBAdapter.insertCourse(courseCode, courseName);
    }

    public void addCourseDialog(){
        LayoutInflater inflater = this.getLayoutInflater();
        final Activity thisActivity = this;

        final AlertDialog d = new AlertDialog.Builder(this)
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
                            Toast toast = Toast.makeText(thisActivity, "Både kursnamn och kurskod måste fylls i!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            long result = addToDB(editTextCoursecode.getText().toString(), editTextCoursename.getText().toString());
                            if(result > 0) {
                                Toast toast = Toast.makeText(thisActivity, editTextCoursename.getText().toString() + " tillagd!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(thisActivity, editTextCoursename.getText().toString() + "s kurskod är upptagen!", Toast.LENGTH_SHORT);
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





