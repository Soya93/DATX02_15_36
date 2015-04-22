package se.chalmers.datx02_15_36.studeraeffektivt.activity;

/*
Saker att fixa är:

Uppdatera då man kryssar av en ruta, någon sortering
 */

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

public class StudyTaskActivity extends ActionBarActivity {

    //Components in the view
    private EditText taskInput;
    private Button addButton;
    private EditText taskParts;
    private FlowLayout listOfTasks;
    private FlowLayout listOfReadAssignments;
    private Spinner chapterSpinner;
    private Spinner courseSpinner;
    private Spinner weekSpinner;
    private ToggleButton readOrTaskAssignment;

    private String courseCode;

    //The access point of the database.
    private DBAdapter dbAdapter;

    private int chosenWeek = Utils.getCurrWeekNumber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_task);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initComponents() {
        addButton = (Button) findViewById(R.id.addButton);
        taskInput = (EditText) findViewById(R.id.taskInput);
        taskParts = (EditText) findViewById(R.id.taskParts);
        listOfTasks = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);
        listOfReadAssignments = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfReadingAssignments);
        chapterSpinner = (Spinner) findViewById(R.id.chapterSpinner);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
        readOrTaskAssignment = (ToggleButton) findViewById(R.id.readOrTaskAssignment);

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
        setSelectedCourse();

        listOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.OTHER);
        listOfReadAssignments.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);

        addButton.setOnClickListener(myOnlyhandler);

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

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            if ((v) == addButton) {
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

        }
    };

    private void setCourses() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);
        Cursor cursor = dbAdapter.getCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");
        while (cursor.moveToNext()) {
            String ccode = cursor.getString(ccodeColumn);
            String cname = cursor.getString(cnameColumn);
            adapter.add(ccode + "-" + cname);
        }

    }


    public void setSelectedCourse() {
        String temp = courseSpinner.getSelectedItem().toString();
        String[] parts = temp.split("-");
        this.courseCode = parts[0];
        Log.d("selected course", courseCode);

        listOfTasks.removeAllViews();
        listOfReadAssignments.removeAllViews();
        listOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.OTHER);
        listOfReadAssignments.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);
    }

    //Metod för att lägga till en uppgift

    public void addTask(int chapter, String taskString, String taskParts) {

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
                    if(!listOfTasks.contains(chapter, elementToAdd)) {
                        int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                        StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, elementToAdd, 0, 0, dbAdapter, AssignmentType.OTHER, AssignmentStatus.UNDONE);
                        addToListOfTasks(studyTask);
                        addToDatabase(studyTask);
                    }
                    else{
                        Toast.makeText(this,"Uppgift redan tillagd!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        //lägger till huvuduppgifterna då deluppgifter inte finns
        else {
            for (String s : stringList) {         //För varje huvuduppgift
                if(!listOfTasks.contains(chapter, s)) {
                    int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                    StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, s, 0, 0, dbAdapter, AssignmentType.OTHER, AssignmentStatus.UNDONE);
                    addToListOfTasks(studyTask);
                    addToDatabase(studyTask);
                }
                else{
                    Toast.makeText(this,"Uppgift redan tillagd!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void addReadAssignment(int chapter, String taskString) {

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
                StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, "ReadAssignment", start, end, dbAdapter, AssignmentType.READ, AssignmentStatus.UNDONE);

                addToDatabase(studyTask);
                addToListOfTasks(studyTask);
            }
            else{
                Toast.makeText(this,"Läsanvisning redan tillagd!",Toast.LENGTH_SHORT).show();
            }

        } else {
            if(!(listOfReadAssignments.contains(chapter, Integer.parseInt(taskString), Integer.parseInt(taskString)))) {

                StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, chosenWeek, "ReadAssignment", Integer.parseInt(taskString), Integer.parseInt(taskString), dbAdapter, AssignmentType.READ, AssignmentStatus.UNDONE);

                addToDatabase(studyTask);
                addToListOfTasks(studyTask);
            }
            else{
                Toast.makeText(this,"Läsanvisning redan tillagd!",Toast.LENGTH_SHORT).show();
            }
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

        Log.d("Lägga till element i databas: ", "" + dbAdapter.getAssignments().getCount());
    }
}
