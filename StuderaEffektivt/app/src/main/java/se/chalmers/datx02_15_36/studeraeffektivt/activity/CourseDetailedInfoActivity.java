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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.view.CourseView;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * Created by SoyaPanda on 15-03-06.
 */
public class CourseDetailedInfoActivity extends ActionBarActivity {

    private FlowLayout layoutWithinScrollViewOfTasks;
    private FlowLayout layoutWithinScrollViewOfOther;

    private boolean isActiveCourse;
    private boolean hasFetchedBefore = false;

    private boolean isInitialized;

    private String courseCode;
    private String courseName;
    private Menu menu;
    private MenuItem activeCourseItem;


    private DBAdapter dbAdapter;
    private CourseView courseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        courseName = getIntent().getStringExtra("CourseName");
        courseCode = getIntent().getStringExtra("CourseCode");
        actionBar.setTitle(courseName);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));
        //initFrag(getIntent().getStringExtra("ActivityTitle"));
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }

//        fillActivity(courseCode, courseName);
        initComponents();

        isInitialized = true;

        String status = dbAdapter.getCourseStatus(courseCode);

        isActiveCourse = (status.toLowerCase().equals("undone"));
        courseView = new CourseView();

    }


    @Override
    public void onStart() {
        super.onStart();
        layoutWithinScrollViewOfTasks.removeAllViews();
        layoutWithinScrollViewOfOther.removeAllViews();
        layoutWithinScrollViewOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);
        layoutWithinScrollViewOfOther.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.OTHER);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_details, menu);
        this.menu = menu;
        updateText();
        return true;
    }

    private void updateText(){
        if(isActiveCourse){
            menu.getItem(3).setTitle("Markera som avslutad");
        }else {
            menu.getItem(3).setTitle("Markera som pågående");
        }
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
        } else if (id == R.id.action_delete) {
            deleteCourse();
        } else if (id == R.id.action_time_on_course) {
            chooseTimeOnCourseDialog();
        } else if (id == R.id.action_download) {
            getAssignmetsFromWeb();
        } else if (id == R.id.action_add) {
            goToAddTasks();
        } else if (id == R.id.action_activate) {
            openConfirmChangeStatusDialog();
        } else if (id == R.id.action_tasks) {
            goToCourseTasks();
        }
        updateText();

        return super.onOptionsItemSelected(item);
    }

    public void initComponents() {

        layoutWithinScrollViewOfTasks = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);

        layoutWithinScrollViewOfOther = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfOther);

    }

    public void onResume() {
        super.onResume();
    }

    private void openConfirmChangeStatusDialog() {
        AlertDialog.Builder builder = courseView.confirmCourseStatusView(courseName, isActiveCourse, this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                changeStatus();
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

    private void changeStatus() {
        if (!isActiveCourse) {
            // mark as ongoing
            dbAdapter.setCourseUndone(courseCode);
            isActiveCourse = true;
            menu.getItem(3).setTitle("Markera som avslutad");
            String status = dbAdapter.getCourseStatus(courseCode);

        } else {
            // mark as completed
            dbAdapter.setCourseDone(courseCode);
            isActiveCourse = false;
            menu.getItem(3).setTitle("Markera som pågående");
            String status = dbAdapter.getCourseStatus(courseCode);
        }

    }

    public void deleteCourse() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Är du säker på att du vill ta bort kursen? Uppgifter och statistik kommer att raderas!");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dbAdapter.deleteCourse(courseCode);
                     //   dbAdapter.deleteCourseAssignmets(courseCode);
                        Toast.makeText(getApplicationContext(), courseName + " borttagen",
                                Toast.LENGTH_LONG).show();
                        finish();
            }
        });
           builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
                   //No button clicked
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
    }

    public void goToCourseTasks() {
        Intent i = new Intent(this, CourseTasksActivity.class);
        i.putExtra("CourseCode", courseCode);
        i.putExtra("CourseName", courseName);
        startActivity(i);
    }

    public void goToAddTasks() {
        Intent i = new Intent(this, StudyTaskActivity.class);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);
    }

    public void getAssignmetsFromWeb() {
        Intent i = new Intent(this, GetAssignmentsFromWeb.class);
        i.putExtra("CourseName", courseName);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);
    }


    private void chooseTimeOnCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Hur många timmar vill du lägga på kursen per vecka?");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setText("0");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Log.d("course", "add " + value + " minutes to " + courseCode);

                //add value
                long add = dbAdapter.insertTimeOnCourse(courseCode, Integer.parseInt(value));
                Toast toast;
                if (add > 0) {

                    int mins = dbAdapter.getTimeOnCourse(courseCode);
                    toast = Toast.makeText(getBaseContext(), "Ditt mål är nu att lägga " + mins + " minuter på " + courseCode + " i veckan.", Toast.LENGTH_LONG);
                } else {
                    toast = Toast.makeText(getBaseContext(), "Det gick inte att lägga till.", Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
    }
}
