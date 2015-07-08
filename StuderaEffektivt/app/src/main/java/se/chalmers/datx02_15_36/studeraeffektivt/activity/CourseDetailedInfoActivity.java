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

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.GetAssignmentsFromWeb;
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
    private TextView timeOnCourseLabel;
    private TextView miniexamLabel;
    private TextView labLabel;
    private TextView handinLabel;

    //Last ids in lists
    private int lastMiniExamId = -1;
    private int lastLabId = -1;
    private int lastHandinId = -1;

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

        addExam(layout);
        addTimeOnCourse(layout);

        if(coursesDBAdapter.hasMiniexams(courseCode)){
            addMiniexams(layout);
        }

        if(coursesDBAdapter.hasLabs(courseCode)){
            addLabs(layout);
        }

        if(coursesDBAdapter.hasHandins(courseCode)){
            addHandins(layout);
        }
    }

    private void addTimeOnCourse(RelativeLayout layout) {
        RelativeLayout.LayoutParams paramsTOC = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        timeOnCourseLabel = new TextView(this);
        timeOnCourseLabel.setId(View.generateViewId());
        int timeOnCourse = coursesDBAdapter.getTimeOnCourse(courseCode);
        timeOnCourseLabel.setText("Timmar per vecka: " + timeOnCourse);
        timeOnCourseLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

        //Padding bottom
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (25*scale + 0.5f);
        timeOnCourseLabel.setPadding(0,0,0,dpAsPixels);

        paramsTOC.addRule(RelativeLayout.BELOW, examLabel.getId());
        layout.addView(timeOnCourseLabel, paramsTOC);
    }

    private void addExam(RelativeLayout layout) {
        examLabel = new TextView(this);
        examLabel.setId(View.generateViewId());
        examLabel.setText("Tentamen: " + coursesDBAdapter.getExamDate(courseCode));
        examLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

        //Padding top
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (18*scale + 0.5f);
        examLabel.setPadding(0,dpAsPixels,0,0);

        layout.addView(examLabel);
    }

    private void addHandins(RelativeLayout layout) {
        addHandinLabel(layout);

        //The handins
        Cursor handins = coursesDBAdapter.getObligatoryHandins(courseCode);
        Log.d("CoursePage", "antal handins: " + handins.getCount());
        TextView tv;
        RelativeLayout.LayoutParams params;
        while (handins.moveToNext()){
            tv = new TextView(this);
            int id = View.generateViewId();
            tv.setId(id);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            String date = handins.getString( handins.getColumnIndex("date") );
            tv.setText(date);

            if(lastHandinId == -1){
                params.addRule(RelativeLayout.BELOW, handinLabel.getId());
            }else{
                params.addRule(RelativeLayout.BELOW, lastHandinId);
            }
            lastHandinId = id;
            layout.addView(tv, params);
        }
    }

    private void addHandinLabel(RelativeLayout layout) {
        RelativeLayout.LayoutParams paramsHandin = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        handinLabel = new TextView(this);
        handinLabel.setId(View.generateViewId());
        handinLabel.setText("Inlämningsuppgifter");
        handinLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

        if(labLabel != null){
            paramsHandin.addRule(RelativeLayout.BELOW, lastLabId);
        }else if(miniexamLabel != null){
            paramsHandin.addRule(RelativeLayout.BELOW, lastMiniExamId);
        }else{
            paramsHandin.addRule(RelativeLayout.BELOW, examLabel.getId());
        }
        layout.addView(handinLabel, paramsHandin);
    }

    private void addLabs(RelativeLayout layout) {
        addLabsLabel(layout);

        //The labs
        Cursor labs = coursesDBAdapter.getObligatoryLabs(courseCode);
        Log.d("CoursePage", "antal labbar: " + labs.getCount());
        TextView tv;
        RelativeLayout.LayoutParams params;
        while (labs.moveToNext()){
            tv = new TextView(this);
            int id = View.generateViewId();
            tv.setId(id);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            String date = labs.getString( labs.getColumnIndex("date") );
            tv.setText(date);

            if(lastLabId == -1){
                params.addRule(RelativeLayout.BELOW, labLabel.getId());
            }else{
                params.addRule(RelativeLayout.BELOW, lastLabId);
            }
            lastLabId = id;
            layout.addView(tv, params);
        }
    }

    private void addLabsLabel(RelativeLayout layout) {
        RelativeLayout.LayoutParams paramsLabs = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //The label
        labLabel = new TextView(this);
        labLabel.setId(View.generateViewId());
        labLabel.setText("Laborationer");
        labLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

        if(miniexamLabel != null) {
            paramsLabs.addRule(RelativeLayout.BELOW, lastMiniExamId);
        }else{
            paramsLabs.addRule(RelativeLayout.BELOW, examLabel.getId());
        }
        layout.addView(labLabel, paramsLabs);
    }

    private void addMiniexams(RelativeLayout layout) {
        addMiniexamsLabel(layout);

        //The miniexams
        Cursor miniexams = coursesDBAdapter.getObligatoryMiniexams(courseCode);
        Log.d("CoursePage", "antal duggor: " + miniexams.getCount());
        TextView tv;
        RelativeLayout.LayoutParams params;
        while (miniexams.moveToNext()){
            tv = new TextView(this);
            int id = View.generateViewId();
            tv.setId(id);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            String date = miniexams.getString( miniexams.getColumnIndex("date") );
            tv.setText(date);

            if(lastMiniExamId == -1){
                params.addRule(RelativeLayout.BELOW, miniexamLabel.getId());
            }else{
                params.addRule(RelativeLayout.BELOW, lastMiniExamId);
            }
            lastMiniExamId = id;
            layout.addView(tv, params);
        }
    }

    private void addMiniexamsLabel(RelativeLayout layout) {
        RelativeLayout.LayoutParams paramsMiniExam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //The label
        miniexamLabel = new TextView(this);
        miniexamLabel.setId(View.generateViewId());
        miniexamLabel.setText("Duggor");
        miniexamLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

        paramsMiniExam.addRule(RelativeLayout.BELOW, timeOnCourseLabel.getId());
        layout.addView(miniexamLabel, paramsMiniExam);
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

        Cursor obl = coursesDBAdapter.getObligatories(courseCode);
        while (obl.moveToNext()){
            String type = obl.getString(obl.getColumnIndex("type"));
            String date = obl.getString(obl.getColumnIndex("date"));
            Log.d("updateCP", type+" "+date);
        }
        initComponents();
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
                    timeOnCourseLabel.setText("Timmar per vecka: "+mins);
                    toast = Toast.makeText(getBaseContext(), "Ditt mål är att lägga " + mins + " minuter på " + courseName + " i veckan.", Toast.LENGTH_LONG);
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
