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
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.sharedPreference.CoursePreferenceHelper;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.ServiceHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

public class StudyTaskActivity extends ActionBarActivity {

    //Components in the view
    private EditText taskInput;
    private EditText taskParts;
    private FlowLayout listOfTasks;
    private Spinner chapterSpinner;
    private Spinner courseSpinner;
    private Spinner weekSpinner;
    private Spinner assignmentTypeSpinner;
    private TextView visibleTasksLabel;
    private TextView taskPartsLabel;


    private String courseCode;
    private String URL_CONNECTION = "http://studiecoachchalmers.se/insertassignmets.php";

    //The access point of the database.
    private OldAssignmentsDBAdapter assDBAdapter;
    private CoursesDBAdapter coursesDBAdapter;

    private int chosenWeek = CalendarUtils.getCurrWeekNumber();

    private CoursePreferenceHelper cph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_task);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Lägg till uppgifter");

        //Create the database access point but check if the context is null first.
        if (this != null) {
            assDBAdapter = new OldAssignmentsDBAdapter(this);
            coursesDBAdapter = new CoursesDBAdapter(this);
        }
        cph = CoursePreferenceHelper.getInstance(getApplicationContext());
        initComponents();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.save_tasks) {
            saveTask();
            return true;
        }else if (id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initComponents() {
        taskInput = (EditText) findViewById(R.id.taskInput);
        taskParts = (EditText) findViewById(R.id.taskParts);
        listOfTasks = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);
        chapterSpinner = (Spinner) findViewById(R.id.chapterSpinner);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
        assignmentTypeSpinner = (Spinner) findViewById(R.id.assignmentTypeSpinner);
        taskPartsLabel = (TextView) findViewById(R.id.taskPartsLabel);
        visibleTasksLabel = (TextView) findViewById(R.id.tasks_title);

        taskInput.getBackground().setColorFilter(Color.parseColor(Colors.lightGreyColor), PorterDuff.Mode.SRC_ATOP);
        taskParts.getBackground().setColorFilter(Color.parseColor(Colors.lightGreyColor), PorterDuff.Mode.SRC_ATOP);

        setCourseSpinner();
        setWeekSpinner();
        setAssignmentTypeSpinner();

        listOfTasks.addTasksFromDatabase(new OldAssignmentsDBAdapter(this), courseCode, AssignmentType.PROBLEM);
    }

    private void setCourseSpinner(){
        Integer[] chapterItems = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50};
        ArrayAdapter<Integer> chapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chapterItems);
        chapterSpinner.setAdapter(chapterAdapter);

        setCourses();
        cph.setSpinnerCourseSelection(courseSpinner);
        chapterSpinner.setSelection(0);
        setSelectedCourse();

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedCourse();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setWeekSpinner(){
        int week = CalendarUtils.getCurrWeekNumber();
        Integer[] weekItems = new Integer[15];
        for(int i = 0; i < weekItems.length; i++){
            if(week > 52)
                week = 1;
            weekItems[i] = week;
            week++;
        }

        ArrayAdapter<Integer> weekAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekItems);
        weekSpinner.setAdapter(weekAdapter);
        weekSpinner.setSelection(0);
    }

    private void setAssignmentTypeSpinner(){
        String[] assignmentTypes = new String[]{AssignmentType.HANDIN.toString(), AssignmentType.LAB.toString(), AssignmentType.PROBLEM.toString(), AssignmentType.READ.toString(), AssignmentType.OBLIGATORY.toString(), AssignmentType.OTHER.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, assignmentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignmentTypeSpinner.setAdapter(adapter);
        assignmentTypeSpinner.setSelection(2);

        if (assignmentTypeSpinner != null) {
            assignmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.PROBLEM.toString())) {
                        visibleTasksLabel.setText("Tillagda " + AssignmentType.PROBLEM.toString() );
                        updateListOfTasks(AssignmentType.PROBLEM);
                        taskParts.setVisibility(View.VISIBLE);
                        taskPartsLabel.setVisibility(View.VISIBLE);
                    } else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.READ.toString())) {
                        visibleTasksLabel.setText("Tillagda " + AssignmentType.READ.toString() );
                        updateListOfTasks(AssignmentType.READ);
                        taskParts.setVisibility(View.GONE);
                        taskPartsLabel.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void updateListOfTasks(AssignmentType assignmentType){
        listOfTasks.removeAllViews();
        listOfTasks.addTasksFromDatabase(new OldAssignmentsDBAdapter(this), courseCode, assignmentType);
    }

    private void saveTask() {
        String[] chapSep = chapterSpinner.getSelectedItem().toString().split(" ");
        int chapter = Integer.parseInt(chapSep[chapSep.length-1]);
        String[] weekSep = weekSpinner.getSelectedItem().toString().split(" ");
        chosenWeek = Integer.parseInt(weekSep[weekSep.length-1]);
        if(!taskInput.getText().toString().equals("")) {
            if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.PROBLEM.toString())) {
                addTask(chapter, taskInput.getText().toString(), taskParts.getText().toString());
            }
            else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.READ.toString())){
                addReadAssignment(chapter, taskInput.getText().toString());
            }
        }

        else{
            Toast.makeText(getApplicationContext(), "Format ej godkänt",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setCourses() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);
        Cursor cursor = coursesDBAdapter.getOngoingCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");
        while (cursor.moveToNext()) {
            String ccode = cursor.getString(ccodeColumn);
            String cname = cursor.getString(cnameColumn);
            if(ccode.equals(getIntent().getStringExtra("CourseCode")))
                adapter.insert(ccode + "-" + cname,0);
            else
                adapter.insert(ccode + "-" + cname,adapter.getCount());
        }

    }


    public void setSelectedCourse() {
        if(courseSpinner.getSelectedItem() != null) {
            String temp = courseSpinner.getSelectedItem().toString();
            String[] parts = temp.split("-");
            this.courseCode = parts[0];
            Log.d("selected course", courseCode);

            cph.setSharedCoursePos(courseSpinner.getSelectedItemPosition());

            //Tests if the update of the sharedpref worked:
            Log.d("sharedcourse", "Timer. set select: "+courseSpinner.getSelectedItemPosition()
                    + " == "+ cph.getSharedCoursePos());

            listOfTasks.removeAllViews();
            listOfTasks.addTasksFromDatabase(assDBAdapter, courseCode, AssignmentType.PROBLEM);

            if(listOfTasks.isEmpty()){
                TextView textView = new TextView(this);
                textView.setText("Du har för närvaranade inga räkneuppgifter för den här kursen, lägg till en uppgift genom att fylla i informationen ovan och trycka på spara-knappen i övre högra hörnet");
                textView.setPadding(15,5,15,5);
                listOfTasks.addView(textView);
            }
        }
        else{
            final AlertDialog d = new AlertDialog.Builder(this)
                    .setMessage("Du måste lägga till en kurs innan du kan lägga till uppgifter!")
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
    }

    //Metod för att lägga till en uppgift

    public void addTask(int chapter, String taskString, String taskParts) {

        if(!(taskString.contains("-") && taskString.contains("."))){

            ArrayList<String> stringList = new ArrayList();
            String[] separateLine;
            String[] separateComma;
            String[] separateTaskParts;
            separateTaskParts = taskParts.split("");

            taskString.replaceAll("\\s+", "");

            int start;
            int end;

            //Checks if there is a comma in the input and if that is the case, it separates the string so that all the elements are separated.
            if (taskString.contains(",")) {

                separateComma = taskString.split(",");  //Divides the string into an array with the elements between the commas
            } else {
                separateComma = new String[1];
                separateComma[0] = taskString;
            }

            //Checks the elements respectively and if a patter of assignments e.g. 1-3 are observed then it makes so that these are added respectively as 1, 2 and 3
            for (String aSeparateComma : separateComma) {
                if (aSeparateComma.contains("-")) {

                    separateLine = aSeparateComma.split("-");    //Divides the string into an array with the elements between the "-" sign
                    start = Integer.parseInt(separateLine[0]);   //Start and end is the interval of the elements which are to be added
                    end = Integer.parseInt(separateLine[separateLine.length - 1]);

                    for (int i = start; i <= end; i++) {
                        stringList.add("" + i);
                    }
                } else {
                    stringList.add(aSeparateComma);
                }
            }

            //Adds the subtaaks if the input for it exists, e.g. a, b, c.
            String elementToAdd;
            Random rand = new Random();
            if (separateTaskParts.length > 1) {
                for (int i = 1; i < separateTaskParts.length; i++) {       //For each subtasks
                    for (String s2 : stringList) {                         //For each maintask
                        elementToAdd = s2 + separateTaskParts[i];       //Combine the maintasks 1 and subtask a such that it becomes 1a
                        if (!listOfTasks.contains(chapter, elementToAdd)) {
                            int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                            StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, elementToAdd, 0, 0, assDBAdapter, AssignmentType.PROBLEM, AssignmentStatus.UNDONE);
                            addToDatabase(studyTask);
                            new AddToWebDatabase().execute(courseCode, chapter+"",chosenWeek+"",elementToAdd,
                                    Integer.toString(0),Integer.toString(0),"PROBLEM","UNDONE");
                        } else {
                            Toast.makeText(this, "Uppgift redan tillagd!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            //Adds the maintasks if the subtasks does not exist
            else {
                for (String s : stringList) {         //For each maintask
                    if (!listOfTasks.contains(chapter, s)) {
                        int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                        StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, s, 0, 0, assDBAdapter, AssignmentType.PROBLEM, AssignmentStatus.UNDONE);
                        addToDatabase(studyTask);
                        new AddToWebDatabase().execute(courseCode, chapter+"",chosenWeek+"",s,
                                Integer.toString(0),Integer.toString(0),"PROBLEM","UNDONE");
                    } else {
                        Toast.makeText(this, "Uppgift redan tillagd!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Format ej godkänt",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void addReadAssignment(int chapter, String taskString) {

        if(!taskString.contains(",") && !taskString.contains(".")){
            String[] separateLine;

            int start;
            int end;

            Random rand = new Random();
            int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;

            if (taskString.contains("-")) {

                separateLine = taskString.split("-");   //Divides the string into an array with the elements between the commas
                start = Integer.parseInt(separateLine[0]);    //Start and end is the interval of the elements which are to be added
                end = Integer.parseInt(separateLine[separateLine.length - 1]);

               if(!(listOfTasks.contains(chapter, start, end))) {
                    StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, "read", start, end, assDBAdapter, AssignmentType.READ, AssignmentStatus.UNDONE);

                    addToDatabase(studyTask);
                    new AddToWebDatabase().execute(courseCode, chapter+"",chosenWeek+"","ReadAssignment",
                            start+"",end+"","READ","UNDONE");
                    //addToListOfTasks(studyTask);
                }
                else{
                    Toast.makeText(this,"Läsanvisning redan tillagd!",Toast.LENGTH_SHORT).show();
                }

            } else {
                 if (!(listOfTasks.contains(chapter, Integer.parseInt(taskString), Integer.parseInt(taskString)))) {

                    StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, "ReadAssignment",
                            Integer.parseInt(taskString), Integer.parseInt(taskString), assDBAdapter, AssignmentType.READ, AssignmentStatus.UNDONE);

                    addToDatabase(studyTask);
                    new AddToWebDatabase().execute(courseCode, chapter+"",chosenWeek+"","ReadAssignmet",
                            taskString,taskString,"READ","UNDONE");
                } else {
                    Toast.makeText(this, "Läsanvisning redan tillagd!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Format ej godkänt",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void addToDatabase(StudyTask studyTask) {

        assDBAdapter.insertAssignment(
                studyTask.getCourseCode(),
                studyTask.getIdNr(),
                studyTask.getChapter(),
                studyTask.getWeek(),
                studyTask.getTaskString(),
                studyTask.getStartPage(),
                studyTask.getEndPage(),
                studyTask.getType(),
                studyTask.getStatus()
        );

        Cursor asses = assDBAdapter.getAssignments();
        Log.d("ass", "asses in db: "+asses.getCount()+" course: "+studyTask.getType());

        if(studyTask.getType().equals(AssignmentType.READ)){
            listOfTasks.removeAllViews();
            listOfTasks.addTasksFromDatabase(assDBAdapter, courseCode, AssignmentType.PROBLEM);
        }
        else{
            listOfTasks.removeAllViews();
            listOfTasks.addTasksFromDatabase(assDBAdapter, courseCode, AssignmentType.PROBLEM);
        }

        taskInput.setText("");
        taskParts.setText("");

    }

    private class AddToWebDatabase extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg) {
            String course = arg[0];
            String chapter = arg[1];
            String week = arg[2];
            String assNr = arg[3];
            String startPage = arg[4];
            String endPage = arg[5];
            String type = arg[6];
            String status = arg[7];


            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("course",course));
            params.add(new BasicNameValuePair("chapter",chapter));
            params.add(new BasicNameValuePair("week",week));
            params.add(new BasicNameValuePair("assNr",assNr));
            params.add(new BasicNameValuePair("startPage",startPage));
            params.add(new BasicNameValuePair("endPage",endPage));
            params.add(new BasicNameValuePair("type",type));
            params.add(new BasicNameValuePair("status",status));


            ServiceHandler serviceClient = new ServiceHandler();

            String json = serviceClient.makeServiceCall(URL_CONNECTION,
                    ServiceHandler.POST, params);

            if (json != null) {
                try {
                    Log.d("jason",json);
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        Log.d(" Success","alexärbäst");
                    } else {
                        Log.d(" Error: ",
                                "notsogod");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "JSON data error!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
}
