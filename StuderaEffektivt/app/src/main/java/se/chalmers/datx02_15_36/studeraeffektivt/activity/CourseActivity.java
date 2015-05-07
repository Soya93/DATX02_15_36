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
    private Button addButton;
    private Button addButtonInner;
    private EditText nameEditText;
    private EditText codeEditText;
    public static List<Map<String, Course>> courseList = new ArrayList<Map<String, Course>>();
    SimpleAdapter simpleAdpt;
    LinearLayout popUp;
    private int selected;
    private ViewGroup container;
    private Bundle bundleFromPreviousFragment;
    private Bundle bundleToNextFragment;
    private int containerId;

    //The access point of the database.
    private DBAdapter dbAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initComponents();
        simpleAdpt = new SimpleAdapter(this, courseList, android.R.layout.simple_list_item_1, new String[]{"Courses"}, new int[]{android.R.id.text1});
        listOfCourses.setAdapter(simpleAdpt);

        getSupportActionBar().setTitle("Mina Kurser");
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
            courseList.add(createCourse("Courses", new Course("lägg till genom att klicka på knappen ovan!", "Du har för närvarande inga kurser")));
            listOfCourses.setEnabled(false);
        }
    }

    private HashMap<String, Course> createCourse(String key, Course course) {
        HashMap<String, Course> newCourse = new HashMap<String, Course>();
        newCourse.put(key, course);

        return newCourse;
    }

    public void setAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popUp.getVisibility() == View.INVISIBLE) {
                    popUp.setVisibility(View.VISIBLE);
                    addButton.setText("Show");
                } else {
                    popUp.setVisibility(View.INVISIBLE);
                    addButton.setText("Lägg till kurs");
                }
            }
        });

    }

    public void setAddButtonInner() {
        addButtonInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.setVisibility(View.INVISIBLE);
                addButton.setText("Lägg till kurs");

                long id = dbAdapter.insertCourse(codeEditText.getText().toString(), nameEditText.getText().toString());
                Log.d("DB", "id: " + id);
                if (id > 0 && this != null) {
                    showCourseList();

                    Toast toast = Toast.makeText(getApplicationContext(), codeEditText.getText().toString() + " succesfully added", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (this != null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Failed to add course" + codeEditText.getText().toString(), Toast.LENGTH_SHORT);
                    toast.show();
                }

            }

        });
    }

    public void setListOfCourses() {
        listOfCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                HashMap courseMap = (HashMap) parent.getItemAtPosition(position);
                Course course1 = (Course) courseMap.get("Courses");
                selected = (int) courseList.indexOf(courseMap);
                goToDetails(course1);
            }
        });

    }

    public void goToDetails(Course course) {

        Intent intent = new Intent(this, CourseDetailedInfoActivity.class);
        intent.putExtra("CourseCode", course.getCourseCode());
        intent.putExtra("CourseName", course.getCourseName());
        startActivity(intent);

        /*Fragment fragment = new CourseDetailedInfoFrag();
        //Fragment fragment = new StudyTaskFragment();

        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(containerId, fragment, "detailedcoursefragment");
        fragmentTransaction.hide(this);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
    }

    public void initComponents() {
        addButton = (Button) findViewById(R.id.addButton);
        listOfCourses = (ListView) findViewById(R.id.listView);
        popUp = (LinearLayout) findViewById(R.id.linLayout);
        addButtonInner = (Button) findViewById(R.id.addButtonInner);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        codeEditText = (EditText) findViewById(R.id.codeEditText);

        setAddButton();
        setAddButtonInner();
        setListOfCourses();
    }
}