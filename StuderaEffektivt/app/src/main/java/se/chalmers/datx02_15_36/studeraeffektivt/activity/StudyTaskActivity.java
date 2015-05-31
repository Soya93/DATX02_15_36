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

/*
Saker att fixa är:
Uppdatera då man kryssar av en ruta, någon sortering
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;
import se.chalmers.datx02_15_36.studeraeffektivt.util.ServiceHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

public class StudyTaskActivity extends ActionBarActivity {

    //Components in the view
    private EditText taskInput;
   // private Button addButton;
    private EditText taskParts;
    private FlowLayout listOfTasks;
    private FlowLayout listOfReadAssignments;
    private Spinner chapterSpinner;
    private Spinner courseSpinner;
    private Spinner weekSpinner;
    private ToggleButton readOrTaskAssignment;
    private TextView taskPartsLabel;

    private String courseCode;
    private String URL_CONNECTION = "http://studiecoachchalmers.se/insertassignmets.php";

    //The access point of the database.
    private DBAdapter dbAdapter;

    private int chosenWeek = Utils.getCurrWeekNumber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_task);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.primaryColor)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Lägg till uppgifter");

        //Create the database access point but check if the context is null first.
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }
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

        //noinspection SimplifiableIfStatement  //noinspection SimplifiableIfStatement
        if (id == R.id.save_tasks) {
            saveTaks();
            return true;
        }else if (id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initComponents() {
        //addButton = (Button) findViewById(R.id.addButton);
        taskInput = (EditText) findViewById(R.id.taskInput);
        taskParts = (EditText) findViewById(R.id.taskParts);
        listOfTasks = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);
        listOfReadAssignments = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfReadingAssignments);
        chapterSpinner = (Spinner) findViewById(R.id.chapterSpinner);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
        readOrTaskAssignment = (ToggleButton) findViewById(R.id.readOrTaskAssignment);
        taskPartsLabel = (TextView) findViewById(R.id.taskPartsLabel);

        readOrTaskAssignment.setChecked(true);
        taskInput.getBackground().setColorFilter(Color.parseColor(Constants.lightGreyColor), PorterDuff.Mode.SRC_ATOP);
        taskParts.getBackground().setColorFilter(Color.parseColor(Constants.lightGreyColor), PorterDuff.Mode.SRC_ATOP);




        Integer[] chapterItems = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50};
       /* String[] chapterItems = new String[]{"Kap 1", "Kapitel 2", "Kap 3", "Kap 4", "Kap 5", "Kap 6", "Kap 7", "Kap 8", "Kap 9", "Kap 10",
                "Kap 11", "Kap 12", "Kap 13", "Kap 14", "Kap 15", "Kap 16", "Kap 17", "Kap 18", "Kap 19", "Kap 20",
                "Kap 21", "Kap 22", "Kap 23", "Kap 24", "Kap 25", "Kap 26", "Kap 27", "Kap 28", "Kap 29", "Kap 30",
                "Kap 31", "Kap 32", "Kap 33", "Kap 34", "Kap 35", "Kap 36", "Kap 37", "Kap 38", "Kap 39", "Kap 40",
                "Kap 41", "Kap 42", "Kap 43", "Kap 44", "Kap 45", "Kap 46", "Kap 47", "Kap 48", "Kap 49", "Kap 50"};*/
        ArrayAdapter<Integer> chapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chapterItems);
        chapterSpinner.setAdapter(chapterAdapter);

        int week = Utils.getCurrWeekNumber();
        Integer[] weekItems = new Integer[15];
        for(int i = 0; i < weekItems.length; i++){
            if(week > 52)
                week = 1;
            weekItems[i] = week;
            week++;
        }

        ArrayAdapter<Integer> weekAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekItems);
        weekSpinner.setAdapter(weekAdapter);


        setCourses();
        courseSpinner.setSelection(0);
        chapterSpinner.setSelection(0);
        weekSpinner.setSelection(0);
        setSelectedCourse();

        listOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.OTHER);
        listOfReadAssignments.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);

        //addButton.setOnClickListener(myOnlyhandler);

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedCourse();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        readOrTaskAssignment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    taskParts.setVisibility(View.VISIBLE);
                    taskPartsLabel.setVisibility(View.VISIBLE);
                }
                else{
                    taskParts.setVisibility(View.GONE);
                    taskPartsLabel.setVisibility(View.GONE);
                }
            }
        });
    }

    /*View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            if ((v) == addButton) {
                saveTaks();
            }

        }
    };*/

    private void saveTaks() {
        String[] chapSep = chapterSpinner.getSelectedItem().toString().split(" ");
        int chapter = Integer.parseInt(chapSep[chapSep.length-1]);
        //int chapter = Integer.parseInt(chapterSpinner.getSelectedItem().toString());
        String[] weekSep = weekSpinner.getSelectedItem().toString().split(" ");
        chosenWeek = Integer.parseInt(weekSep[weekSep.length-1]);
        //chosenWeek = Integer.parseInt(weekSpinner.getSelectedItem().toString());
        if(!taskInput.getText().toString().equals("")) {
            if (readOrTaskAssignment.isChecked()) {
                addTask(chapter, taskInput.getText().toString(), taskParts.getText().toString());
            }
            else {
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
        Cursor cursor = dbAdapter.getOngoingCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");
        while (cursor.moveToNext()) {
            String ccode = cursor.getString(ccodeColumn);
            String cname = cursor.getString(cnameColumn);
            if(ccode.equals(getIntent().getStringExtra("CourseCode")))
                adapter.insert(ccode + "-" + cname,0);
            else
                adapter.insert(ccode + "-" + cname,adapter.getCount());

            /*if(ccode == getIntent().getStringExtra("CourseCode"))
                adapter.add(ccode + "-" + cname);
            else
                adapter.add(ccode + "-" + cname);*/
        }

    }


    public void setSelectedCourse() {
        if(courseSpinner.getSelectedItem() != null) {
            String temp = courseSpinner.getSelectedItem().toString();
            String[] parts = temp.split("-");
            this.courseCode = parts[0];
            Log.d("selected course", courseCode);

            listOfTasks.removeAllViews();
            listOfReadAssignments.removeAllViews();
            listOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.OTHER);
            listOfReadAssignments.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);

            if(listOfTasks.isEmpty()){
                TextView textView = new TextView(this);
                textView.setText("Du har för närvaranade inga räkneuppgifter för den här kursen, lägg till en uppgift genom att fylla i informationen ovan och trycka på spara-knappen i övre högra hörnet");
                textView.setPadding(15,5,15,5);
                listOfTasks.addView(textView);
            }
            if(listOfReadAssignments.isEmpty()){
                TextView textView = new TextView(this);
                textView.setText("Du har för närvaranade inga läsanvisningar för den här kursen, lägg till en uppgift genom att fylla i informationen ovan och trycka på spara-knappen i övre högra hörnet");
                textView.setPadding(15,5,15,5);
                listOfReadAssignments.addView(textView);
            }
        }
        else{
            LayoutInflater inflater = this.getLayoutInflater();

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

            //Kollar om det finns kommatecken i input för uppgifter och separerar i så fall stringen så att alla element hamnar separat
            if (taskString.contains(",")) {

                separateComma = taskString.split(",");  //Delar upp stringen till en array med elementen mellan kommatecknerna
            } else {
                separateComma = new String[1];
                separateComma[0] = taskString;
            }

            //Kollar elementen var för sig och ser om de är ett spann av uppgifter att lägga till 1-3 gör så att 1, 2 och 3 läggs till
            for (String aSeparateComma : separateComma) {
                if (aSeparateComma.contains("-")) {

                    separateLine = aSeparateComma.split("-");   //Delar upp stringen till en array med elementen mellan bindesstrecken
                    start = Integer.parseInt(separateLine[0]);    //Start och end är intervallet för de element som skall läggas till
                    end = Integer.parseInt(separateLine[separateLine.length - 1]);

                    for (int i = start; i <= end; i++) {
                        stringList.add("" + i);
                    }
                } else {
                    stringList.add(aSeparateComma);
                }
            }

            //Lägger till deluppgifter om input för detta finns, tex a, b c.
            String elementToAdd;
            Random rand = new Random();
            if (separateTaskParts.length > 1) {
                for (int i = 1; i < separateTaskParts.length; i++) {       //För varje deluppgift
                    for (String s2 : stringList) {                         //För varje vihuv uppgift
                        elementToAdd = s2 + separateTaskParts[i];       //Sätt ihop dessa Huvuduppgift 1 och deluppgift a blir 1a
                        if (!listOfTasks.contains(chapter, elementToAdd)) {
                            int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                            StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, elementToAdd, 0, 0, dbAdapter, AssignmentType.OTHER, AssignmentStatus.UNDONE);
                            //addToListOfTasks(studyTask);
                            addToDatabase(studyTask);
                            new AddToWebDatabase().execute(courseCode, chapter+"",chosenWeek+"",elementToAdd,
                                    Integer.toString(0),Integer.toString(0),"OTHER","UNDONE");
                        } else {
                            Toast.makeText(this, "Uppgift redan tillagd!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            //lägger till huvuduppgifterna då deluppgifter inte finns
            else {
                for (String s : stringList) {         //För varje huvuduppgift
                    if (!listOfTasks.contains(chapter, s)) {
                        int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                        StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, s, 0, 0, dbAdapter, AssignmentType.OTHER, AssignmentStatus.UNDONE);
                        //addToListOfTasks(studyTask);
                        addToDatabase(studyTask);
                        new AddToWebDatabase().execute(courseCode, chapter+"",chosenWeek+"",s,
                                Integer.toString(0),Integer.toString(0),"OTHER","UNDONE");
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

                separateLine = taskString.split("-");   //Delar upp stringen till en array med elementen mellan bindesstrecken
                start = Integer.parseInt(separateLine[0]);    //Start och end är intervallet för de element som skall läggas till
                end = Integer.parseInt(separateLine[separateLine.length - 1]);

                if(!(listOfReadAssignments.contains(chapter, start, end))) {
                    StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, "read", start, end, dbAdapter, AssignmentType.READ, AssignmentStatus.UNDONE);

                    addToDatabase(studyTask);
                    new AddToWebDatabase().execute(courseCode, chapter+"",chosenWeek+"","ReadAssignment",
                            start+"",end+"","READ","UNDONE");
                    //addToListOfTasks(studyTask);
                }
                else{
                    Toast.makeText(this,"Läsanvisning redan tillagd!",Toast.LENGTH_SHORT).show();
                }

            } else {
                if (!(listOfReadAssignments.contains(chapter, Integer.parseInt(taskString), Integer.parseInt(taskString)))) {

                    StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, "ReadAssignment",
                            Integer.parseInt(taskString), Integer.parseInt(taskString), dbAdapter, AssignmentType.READ, AssignmentStatus.UNDONE);

                    addToDatabase(studyTask);
                    new AddToWebDatabase().execute(courseCode, chapter+"",chosenWeek+"","ReadAssignmet",
                            taskString,taskString,"READ","UNDONE");
                    //addToListOfTasks(studyTask);
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

    public void addToListOfTasks(StudyTask studyTask) {

        if(studyTask.getType().equals(AssignmentType.READ)){
            listOfReadAssignments.addView(studyTask);
        }
        else{
            listOfTasks.addView(studyTask);
        }
    }

    public void addToDatabase(StudyTask studyTask) {

        dbAdapter.insertAssignment(
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

        Cursor asses = dbAdapter.getAssignments();
        Log.d("ass", "asses in db: "+asses.getCount()+" course: "+studyTask.getType());

        if(studyTask.getType().equals(AssignmentType.READ)){
            listOfReadAssignments.removeAllViews();
            listOfReadAssignments.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);
        }
        else{
            listOfTasks.removeAllViews();
            listOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.OTHER);
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
            // TODO Auto-generated method stub



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
