package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
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
    private Switch isActiveSwitch;
    private boolean hasFetchedBefore = false;


    private String courseCode;
    private String courseName;

    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        courseName = getIntent().getStringExtra("CourseName");
        courseCode = getIntent().getStringExtra("CourseCode");
        actionBar.setTitle(courseName);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.primaryColor)));
        //initFrag(getIntent().getStringExtra("ActivityTitle"));
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }

//        fillActivity(courseCode, courseName);
        initComponents();
        layoutWithinScrollViewOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);
        taskListfromWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutWithinScrollViewOfTasks.removeAllViews();
                layoutWithinScrollViewOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);
            }
        });

        TextView textView1 = new TextView(this);
        textView1.setText("Du har inte hämtat uppgifter från webben!");
        taskListfromWeb.addView(textView1);
        isActiveSwitch = (Switch) findViewById(R.id.isActiveSwitch);

        String status = dbAdapter.getCourseStatus(courseCode);

        Log.i("CourseDetailedInfo course status", status);

        isActiveSwitch.setChecked(status.toLowerCase().equals("undone"));
        if(isActiveSwitch.isChecked()){
            isActiveSwitch.setText("Pågående");
        }else {
            isActiveSwitch.setText("Avslutad");
        }

        isActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    isActiveSwitch.setText("Pågående");
                    dbAdapter.setCourseUndone(courseCode);
                    String status = dbAdapter.getCourseStatus(courseCode);
                    Log.i("CourseDetailedInfo set course undone", status);

                } else {
                    isActiveSwitch.setText("Avslutad");
                    dbAdapter.setCourseDone(courseCode);
                    String status = dbAdapter.getCourseStatus(courseCode);
                    Log.i("CourseDetailedInfo set course done", status);
                }


            }
        });





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
                Log.i("detailedInfo", "click on switch " + v.getId());
                Log.i("detailedInfo", "click on switch " + isActiveSwitch.getId());

                if (v.getTag() == button1.getTag()) {
                    //delete course
                    deleteCourse(v);
                } else if (v.getTag() == button2.getTag()) {
                    chooseTimeOnCourseDialog();
                } else if (v.getTag() == button3.getTag()) {
                    //download tasks
                    getAssignmetsFromWeb(v);
                } else if (v.getTag() == button4.getTag()) {
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

        kursDetaljer = (TextView) findViewById(R.id.kursDetaljer);

        layoutWithinScrollViewOfTasks = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);
        taskListfromWeb = (FlowLayout) findViewById(R.id.taskListfromWeb);

    }

    public void deleteCourse(View v) {
        dbAdapter.deleteCourse(courseCode);
        Cursor cur = dbAdapter.getCourses();
        while(cur.moveToNext()) {
        }


        this.finish();
    }

    public void goToTasks(View v) {


        Intent i = new Intent(this, StudyTaskActivity.class);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);
    }

    public void getAssignmetsFromWeb(View v) {
        if(!hasFetchedBefore)
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
           hasFetchedBefore =true;
                    taskListfromWeb.removeAllViews();
                    Iterator it = assignmetsHashMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        StudyTask2 test = (StudyTask2) pair.getValue();

                       test.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               layoutWithinScrollViewOfTasks.removeAllViews();
                               layoutWithinScrollViewOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);
                           }
                       });
                        taskListfromWeb.addView(test,taskListfromWeb.getChildCount());

                            /*
                        taskListfromWeb.addTasksFromWeb(test.getIdNr(), test.getCourseCode(),
                                test.getChapter(), test.getWeek(), test.getTaskString(), test.getStartPage(),
                                test.getEndPage(), "UNDONE", "READ", dbAdapter);

                        it.remove(); // avoids a ConcurrentModificationException
                        */
                    }

            }


    }

    private void chooseTimeOnCourseDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Hur många timmar vill du lägga på kursen per vecka?");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setText("0");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Log.d("course", "add "+value+" minutes to "+courseCode);

                //add value
                long add = dbAdapter.insertTimeOnCourse(courseCode, Integer.parseInt(value));
                Toast toast;
                if(add > 0){

                    int mins = dbAdapter.getTimeOnCourse(courseCode);
                    toast = Toast.makeText(getBaseContext(), "Ditt mål är nu att lägga "+mins+" minuter på "+courseCode+" i veckan.", Toast.LENGTH_LONG);
                }else{
                    toast = Toast.makeText(getBaseContext(), "Det gick inte att lägga till.", Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }



}

