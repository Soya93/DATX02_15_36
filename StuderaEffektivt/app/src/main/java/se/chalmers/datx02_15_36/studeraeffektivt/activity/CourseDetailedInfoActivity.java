package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

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
    private String URL_CONNECTION = "http://studiecoachchalmers.se/getassignmets2.php";
    private HashMap<Integer, StudyTask2> assignmetsHashMap = new HashMap<Integer, StudyTask2>();
    private View.OnClickListener fabHandler;
    private FloatingActionButton actionButton;
    private SubActionButton button1;
    private SubActionButton button2;
    private SubActionButton button3;
    private SubActionButton button4;


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
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }
        layoutWithinScrollViewOfTasks = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);
        layoutWithinScrollViewOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);
//        fillActivity(courseCode, courseName);

        // listener for FAB menu
        FloatingActionMenu.MenuStateChangeListener myFABHandler = new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
            }
        };

        //Handler for submenu items
        fabHandler = new View.OnClickListener() {

            public void onClick(View v) {
                if (v.getTag() == button1.getTag()) {
                    //delete course
                } else if (v.getTag() == button2.getTag()) {
                    //set time
                } else if (v.getTag() == button3.getTag()) {
                    //download tasks
                    getAssignmetsFromWeb(v);
                } else {
                    //Add tasks
                   goToTasks(v);
                }

            }
        };

        //Create menu
        ImageView icon = new ImageView(this); // Create an icon
        Drawable moreIcon = getResources().getDrawable( R.drawable.ic_navigation_more_vert).mutate();
        moreIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP);
        icon.setImageDrawable(moreIcon);

        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView itemIcon1 = new ImageView(this);
        Drawable plusIcon = getResources().getDrawable( R.drawable.ic_action_delete).mutate();
        plusIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        itemIcon1.setImageDrawable(plusIcon);
        button1 = itemBuilder.setContentView(itemIcon1).build();
        button1.setTag(1);
        button1.setOnClickListener(fabHandler);

        ImageView itemIcon2 = new ImageView(this);
        Drawable repeatIcon = getResources().getDrawable( R.drawable.ic_device_access_time).mutate();
        repeatIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        itemIcon2.setImageDrawable(repeatIcon);
        button2 = itemBuilder.setContentView(itemIcon2).build();
        button2.setTag(2);
        button2.setOnClickListener(fabHandler);
        ImageView itemIcon3 = new ImageView(this);
        Drawable nbrOfVisibleDaysIcon = getResources().getDrawable( R.drawable.ic_action_download).mutate();
        nbrOfVisibleDaysIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        itemIcon3.setImageDrawable(nbrOfVisibleDaysIcon);
        button3 = itemBuilder.setContentView(itemIcon3).build();
        button3.setTag(3);
        button3.setOnClickListener(fabHandler);

        ImageView itemIcon4 = new ImageView(this);
        Drawable calendarsIcon = getResources().getDrawable( R.drawable.ic_content_add).mutate();
        calendarsIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        itemIcon4.setImageDrawable(calendarsIcon);
        button4 = itemBuilder.setContentView(itemIcon4).build();
        button4.setTag(4);
        button4.setOnClickListener(fabHandler);

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .addSubActionView(button4)
                .attachTo(actionButton)
                .build();

        actionMenu.setStateChangeListener(myFABHandler);

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


        fillActivity(courseCode, courseName);

        return rootView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_course_details, menu);
        return true;
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

    public void fillActivity(String courseCode, String courseName) {
        kursDetaljer.setText(courseCode + " - " + courseName);
    }

    public void initComponents() {
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


            String jsonStr = sh.makeServiceCall(URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    //Log.e("BAJS",jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray assignmetsList = jsonObj.getJSONArray("assignments");

                    // looping through All Contacts
                    for (int i = 0; i < assignmetsList.length(); i++) {
                        JSONObject c = assignmetsList.getJSONObject(i);

                        String returnedCod = c.getString("course");
                        Log.d("code", returnedCod);
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
                        assignmetsHashMap.put(studyTask2.getIdNr(), studyTask2);

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

            taskListfromWeb = (FlowLayout) findViewById(R.id.taskListfromWeb);
                    Iterator it = assignmetsHashMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        StudyTask2 test = (StudyTask2) pair.getValue();
                        taskListfromWeb.addTasksFromWeb(test.getIdNr(), test.getCourseCode(),
                                test.getChapter(), test.getWeek(), test.getTaskString(), test.getStartPage(),
                                test.getEndPage(), "UNDONE", "READ", dbAdapter);
                        it.remove(); // avoids a ConcurrentModificationException
                    }

            }


    }



}

