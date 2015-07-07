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
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.view.CourseView;

/**
 * Created by SoyaPanda on 15-03-06.
 */
public class CourseDetailedInfoActivity extends ActionBarActivity {

    private boolean isActiveCourse;
    private boolean hasFetchedBefore = false;

    private boolean isInitialized;

    private String courseCode;
    private String courseName;
    private Menu menu;
    private MenuItem activeCourseItem;

    private CoursesDBAdapter coursesDBAdapter;
    private CourseView alertCreator;

    //View objects
    private TextView examLabel;
    private TextView miniexamLabel;
    private TextView labLabel;
    private TextView handinLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        courseName = getIntent().getStringExtra("CourseName");
        courseCode = getIntent().getStringExtra("CourseCode");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(courseName);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));

        if (this != null) {
            coursesDBAdapter = new CoursesDBAdapter(this);
        }

        isInitialized = true;

        String status = coursesDBAdapter.getCourseStatus(courseCode);
        isActiveCourse = (status.toLowerCase().equals("undone"));
        alertCreator = new CourseView();

        initComponents();
    }

    private void initComponents(){

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_course_details);

        //Only for testing
        Cursor cursor = coursesDBAdapter.getObligatories();
        while (cursor.moveToNext()){
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            Log.d("CoursePage", type+" "+date);
        }

        //Exam date
        RelativeLayout.LayoutParams paramsExam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        examLabel = new TextView(this);
        examLabel.setId(View.generateViewId());
        examLabel.setText("Tentamen: " + coursesDBAdapter.getExamDate(courseCode));
        Log.d("CoursePage", "tentadatum: " + coursesDBAdapter.getExamDate(courseCode));
        examLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

        layout.addView(examLabel);

        //Miniexams (duggor)
        Log.d("CoursePage", "duggor finns: "+coursesDBAdapter.hasMiniexams(courseCode));
        RelativeLayout.LayoutParams paramsMiniExam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(coursesDBAdapter.hasMiniexams(courseCode)){
            miniexamLabel = new TextView(this);
            miniexamLabel.setId(View.generateViewId());
            miniexamLabel.setText("Duggor");
            miniexamLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

            paramsMiniExam.addRule(RelativeLayout.BELOW, examLabel.getId());
            layout.addView(miniexamLabel, paramsMiniExam);
        }

        //Labs
        Log.d("CoursePage", "labbar finns: "+coursesDBAdapter.hasLabs(courseCode));
        RelativeLayout.LayoutParams paramsLabs = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(coursesDBAdapter.hasLabs(courseCode)){
            labLabel = new TextView(this);
            labLabel.setId(View.generateViewId());
            labLabel.setText("Laborationer");
            labLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

            if(miniexamLabel != null) {
                paramsLabs.addRule(RelativeLayout.BELOW, miniexamLabel.getId());
            }else{
                paramsLabs.addRule(RelativeLayout.BELOW, examLabel.getId());
            }
            layout.addView(labLabel, paramsLabs);
        }

        //Handins
        Log.d("CoursePage", "inluppg finns: "+coursesDBAdapter.hasHandins(courseCode));
        RelativeLayout.LayoutParams paramsHandin = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(coursesDBAdapter.hasHandins(courseCode)){
            handinLabel = new TextView(this);
            handinLabel.setId(View.generateViewId());
            handinLabel.setText("Inlämningsuppgifter");
            handinLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

            if(labLabel != null){
                paramsHandin.addRule(RelativeLayout.BELOW, labLabel.getId());
            }else if(miniexamLabel != null){
                paramsHandin.addRule(RelativeLayout.BELOW, miniexamLabel.getId());
            }else{
                paramsHandin.addRule(RelativeLayout.BELOW, examLabel.getId());
            }
            layout.addView(handinLabel, paramsHandin);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
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
            menu.getItem(4).setTitle("Markera som avslutad");
        }else {
            menu.getItem(4).setTitle("Markera som pågående");
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
            getAssignmentsFromWeb();
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

    public void onResume() {
        super.onResume();
    }

    private void openConfirmChangeStatusDialog() {
        AlertDialog.Builder builder = alertCreator.confirmCourseStatusView(courseName, isActiveCourse, this);

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
            coursesDBAdapter.setCourseUndone(courseCode);
            isActiveCourse = true;
            menu.getItem(3).setTitle("Markera som avslutad");
            String status = coursesDBAdapter.getCourseStatus(courseCode);

        } else {
            // mark as completed
            coursesDBAdapter.setCourseDone(courseCode);
            isActiveCourse = false;
            menu.getItem(3).setTitle("Markera som pågående");
            String status = coursesDBAdapter.getCourseStatus(courseCode);
        }

    }

    public void deleteCourse() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Är du säker på att du vill ta bort kursen? Uppgifter och statistik kommer att raderas!");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                coursesDBAdapter.deleteCourse(courseCode);
                     //   coursesDBAdapter.deleteCourseAssignmets(courseCode);
                //TODO remove all the assignments of the course too
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
        Intent i = new Intent(this, CourseAssignmentsActivity.class);
        i.putExtra("CourseCode", courseCode);
        i.putExtra("CourseName", courseName);
        startActivity(i);
    }

    public void goToAddTasks() {
        Intent i = new Intent(this, AddAssignmentActivity.class);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);
    }

    public void getAssignmentsFromWeb() {
        GetAssignmentsFromWeb getAssignmentsFromWeb = new GetAssignmentsFromWeb(this);
        getAssignmentsFromWeb.addAssignmentsFromWeb(courseCode);
        Toast.makeText(getBaseContext(), "Kursen har uppdaterats", Toast.LENGTH_LONG);
        //TODO: Kolla ifall nätverk finns när användaren klickar på uppdatera
        //Om inte, ge ett passande toast för det.
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
                long add = coursesDBAdapter.insertTimeOnCourse(courseCode, Integer.parseInt(value));
                Toast toast;
                if (add > 0) {

                    int mins = coursesDBAdapter.getTimeOnCourse(courseCode);
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
