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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.LabAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OtherAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ProblemAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.SessionsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.web.GetAssignmentsFromWeb;
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
    private LabAssignmentsDBAdapter labsDBAdapter;
    private HandInAssignmentsDBAdapter handinsDBAdapter;

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


        coursesDBAdapter = new CoursesDBAdapter(this);
        labsDBAdapter = new LabAssignmentsDBAdapter(this);
        handinsDBAdapter = new HandInAssignmentsDBAdapter(this);

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

        if (labsDBAdapter.getAssignments(courseCode).getCount() != 0){
            addLabs(layout);
        }
        if(handinsDBAdapter.getAssignments(courseCode).getCount() != 0){
            addHandins(layout);
        }

        if(coursesDBAdapter.hasMiniexams(courseCode)){
            addMiniexams(layout);
        }
    }

    public void updateComponents(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_course_details);

        layout.removeAllViews();

        lastMiniExamId = -1;
        lastLabId = -1;
        lastHandinId = -1;

        Log.d("updateCP", "har wipat allt");
        initComponents();
    }

    private void addTimeOnCourse(RelativeLayout layout) {
        RelativeLayout.LayoutParams paramsTOC = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        timeOnCourseLabel = new TextView(this);
        timeOnCourseLabel.setId(View.generateViewId());
        int timeOnCourse = (coursesDBAdapter.getTimeOnCourse(courseCode))/60;
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
        String exam = coursesDBAdapter.getExamDate(courseCode);
        exam = exam.equals("")? "Det finns inget angivet tentamensdatum": exam;

        examLabel.setText("Tentamen: " + exam);
        examLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);


        //Padding top
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (18*scale + 0.5f);
        examLabel.setPadding(0,dpAsPixels,0,0);

        layout.addView(examLabel);
    }

    private void addHandins(RelativeLayout layout) {
        Log.d("updateCP", "in add handins");
        addHandinLabel(layout);

        //The handins
        Cursor handins = handinsDBAdapter.getAssignments(courseCode);
        Log.d("CoursePage", "antal handins: " + handins.getCount());
        TextView tv;
        RelativeLayout.LayoutParams params;
        //String nr = "";
        while (handins.moveToNext()){
            String date = handins.getString( handins.getColumnIndex("date") );
            String nr = handins.getString( handins.getColumnIndex("nr") );

            //Formatting the date and the handIn nr so that these will be the id of the textview
            int id = 0;
            try {
                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                String convertedText = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(tradeDate);
                String dateAndNr = convertedText + nr;
                id = Integer.parseInt(dateAndNr);

            } catch (Exception e){
                System.err.println("Caught exception: " + e.getMessage());
            }

            if(layout.findViewById(id) == null){
                tv = new TextView(this);
                tv.setId(id);
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                tv.setText("Inlämning " + nr + ", Deadline: " + date);
                if(lastHandinId == -1){
                    params.addRule(RelativeLayout.BELOW, handinLabel.getId());
                }else{
                    params.addRule(RelativeLayout.BELOW, lastHandinId);
                }
                lastHandinId = id;
                layout.addView(tv, params);

            }
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
        }else{
            paramsHandin.addRule(RelativeLayout.BELOW, timeOnCourseLabel.getId());
        }
        layout.addView(handinLabel, paramsHandin);
    }

    private void addLabs(RelativeLayout layout) {
        addLabsLabel(layout);

        //The labs
        Cursor labs = labsDBAdapter.getAssignments(courseCode);
        TextView tv;
        RelativeLayout.LayoutParams params;
        while (labs.moveToNext()){

            String date = labs.getString( labs.getColumnIndex("date") );
            String nr = labs.getString(labs.getColumnIndex("nr"));

            //Formatting the date and the lab nr so that these will be the id of the textview
            int id = 0;
            try {
                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                String convertedText = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(tradeDate);
                String dateAndNr = convertedText + nr;
                id = Integer.parseInt(dateAndNr);

            } catch (Exception e){
                System.err.println("Caught exception: " + e.getMessage());
            }

            Log.i("CDIA", "lab id: " + id);
            if(layout.findViewById(id) == null){
                tv = new TextView(this);
                tv.setId(id);
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                tv.setText("Laboration " + nr + ", Deadline: " + date);

                if(lastLabId == -1){
                    params.addRule(RelativeLayout.BELOW, labLabel.getId());
                }else{
                    params.addRule(RelativeLayout.BELOW, lastLabId);
                }
                lastLabId = id;
                layout.addView(tv, params);
            }
        }
    }

    private void addLabsLabel(RelativeLayout layout) {
        RelativeLayout.LayoutParams paramsLabs = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //The label
        labLabel = new TextView(this);
        labLabel.setId(View.generateViewId());
        labLabel.setText("Laborationer");
        labLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);

        if(timeOnCourseLabel != null) {
            paramsLabs.addRule(RelativeLayout.BELOW, timeOnCourseLabel.getId());
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
            tv.setText("Datum: " + date);

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

        if(handinLabel != null){
            paramsMiniExam.addRule(RelativeLayout.BELOW, lastHandinId);
        }else if(labLabel != null){
            paramsMiniExam.addRule(RelativeLayout.BELOW, lastLabId);
        } else {
            paramsMiniExam.addRule(RelativeLayout.BELOW, timeOnCourseLabel.getId());
        }
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

        int i = 0;

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
            updateText();
        } else if (id == R.id.action_tasks) {
            goToCourseTasks();
        }

        updateComponents();

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        updateComponents();
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

                //Delete assignments
                new HandInAssignmentsDBAdapter(getApplicationContext()).deleteAssignments(courseCode);
                new ProblemAssignmentsDBAdapter(getApplicationContext()).deleteAssignments(courseCode);
                new LabAssignmentsDBAdapter(getApplicationContext()).deleteAssignments(courseCode);
                new ReadAssignmentsDBAdapter(getApplicationContext()).deleteAssignments(courseCode);
                new OtherAssignmentsDBAdapter(getApplicationContext()).deleteAssignments(courseCode);
                new CoursesDBAdapter(getApplicationContext()).deleteObligatories(courseCode);

                //Delete time for course per week
                new CoursesDBAdapter(getApplicationContext()).deleteTimeOnCourse(courseCode);

                //Delete time study sessions
                new SessionsDBAdapter(getApplicationContext()).deleteSessions(courseCode);

                //Delete course
                coursesDBAdapter.deleteCourse(courseCode);

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
    }

    private void chooseTimeOnCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Hur många timmar vill du lägga på kursen per vecka?");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        int time = coursesDBAdapter.getTimeOnCourse(courseCode)/60;
        String inputText = time > 0? time + "": "0";
        input.setText(inputText);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                int minutes = Integer.parseInt(value);
                minutes = minutes*60;
                Log.d("course", "add " + minutes + " minutes to " + courseCode);

                //add value
                long add = coursesDBAdapter.insertTimeOnCourse(courseCode, minutes);
                Toast toast;
                if (add > 0) {

                    int hours = (coursesDBAdapter.getTimeOnCourse(courseCode))/60;
                    timeOnCourseLabel.setText("Timmar per vecka: "+hours);
                    toast = Toast.makeText(getBaseContext(), "Ditt mål är att lägga " + hours + " timmar på " + courseName + " i veckan.", Toast.LENGTH_LONG);
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
