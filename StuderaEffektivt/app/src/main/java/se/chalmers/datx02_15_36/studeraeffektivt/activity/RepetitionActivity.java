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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;

public class RepetitionActivity extends ActionBarActivity {

    private String selectedWeek;
    private String selectedCourse;
    private CalendarModel calendarModel;
    private Spinner weekSpinner;
    private Spinner courseSpinner;
    private TextView taskTextView;
    public static Context cntxofParent;
    private OldAssignmentsDBAdapter assDBAdapter;
    private CoursesDBAdapter coursesDBAdapter;
    private String prevCourse = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repetition);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Repetition");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));

        cntxofParent = RepetitionActivity.this;

        weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        taskTextView = (TextView) findViewById(R.id.textView8);

        calendarModel = new CalendarModel();
        calendarModel.getCalendarInfo(getContentResolver());

        int currentWeek = CalendarUtils.getCurrWeekNumber();

        assDBAdapter = new OldAssignmentsDBAdapter(this);
        coursesDBAdapter = new CoursesDBAdapter(this);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter2);
        Cursor coursesCursor = coursesDBAdapter.getCourses();
        int cnameColumn = coursesCursor.getColumnIndex("cname");
        int ccodeColumn = coursesCursor.getColumnIndex("_ccode");
        int hasAssignments = 0;

        if (coursesCursor.getCount() > 0) {
            while (coursesCursor.moveToNext()) {
                String ccode = coursesCursor.getString(ccodeColumn);
                String cname = coursesCursor.getString(cnameColumn);
                adapter2.add(ccode + " " + cname);
                if(assDBAdapter.getDoneAssignments(ccode).getCount()>1){
                    hasAssignments++;
                }
            }
            if(hasAssignments < 1){
                noAssignmentsForCourse();
            }
        } else {
            adapter2.add("Inga tillagda kurser");
            if (assDBAdapter.getAssignments().getCount() < 1) {
                noCourses();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        weekSpinner.setAdapter(adapter);
        for (int i = 7, j = 0; i >= 0 && j < 7; i--, j++) {
            adapter.add("Vecka " + (currentWeek - i));
            //adapter.add("Vecka " + (currentWeek);     //For testing of getting assignments from a specific week
        }

        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //setRandomTasksView();
                setRandomTasksWithWeekView();     //For random assignments of a specific week
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //setRandomTasksView();
                setRandomTasksWithWeekView(); //For random assignments of a specific week
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        courseSpinner.setSelection(0);
        weekSpinner.setSelection(0);
        //setRandomTasksView();
        setRandomTasksWithWeekView(); //For random assignments of a specific week
    }

    private void addEventToCalendar(){
        selectedCourse = courseSpinner.getSelectedItem().toString();
        selectedWeek = weekSpinner.getSelectedItem().toString().toLowerCase();
        String courseName = (selectedCourse.split(" "))[1];

        //Cal
        EventActivity eventActivity = new EventActivity();
        Intent intent = new Intent(this, eventActivity.getClass());
        intent.putExtra("isInAddMode", false);
        intent.putExtra("isInRepMode", true);
        intent.putExtra("startTime", 0L);
        intent.putExtra("endTime", 0L);
        intent.putExtra("description", "Utvalda repetitionsuppgifter: \n" + taskTextView.getText());
        intent.putExtra("title", "Repetitonspass för " + selectedWeek + " av " + courseName);
        SharedPreferences sharedPref = this.getSharedPreferences("calendarPref", Context.MODE_PRIVATE);
        Long homeCalID = sharedPref.getLong("homeCalID", 1L); // 1 is some value if it fails to read??
        intent.putExtra("calID", homeCalID);        // is the home calendar
        intent.putExtra("calName", calendarModel.getCalendarsMap().get(homeCalID));     //get name of the home calendar
        intent.putExtra("color", calendarModel.getCalIdAndColorMap().get(homeCalID));
        startActivity(intent);
    }

    //Sets random tasks to a course with respect to the chosen week
    private void setRandomTasksWithWeekView(){
        selectedCourse = courseSpinner.getSelectedItem().toString();
        selectedWeek = weekSpinner.getSelectedItem().toString().toLowerCase();
        String courseCode = (selectedCourse.split(" "))[0];
        String courseName = (selectedCourse.split(" "))[1];
        int week = Integer.parseInt((selectedWeek.split(" "))[1]);

        if(hasFinishedAssignments(courseCode, week)){
            taskTextView.setText("");
            for (String task : getRandomAssingments(courseCode,week)) {
                taskTextView.setText(taskTextView.getText().toString() + task + "\n");
            }
            taskTextView.setText(taskTextView.getText().toString());
        } else {
            taskTextView.setText("Det finns inga repetitionsuppgifter. Du måste ha avklarade uppgifter från vecka " +  week + " i " + courseName + " för att skapa ett repetitionspass för den");
        }
    }

    private void noCourses() {
        final AlertDialog d = new AlertDialog.Builder(this)
                .setMessage("Du måste lägga till en kurs och utfört studiepass innan du kan lägga till ett repetitionspass!")
                .setPositiveButton("OK", null) //Set to null. We override the onclick
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button positive = d.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        finish();
                    }
                });
            }
        });

        d.show();
    }

    private void noAssignmentsForCourse() {
        final AlertDialog d = new AlertDialog.Builder(this)
                .setMessage("Du måste ha avklarat uppgifter för en kurs för att kunna skapa ett repetitionspass!")
                .setPositiveButton("OK", null) //Set to null. We override the onclick
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button positive = d.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        finish();
                    }
                });
            }
        });

        d.show();
    }

    private boolean hasFinishedAssignments(String courseCode, int week){
        Cursor doneAssignments = assDBAdapter.getDoneAssignments(courseCode);
        int nbrOfDoneAssignments = 0;

        while (doneAssignments.moveToNext()) {
            if (doneAssignments.getInt(doneAssignments.getColumnIndex("week")) == week) {
                nbrOfDoneAssignments++;
            }
        }
        return nbrOfDoneAssignments > 0;
    }


    //Returns random finished assignments of the course from a specific week
    private List <String> getRandomAssingments(String courseCode, int week) {
        Cursor doneAssignments = assDBAdapter.getDoneAssignments(courseCode);
        List<String> finishedAssignments = new ArrayList<>();

        while (doneAssignments.moveToNext()) {
            String taskInfo = "Kapitel: " + doneAssignments.getString(doneAssignments.getColumnIndex("chapter"));
            if (doneAssignments.getInt(doneAssignments.getColumnIndex("week")) == week) {
                if(doneAssignments.getString(doneAssignments.getColumnIndex("type")).equals(AssignmentType.READ)){
                    taskInfo =  taskInfo + " läs sid " + doneAssignments.getInt(doneAssignments.getColumnIndex("startPage")) + "-" +
                            doneAssignments.getInt(doneAssignments.getColumnIndex("stopPage"));
                } else {
                    taskInfo = taskInfo + " uppgift " + doneAssignments.getString(doneAssignments.getColumnIndex("assNr"));
                }
                finishedAssignments.add(taskInfo);
            }
        }
        return randomAssignments(finishedAssignments);
    }

    /* Randomizes assignments for a repetitionssession */
    private List<String> randomAssignments(List<String> assignments) {
        List<String> randomAssignments = new ArrayList<>();
        int numOfAssignments = assignments.size();

        for(int i = 0; i<= numOfAssignments/2; i++){
            Random random = new Random();
            String randomAssignment = assignments.get(random.nextInt(assignments.size()));

            if(!randomAssignments.contains(randomAssignment)) { //check duplicates
                randomAssignments.add(randomAssignment);
            }
        }
        return randomAssignments;
    }

    //Sets random tasks to a course without respect to the chosen week
    private void setRandomTasksView(){
        selectedCourse = courseSpinner.getSelectedItem().toString();
        selectedWeek = weekSpinner.getSelectedItem().toString().toLowerCase();

        if(!prevCourse.equals(selectedCourse)) {
            String courseCode = (selectedCourse.split(" "))[0];

            taskTextView.setText("");
            for (String task : getRandomAssingments(courseCode)) {
                taskTextView.setText(taskTextView.getText().toString() + task + "\n");
            }
            taskTextView.setText(taskTextView.getText().toString());
            prevCourse = selectedCourse;
        }
    }


    //Returns random assignments of the course
    private List<String> getRandomAssingments(String courseCode) {
        Cursor doneAssignments = assDBAdapter.getDoneAssignments(courseCode); //dbAdapter.getAssignments(courseCode);
        List<String> finishedAssignments = new ArrayList<>();

        while (doneAssignments.moveToNext()) {
            String taskInfo = "Kapitel: " + doneAssignments.getString(doneAssignments.getColumnIndex("chapter"));
            if(doneAssignments.getString(doneAssignments.getColumnIndex("type")).equals("READ")){
                taskInfo =  taskInfo + " läs sid " + doneAssignments.getInt(doneAssignments.getColumnIndex("startPage")) + "-" +
                        doneAssignments.getInt(doneAssignments.getColumnIndex("stopPage"));
            } else {
                taskInfo = taskInfo + " uppgift " + doneAssignments.getString(doneAssignments.getColumnIndex("assNr"));
            }
            finishedAssignments.add(taskInfo);
        }
        return randomAssignments(finishedAssignments);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repetition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else {
            addEventToCalendar();
        }
        return super.onOptionsItemSelected(item);
    }

}

