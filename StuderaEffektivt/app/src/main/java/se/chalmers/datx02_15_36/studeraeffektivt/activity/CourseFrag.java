package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
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
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;


public class CourseFrag extends Fragment {

    private ListView listOfCourses;
    private Button addButton;
    private Button addButtonInner;
    private EditText nameEditText;
    private EditText codeEditText;
    public static List<Map<String, Course>> courseList = new ArrayList<Map<String, Course>>();
    SimpleAdapter simpleAdpt;
    LinearLayout popUp;
    private View view;
    private int selected;
    private ViewGroup container;
    private Bundle bundleFromPreviousFragment;
    private Bundle bundleToNextFragment;
    private int containerId;

    //The access point of the database.
    private DBAdapter dbAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_course, container, false);
        this.container = container;
        this.view = rootView;
        initComponents();
        bundleFromPreviousFragment = this.getArguments();
        containerId = bundleFromPreviousFragment.getInt("containerId");
        bundleToNextFragment = new Bundle();
        simpleAdpt = new SimpleAdapter(this.getActivity(), courseList, android.R.layout.simple_list_item_1, new String[]{"Courses"}, new int[]{android.R.id.text1});
        listOfCourses.setAdapter(simpleAdpt);




        //Create the database access point but check if the context is null first.
        if (getActivity() != null) {
            dbAdapter = new DBAdapter(getActivity());
        }
        showCourseList();

        return rootView;

    }

    /**
     * Init list with courses from database.
     */
    public void showCourseList() {
        courseList.clear();

        Cursor cursor = dbAdapter.getCourses();
        Log.d("DB", "cursor.getCount() är "+cursor.getCount());
        if (cursor.getCount() > 0){
            courseList.add(createCourse("Courses", new Course("Default Course", "DDD111"))); //Tas bort sedan.
            String ccode = "";
            String cname = "";
            while (cursor.moveToNext()) {
                ccode = cursor.getString(0);
                cname = cursor.getString(1);
                courseList.add(createCourse("Courses", new Course(cname, ccode)));
            }
        }else{
            courseList.add(createCourse("Courses", new Course("Default Course", "DDD111")));  //Ändra namn.

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
                if (id > 0 && getActivity() != null) {
                    showCourseList();

                    Toast toast = Toast.makeText(getActivity(), codeEditText.getText().toString() + " succesfully added", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (getActivity() != null) {
                    Toast toast = Toast.makeText(getActivity(), "Failed to add course" + codeEditText.getText().toString(), Toast.LENGTH_SHORT);
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
                bundleToNextFragment.putInt("containerId", ((ViewGroup) container.getParent()).getId());
                bundleToNextFragment.putInt("kurs", courseList.indexOf(courseMap));
                goToDetails(bundleToNextFragment);
            }
        });

    }

    public void goToDetails(Bundle bundle) {
        //Fragment fragment = new CourseDetailedInfoFrag();
        Fragment fragment = new StudyTaskFragment();

        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(containerId, fragment, "detailedcoursefragment");
        fragmentTransaction.hide(this);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void initComponents() {
        addButton = (Button) view.findViewById(R.id.addButton);
        listOfCourses = (ListView) view.findViewById(R.id.listView);
        popUp = (LinearLayout) view.findViewById(R.id.linLayout);
        addButtonInner = (Button) view.findViewById(R.id.addButtonInner);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        codeEditText = (EditText) view.findViewById(R.id.codeEditText);

        setAddButton();
        setAddButtonInner();
        setListOfCourses();
    }
}