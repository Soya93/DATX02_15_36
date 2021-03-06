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
    private int selectedCourse;
    private View view;
    private Bundle bundleFromPreviousFragment;
    private FlowLayout layoutWithinScrollViewOfTasks;
    private FlowLayout layoutWithinScrollViewOfOther;
    private View.OnClickListener fabHandler;
    private FloatingActionButton actionButton;
    private SubActionButton button1;
    private SubActionButton button2;
    private SubActionButton button3;
    private SubActionButton button4;
    private Switch isActiveSwitch;
    private boolean hasFetchedBefore = false;

    private boolean isInitialized;

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

        isInitialized = true;

        isActiveSwitch = (Switch) findViewById(R.id.isActiveSwitch);

        String status = dbAdapter.getCourseStatus(courseCode);

     //   Log.i("CourseDetailedInfo course status", status);

        isActiveSwitch.setChecked(status.toLowerCase().equals("undone"));
        /*if(isActiveSwitch.isChecked()){
            isActiveSwitch.setText("Pågående");
        }else {
            isActiveSwitch.setText("Avslutad");
        }*/

        isActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    //isActiveSwitch.setText("Pågående");
                    dbAdapter.setCourseUndone(courseCode);
                    String status = dbAdapter.getCourseStatus(courseCode);
                   // Log.i("CourseDetailedInfo set course undone", status);

                } else {
                    //isActiveSwitch.setText("Avslutad");
                    dbAdapter.setCourseDone(courseCode);
                    String status = dbAdapter.getCourseStatus(courseCode);
                   // Log.i("CourseDetailedInfo set course done", status);
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
                    deleteCourse();
                } else if (v.getTag() == button2.getTag()) {
                    //Time to spend om course
                    chooseTimeOnCourseDialog();
                } else if (v.getTag() == button3.getTag()) {
                    //download tasks
                    getAssignmetsFromWeb();
                } else if (v.getTag() == button4.getTag()) {
                    //Add tasks
                    goToTasks();
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

    @Override
    public void onStart(){
        super.onStart();
        layoutWithinScrollViewOfTasks.removeAllViews();
        layoutWithinScrollViewOfOther.removeAllViews();
        layoutWithinScrollViewOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);
        layoutWithinScrollViewOfOther.addTasksFromDatabase(dbAdapter,courseCode,AssignmentType.OTHER);

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
        }else if( id == R.id.action_delete) {
            deleteCourse();
        }else if (id == R.id.action_time_on_course){
            chooseTimeOnCourseDialog();
        }else if (id == R.id.action_download){
            getAssignmetsFromWeb();
        }else if(id == R.id.action_add) {
            goToTasks();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initComponents() {

        layoutWithinScrollViewOfTasks = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);

        layoutWithinScrollViewOfOther = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfOther);

    }

    public void onResume() {
        super.onResume();
    }

    public void deleteCourse() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dbAdapter.deleteCourseAssignmets(courseCode);
                        dbAdapter.deleteCourse(courseCode);
                        dbAdapter.deleteTimeGoalCourse(courseCode);
                        Cursor cur = dbAdapter.getCourses();
                        while(cur.moveToNext()) {
                        }
                        Toast.makeText(getApplicationContext(), courseName + " borttagen",
                                Toast.LENGTH_LONG).show();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Är du säker på att du vill ta bort kursen? Uppgifter och statistik kommer att raderas!").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nej", dialogClickListener).show();
        }

    public void goToTasks() {
        Intent i = new Intent(this, StudyTaskActivity.class);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);
    }

    public void getAssignmetsFromWeb() {
      Intent i = new Intent (this,GetAssignmetsFromWeb.class);
        i.putExtra("CourseName",courseName);
        i.putExtra("CourseCode",courseCode);
        startActivity(i);


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
