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
import android.util.Log;
import android.view.LayoutInflater;
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
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CalendarFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;
import se.chalmers.datx02_15_36.studeraeffektivt.util.RepetitionReminder;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;

public class RepetitionActivity extends ActionBarActivity {

    private String selectedWeek;
    private String selectedCourse;
    private CalendarModel calendarModel;
    private Spinner weekSpinner;
    private Spinner courseSpinner;
    private TextView taskTextView;
    private TextView tasklabel;
    public static Context cntxofParent;
    private DBAdapter dbAdapter;
    private String prevCourse;
    private String prevWeek;
    private boolean hasAlreadyGeneratedForThisCourse = false;
    private boolean canRepeat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repetition);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Repetition");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.primaryColor)));

        cntxofParent = RepetitionActivity.this;

        weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        tasklabel = (TextView) findViewById(R.id.textView4);
        taskTextView = (TextView) findViewById(R.id.textView8);
        tasklabel.setVisibility(View.GONE);

        calendarModel = new CalendarModel();
        calendarModel.getCalendarInfo(getContentResolver());

        final String[] alternatives = new String[7];
        int currentWeek = Utils.getCurrWeekNumber();


        dbAdapter = new DBAdapter(this);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter2);
        Cursor cursor = dbAdapter.getCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");

        if (cursor.getCount() > 0) {
            String ccode = "";
            while (cursor.moveToNext()) {
                ccode = cursor.getString(ccodeColumn);
                String cname = cursor.getString(cnameColumn);
                adapter2.add(ccode + " " + cname);
            }
            if(dbAdapter.getDoneAssignments(ccode).getCount()<1){
                canRepeat = false;
                noAssignmentsForCourse();
            }
        } else {
            adapter2.add("Inga tillagda kurser");
            if (dbAdapter.getAssignments().getCount() < 1) {
                canRepeat = false;
                noCourses();
            }
        }
        canRepeat = true;
        tasklabel.setVisibility(View.VISIBLE);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Log.i("calendarFrag", weekSpinner.toString());
        Log.i("calendarFrag", adapter.toString());
        weekSpinner.setAdapter(adapter);
        for (int i = 7, j = 0; i >= 0 && j < alternatives.length; i--, j++) {
            int newWeek = currentWeek - i;
            Log.i("newWeek", newWeek + "");
            adapter.add("Vecka " + newWeek);
            alternatives[j] = "Vecka " + newWeek;
        }
        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setRandomTasksView();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setRandomTasksView();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        courseSpinner.setSelection(0);
        prevCourse = courseSpinner.getSelectedItem().toString();
        weekSpinner.setSelection(0);
        //prevWeek = weekSpinner.getSelectedItem().toString();
        setRandomTasksView();
    }

    private void setRandomTasksView(){
        selectedCourse = courseSpinner.getSelectedItem().toString();
        selectedWeek = weekSpinner.getSelectedItem().toString().toLowerCase();

        if(canRepeat && !hasAlreadyGeneratedForThisCourse) {
            String[] splitCourseCodeFromName = (selectedCourse.split(" "));
            String courseCode = splitCourseCodeFromName[0];
            String[] splitWeekTextFromNr = (selectedWeek.split(" "));
            int week = Integer.parseInt(splitWeekTextFromNr[1]);

            //for (String task : getRandomAssingments(courseCode,week)) { TODO: ange veckor
            for (String task : getRandomAssingments(courseCode)) {
                taskTextView.setText(taskTextView.getText().toString() + task + "\n");
            }
            taskTextView.setText(taskTextView.getText().toString());
            if(prevCourse.equals(selectedCourse)){ //&& prevWeek.equals(selectedWeek)
                hasAlreadyGeneratedForThisCourse = true;
            }
            prevCourse = selectedCourse;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_repetition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else {
            selectedCourse = courseSpinner.getSelectedItem().toString();
            selectedWeek = weekSpinner.getSelectedItem().toString().toLowerCase();
            String [] splitCourseCodeFromName = (selectedCourse.split(" "));
            String courseName = splitCourseCodeFromName[1];

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

        return super.onOptionsItemSelected(item);
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

    //Returns random assignments of the course
    public List<String> getRandomAssingments(String courseCode) {
        Cursor doneAssignments = dbAdapter.getDoneAssignments(courseCode); //dbAdapter.getAssignments(courseCode);
        List<String> finishedAssignments = new ArrayList<>();

        while (doneAssignments.moveToNext()) {
            String taskInfo = "Kapitel: " + doneAssignments.getString(doneAssignments.getColumnIndex("chapter"));
            if(doneAssignments.getString(doneAssignments.getColumnIndex("type")).equals(AssignmentType.READ)){
                taskInfo =  taskInfo + " läs sid " + doneAssignments.getInt(doneAssignments.getColumnIndex("startPage")) + "-" +
                        doneAssignments.getInt(doneAssignments.getColumnIndex("stopPage"));
            } else {
                taskInfo = taskInfo + " uppgift " + doneAssignments.getString(doneAssignments.getColumnIndex("assNr"));
            }
            finishedAssignments.add(taskInfo);
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

    //Returns random finished assignments of the course from a specific week
    public List <String> getRandomAssingments(String courseCode, int week) {
        Cursor doneAssignments = dbAdapter.getDoneAssignments(courseCode);
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
}

