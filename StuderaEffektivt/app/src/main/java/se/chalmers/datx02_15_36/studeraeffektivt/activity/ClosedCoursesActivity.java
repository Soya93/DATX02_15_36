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

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;


public class ClosedCoursesActivity extends ActionBarActivity {

    private ListView listOfCourses;
    public static List<Map<String, Course>> courseList = new ArrayList<Map<String, Course>>();
    public SimpleAdapter simpleAdpt;

    //The access point of the database.
    private CoursesDBAdapter coursesDBAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_courses);
        initComponents();
        simpleAdpt = new SimpleAdapter(this, courseList, android.R.layout.simple_list_item_1, new String[]{"Courses"}, new int[]{android.R.id.text1});
        listOfCourses.setAdapter(simpleAdpt);

        getSupportActionBar().setTitle("Mina avslutade kurser");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));

        //Create the database access point but check if the context is null first.
        if (this != null) {
            coursesDBAdapter = new CoursesDBAdapter(this);
        }
        showCourseList();
    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first
        showCourseList();
        Log.i("avslutade kurser", "onresume");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Init list with courses from database.
     */
    public void showCourseList() {
        courseList.clear();
        Log.d("courseList", "cursorList.size är "+courseList.size());
        Cursor cursor = coursesDBAdapter.getDoneCourses();
        Log.d("DB", "cursor.getCount() är "+cursor.getCount());
        if (cursor.getCount() > 0){
            String ccode = "";
            String cname = "";
            while (cursor.moveToNext()) {
                ccode = cursor.getString(cursor.getColumnIndex(CoursesDBAdapter.COURSES__ccode));
                cname = cursor.getString(cursor.getColumnIndex(CoursesDBAdapter.COURSES_cname));
                courseList.add(createCourse("Courses", new Course(cname, ccode)));
            }
            simpleAdpt.notifyDataSetChanged();
        }else{
            courseList.add(createCourse("Courses", new Course("avsluta kurser gör du i den specifika kursen på föregående sida!", "Här hamnar dina avslutade kurser, ")));
            listOfCourses.setEnabled(false);
        }

    }

    private HashMap<String, Course> createCourse(String key, Course course) {
        HashMap<String, Course> newCourse = new HashMap<String, Course>();
        newCourse.put(key, course);

        return newCourse;
    }

    public void setListOfCourses() {
        listOfCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                HashMap courseMap = (HashMap) parent.getItemAtPosition(position);
                Course course1 = (Course) courseMap.get("Courses");
                goToDetails(course1);
            }
        });

    }

    public void goToDetails(Course course) {

        Intent intent = new Intent(this, CourseDetailedInfoActivity.class);
        intent.putExtra("CourseCode", course.getCourseCode());
        intent.putExtra("CourseName", course.getCourseName());
        startActivity(intent);

    }

    public void initComponents() {
        listOfCourses = (ListView) findViewById(R.id.listView);

        setListOfCourses();
    }
}