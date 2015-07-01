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
import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.LabAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OtherAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ProblemAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.sharedPreference.CoursePreferenceHelper;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.ServiceHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;

public class StudyTaskActivity extends ActionBarActivity {

    //Components in the view
    private EditText taskInput;
    private EditText taskParts;
    private AssignmentCheckBoxLayout assignmentsFlowLayout;
    private Spinner chapterSpinner;
    private Spinner courseSpinner;
    private Spinner weekSpinner;
    private Spinner assignmentTypeSpinner;
    private TextView visibleTasksLabel;
    private TextView taskPartsLabel;


    private String courseCode;
    private String URL_CONNECTION = "http://studiecoachchalmers.se/insertassignmets.php";

    //The access point of the database.
    private HandInAssignmentsDBAdapter handInDB;
    private LabAssignmentsDBAdapter labDB;
    private OtherAssignmentsDBAdapter otherDB;
    private ProblemAssignmentsDBAdapter problemDB;
    private ReadAssignmentsDBAdapter readDB;


    private CoursesDBAdapter coursesDBAdapter;

    private int chosenWeek = CalendarUtils.getCurrWeekNumber();

    private CoursePreferenceHelper coursePrefHelper;

    public StudyTaskActivity() {
    }

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
            handInDB = new HandInAssignmentsDBAdapter(this);
            labDB = new LabAssignmentsDBAdapter(this);
            otherDB = new OtherAssignmentsDBAdapter(this);
            problemDB = new ProblemAssignmentsDBAdapter(this);
            readDB = new ReadAssignmentsDBAdapter(this);
            coursesDBAdapter = new CoursesDBAdapter(this);
        }
        coursePrefHelper = CoursePreferenceHelper.getInstance(getApplicationContext());
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
        assignmentsFlowLayout = (AssignmentCheckBoxLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);
        chapterSpinner = (Spinner) findViewById(R.id.chapterSpinner);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
        assignmentTypeSpinner = (Spinner) findViewById(R.id.assignmentTypeSpinner);
        taskPartsLabel = (TextView) findViewById(R.id.taskPartsLabel);
        visibleTasksLabel = (TextView) findViewById(R.id.tasks_title);

        taskInput.getBackground().setColorFilter(Color.parseColor(Colors.lightGreyColor), PorterDuff.Mode.SRC_ATOP);
        taskParts.getBackground().setColorFilter(Color.parseColor(Colors.lightGreyColor), PorterDuff.Mode.SRC_ATOP);

        setCourseSpinner();
        setChapterSpinner();
        setWeekSpinner();
        setAssignmentTypeSpinner();
        //updateAssignmentsLayout(AssignmentType.PROBLEM);
    }

    private void setChapterSpinner() {
        Integer[] chapterItems = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50};
        ArrayAdapter<Integer> chapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chapterItems);
        chapterSpinner.setAdapter(chapterAdapter);
    }

    private void setCourseSpinner(){
        setCourses();
        coursePrefHelper.setSpinnerCourseSelection(courseSpinner);
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
                    //updateView();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void updateView(){
        if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.HANDIN.toString())) {
            visibleTasksLabel.setText("Tillagda " + AssignmentType.HANDIN.toString());
            updateAssignmentsLayout(AssignmentType.HANDIN);
            taskParts.setVisibility(View.VISIBLE);
            taskPartsLabel.setVisibility(View.VISIBLE);

        } else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.LAB.toString())) {
            visibleTasksLabel.setText("Tillagda " + AssignmentType.LAB.toString() );
            updateAssignmentsLayout(AssignmentType.LAB);
            taskParts.setVisibility(View.VISIBLE);
            taskPartsLabel.setVisibility(View.VISIBLE);

        } else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.PROBLEM.toString())) {
            visibleTasksLabel.setText("Tillagda " + AssignmentType.PROBLEM.toString() );
            updateAssignmentsLayout(AssignmentType.PROBLEM);
            taskParts.setVisibility(View.VISIBLE);
            taskPartsLabel.setVisibility(View.VISIBLE);

        } else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.READ.toString())) {
            visibleTasksLabel.setText("Tillagda " + AssignmentType.READ.toString());
            updateAssignmentsLayout(AssignmentType.READ);
            taskParts.setVisibility(View.GONE);
            taskPartsLabel.setVisibility(View.GONE);

        } else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.OBLIGATORY.toString())) {
            visibleTasksLabel.setText("Tillagda " + AssignmentType.OBLIGATORY.toString());
            updateAssignmentsLayout(AssignmentType.OBLIGATORY);
            taskParts.setVisibility(View.GONE);
            taskPartsLabel.setVisibility(View.GONE);

        } else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.OTHER.toString())) {
            visibleTasksLabel.setText("Tillagda " + AssignmentType.OTHER.toString());
            updateAssignmentsLayout(AssignmentType.OTHER);
            taskParts.setVisibility(View.GONE);
            taskPartsLabel.setVisibility(View.GONE);

        }

    }

    private void updateAssignmentsLayout(AssignmentType assignmentType){
        assignmentsFlowLayout.removeAllViews();
        switch (assignmentType) {
            case HANDIN:
                assignmentsFlowLayout.addHandInsFromDatabase(courseCode, handInDB);
            case LAB:
                assignmentsFlowLayout.addLabsFromDatabase(courseCode, labDB);
            case OTHER:
                assignmentsFlowLayout.addOthersFromDatabase(courseCode, otherDB);
            case PROBLEM:
                assignmentsFlowLayout.addProblemsFromDatabase(courseCode, problemDB);
            case READ:
                assignmentsFlowLayout.addReadsFromDatabase(courseCode, readDB);
        }

    }

    private void saveTask() {
        String[] chapSep = chapterSpinner.getSelectedItem().toString().split(" ");
        int chapter = Integer.parseInt(chapSep[chapSep.length-1]);
        String[] weekSep = weekSpinner.getSelectedItem().toString().split(" ");
        chosenWeek = Integer.parseInt(weekSep[weekSep.length-1]);
        if(!taskInput.getText().toString().equals("")) {

            if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.PROBLEM.toString())) {
                addTask(chapter, taskInput.getText().toString(), taskParts.getText().toString(), AssignmentType.PROBLEM);
            }
            else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.LAB.toString())){
                addTask(chapter, taskInput.getText().toString(), taskParts.getText().toString(), AssignmentType.LAB);
            }
            else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.HANDIN.toString())){
                addTask(chapter, taskInput.getText().toString(), taskParts.getText().toString(), AssignmentType.HANDIN);
            }
            else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.READ.toString())){
                Log.i("STA", "in saveTask");
                addReadAssignment(chapter, taskInput.getText().toString());
            }
            else if (assignmentTypeSpinner.getSelectedItem().toString().equals(AssignmentType.OTHER.toString())){
                addOtherAssignment(Integer.toString(chapter), taskInput.getText().toString());
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

            coursePrefHelper.setSharedCoursePos(courseSpinner.getSelectedItemPosition());

            //Tests if the update of the sharedpref worked:
            Log.d("sharedcourse", "Timer. set select: " + courseSpinner.getSelectedItemPosition()
                    + " == " + coursePrefHelper.getSharedCoursePos());

            assignmentsFlowLayout.removeAllViews();
//            assignmentsFlowLayout.addProblemsFromDatabase(courseCode, problemDB);

            if(assignmentsFlowLayout.isEmpty()){
                TextView textView = new TextView(this);
                textView.setText("Du har för närvaranade inga räkneuppgifter för den här kursen, lägg till en uppgift genom att fylla i informationen ovan och trycka på spara-knappen i övre högra hörnet");
                textView.setPadding(15,5,15,5);
                assignmentsFlowLayout.addView(textView);
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

    public void addTask(int chapter, String taskString, String taskParts, AssignmentType type) {

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
                        if (!assignmentsFlowLayout.contains(Integer.toString(chapter), elementToAdd)) {
                            int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                            addToDatabase(courseCode, type, randomNum, Integer.toString(chapter), chosenWeek, elementToAdd);
                        } else {
                            Toast.makeText(this, "Uppgift redan tillagd!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            //Adds the maintasks if the subtasks does not exist
            else {
                for (String s : stringList) {         //For each maintask
                    if (!assignmentsFlowLayout.contains(Integer.toString(chapter), s)) {
                        int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                        addToDatabase(courseCode, type, randomNum, Integer.toString(chapter), chosenWeek, s);
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

            Log.i("STA", "in addReadAssignment");


            Random rand = new Random();
            int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;

            if (taskString.contains("-")) {

                separateLine = taskString.split("-");   //Divides the string into an array with the elements between the commas
                start = Integer.parseInt(separateLine[0]);    //Start and end is the interval of the elements which are to be added
                end = Integer.parseInt(separateLine[separateLine.length - 1]);

               // if(!(assignmentsFlowLayout.contains(Integer.toString(chapter), Integer.toString(start), Integer.toString(end)))) {
                    addToDatabase(courseCode, AssignmentType.READ, randomNum, Integer.toString(chapter), chosenWeek, start, end);
                /*}
                else{
                    Toast.makeText(this,"Läsanvisning redan tillagd!",Toast.LENGTH_SHORT).show();
                }*/

            } else {
                if (!(assignmentsFlowLayout.contains(Integer.toString(chapter), taskString, taskString))) {
                    addToDatabase(courseCode, AssignmentType.READ, randomNum, Integer.toString(chapter), chosenWeek, Integer.parseInt(taskString), Integer.parseInt(taskString));
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

    public void addOtherAssignment(String chapter, String taskString) {
        if(!taskString.contains(",") && !taskString.contains(".")){
            Random rand = new Random();
            int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;

            if (!(assignmentsFlowLayout.contains(chapter, taskString, taskString))) {
                addToDatabase(courseCode, AssignmentType.READ, randomNum, chosenWeek, taskString);
            } else {
                Toast.makeText(this, "Läsanvisning redan tillagd!", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(getApplicationContext(), "Format ej godkänt",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void addToDatabase(String courseCode, AssignmentType type, int id, String chapterOrNumber, int week, String assignment) {
        switch (type){

            case PROBLEM:
                ProblemAssignmentsDBAdapter problemAssignmentsDBAdapter = new ProblemAssignmentsDBAdapter(this);
                problemAssignmentsDBAdapter.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            case LAB:
                LabAssignmentsDBAdapter labAssignmentsDBAdapter = new LabAssignmentsDBAdapter(this);
                labAssignmentsDBAdapter.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            case HANDIN:
                HandInAssignmentsDBAdapter handInAssignmentsDBAdapter = new HandInAssignmentsDBAdapter(this);
                handInAssignmentsDBAdapter.insertAssignment(courseCode, id, chapterOrNumber, week, assignment, AssignmentStatus.UNDONE);
                break;

            default:
                //do nothing

        }
       // updateAssignmentsLayout(type);
        taskInput.setText("");
        taskParts.setText("");
    }

    public void addToDatabase(String courseCode, AssignmentType type, int id, String chapter, int week, int startPage, int endPage){






        Log.i("STA", "in addToDatabase: kurskod:" + courseCode);


        Log.i("STA", "in addToDatabase: kurskod:" + courseCode);

        Log.i("STA", "in addToDatabase: id:" + id);

        Log.i("STA", "in addToDatabase: chapter:" + chapter);

        Log.i("STA", "in addToDatabase: week:" + week);

        Log.i("STA", "in addToDatabase: startPage:" + startPage);

        Log.i("STA", "in addToDatabase: endPage:" + endPage);

        long result = readDB.insertAssignment(courseCode, id, chapter, week, startPage, endPage, AssignmentStatus.UNDONE);

        Log.i("STA", "in addToDatabase: result:" + result);

       Log.i("STA", "in addToDatabase: inserted week:" + readDB.getWeek(id));



       // updateAssignmentsLayout(type);
        taskInput.setText("");
        taskParts.setText("");
    }

    public void addToDatabase(String courseCode, AssignmentType type, int id, int week, String assignment) {
        OtherAssignmentsDBAdapter otherAssignmentsDBAdapter = new OtherAssignmentsDBAdapter(this);
        otherAssignmentsDBAdapter.insertAssignment(courseCode, id, week, assignment, AssignmentStatus.UNDONE);
       // updateAssignmentsLayout(type);
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
