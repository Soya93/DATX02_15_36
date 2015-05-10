package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.CourseDetailedInfoActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;


public class CourseActivity extends ActionBarActivity {

    private ListView listOfCourses;
    public static List<Map<String, Course>> courseList = new ArrayList<Map<String, Course>>();
    SimpleAdapter simpleAdpt;

    //The access point of the database.
    private DBAdapter dbAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initComponents();
        simpleAdpt = new SimpleAdapter(this, courseList, android.R.layout.simple_list_item_1, new String[]{"Courses"}, new int[]{android.R.id.text1});
        listOfCourses.setAdapter(simpleAdpt);

        getSupportActionBar().setTitle("Mina inaktiva Kurser");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.primaryColor)));

        //Create the database access point but check if the context is null first.
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }
        showCourseList();
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

        Cursor cursor = dbAdapter.getDoneCourses();
        Log.d("DB", "cursor.getCount() är "+cursor.getCount());
        if (cursor.getCount() > 0){
            String ccode = "";
            String cname = "";
            while (cursor.moveToNext()) {
                ccode = cursor.getString(0);
                cname = cursor.getString(1);
                courseList.add(createCourse("Courses", new Course(cname, ccode)));
            }
        }else{
            courseList.add(createCourse("Courses", new Course("inaktivera kurser gör du i den specifika kursen på föregående sida!", "Här hamnar dina inaktiva kurser, ")));
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