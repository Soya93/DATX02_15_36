package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.StudyTaskActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask2;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;
import se.chalmers.datx02_15_36.studeraeffektivt.util.ServiceHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * Created by SoyaPanda on 15-03-06.
 */
public class CourseDetailedInfoActivity extends ActionBarActivity {
    private TextView kursDetaljer;
    private int selectedCourse;
    private View view;
    private Bundle bundleFromPreviousFragment;
    private Button taskButton;
    private FlowLayout layoutWithinScrollViewOfTasks;
    private FlowLayout taskListfromWeb;
    private String URL_CONNECTION = "http://192.168.1.6/getassignmets.php";
    private HashMap<String, StudyTask2> assignmetsHashMap = new HashMap<String, StudyTask2>();


    private String courseCode;
    private String courseName;

    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getIntent().getStringExtra("CourseName"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.primaryColor)));
        //initFrag(getIntent().getStringExtra("ActivityTitle"));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        View rootView = inflater.inflate(R.layout.activity_course_details, container, false);
        this.view = rootView;

        initComponents();

        //bundleFromPreviousFragment = this.getArguments();
        //selectedCourse = bundleFromPreviousFragment.getInt("kurs");
        //courseCode = bundleFromPreviousFragment.getString("CourseCode");
        //courseName = bundleFromPreviousFragment.getString("CourseName");
        //Course course = CourseFrag.courseList.get(selectedCourse).get("Courses");

        //Create the database access point but check if the context is null first.
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }

        layoutWithinScrollViewOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);

        fillActivity(courseCode, courseName);

        return rootView;
    }

    public void fillActivity(String courseCode, String courseName) {
        kursDetaljer.setText(courseCode + " - " + courseName);
    }

    public void initComponents() {
        taskButton = (Button) view.findViewById(R.id.taskButton);
        taskButton.setOnClickListener(myOnlyhandler);

        kursDetaljer = (TextView) view.findViewById(R.id.kursDetaljer);

        layoutWithinScrollViewOfTasks = (FlowLayout) view.findViewById(R.id.layoutWithinScrollViewOfTasks);

    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            goToTasks((Button) v);

        }
    };

    public void goToTasks(View v) {

        Intent i = new Intent(this, StudyTaskActivity.class);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);

    }

    public void getAssignmetsFromWeb(View v) {
        new GetAllAssignments().execute(courseCode);
    }

    private class GetAllAssignments extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(String... args) {
            String courseCode = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseCode", courseCode));



            ServiceHandler sh = new ServiceHandler();


            String jsonStr = sh.makeServiceCall(URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    Log.e("BAJS",jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray assignmetsList = jsonObj.getJSONArray("assignmets");

                    // looping through All Contacts
                    for (int i = 0; i < assignmetsList.length(); i++) {
                        JSONObject c = assignmetsList.getJSONObject(i);

                        String returnedCod = c.getString("courseCode");
                        String chapter = c.getString("chapter");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        String startPage = c.getString("startPage");
                        String endPage = c.getString("endPage");
                        String type = c.getString("type");
                        String status = c.getString("status");
                        AssignmentType status1;
                        if (status.equals("READ")) {
                            status1 = AssignmentType.READ;
                        } else {
                            status1 = AssignmentType.OTHER;

                        }
                        StudyTask2 studyTask2 = new StudyTask2(getBaseContext(), returnedCod, Integer.parseInt(chapter),
                                Integer.parseInt(week), assNr, Integer.parseInt(startPage), Integer.parseInt(endPage), dbAdapter, status1
                                , AssignmentStatus.UNDONE);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute() {

            Iterator it = assignmetsHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                StudyTask2 test = (StudyTask2) pair.getValue();
                taskListfromWeb.addTasksFromWeb(test.getIdNr(),test.getCourseCode(),
                        test.getChapter(),test.getWeek(),test.getTaskString(),test.getStartPage(),
                        test.getEndPage(),"UNDONE", "READ",dbAdapter);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }


    }


}

