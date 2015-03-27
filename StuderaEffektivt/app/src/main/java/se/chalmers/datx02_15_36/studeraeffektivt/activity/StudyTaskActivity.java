package se.chalmers.datx02_15_36.studeraeffektivt.activity;

/*
Saker att fixa är:

Verkar inte hämta från databasen? gjorde det innan men inte nu, detta gäller för både courseDetailedInfoFrag och StudyTaskActivity, den andra button säger att det inte finns något där
Göra så att bockade hamnar sist och obockade först.
Ställa in så att man inte kan skriva in vad som helst i inputrutorna
Uppdatera då man kryssar av en ruta, någon sortering
 */

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

public class StudyTaskActivity extends ActionBarActivity {

    //Components in the view
    private EditText taskInput;
    private TextView taskOutput;
    private Button addButton;
    private Button deleteButton;
    private EditText taskParts;
    //private LinearLayout layoutWithinScrollViewOfTasks;
    private FlowLayout listOfTasks;
    private FlowLayout listOfReadAssignments;
    private Spinner chapterSpinner;
    private ToggleButton readOrTaskAssignment;

    private Intent intentFromPreviousFragment;

    //The bundle given from the fragment before this
    private Bundle bundleFromPreviousFragment;
    private String courseCode;

    //HashMap with the cahpters as keys and a list of tasks as the elements.
    private HashMap<Integer, ArrayList<StudyTask>> taskMap = new HashMap<>();
    ArrayList<StudyTask> studyTaskList;

    private View view;

    //The access point of the database.
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_task);

        courseCode = getIntent().getExtras().getString("CourseCode");

        //Create the database access point but check if the context is null first.
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }

        initComponents();
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
        deleteButton = (Button) findViewById(R.id.deleteButton);
        taskInput = (EditText) findViewById(R.id.taskInput);
        taskParts = (EditText) findViewById(R.id.taskParts);
        listOfTasks = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);
        listOfReadAssignments = (FlowLayout) findViewById(R.id.layoutWithinScrollViewOfReadingAssignments);
        chapterSpinner = (Spinner) findViewById(R.id.chapterSpinner);
        readOrTaskAssignment = (ToggleButton) findViewById(R.id.readOrTaskAssignment);

        Integer[] items = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, items);
        chapterSpinner.setAdapter(adapter);

        addTasksFromDatabase();

        addButton.setOnClickListener(myOnlyhandler);
        deleteButton.setOnClickListener(myOnlyhandler);


    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            if (((Button) v) == addButton) {
                int chapter = Integer.parseInt(chapterSpinner.getSelectedItem().toString());
                if(readOrTaskAssignment.isChecked()) {
                    addTask(chapter, taskInput.getText().toString(), taskParts.getText().toString());
                }
                else{
                    addReadAssignment(chapter, taskInput.getText().toString());
                }
            } else if (((Button) v) == deleteButton) {
                deleteTask(taskInput.getText().toString());
            }

        }
    };

    //Metod för att lägga till en uppgift

    public void addTask(int chapter, String taskString, String taskParts) {

        ArrayList<String> stringlist = new ArrayList();
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
        for (int a = 0; a < separateComma.length; a++) {
            if (separateComma[a].contains("-")) {

                separateLine = separateComma[a].split("-");   //Delar upp stringen till en array med elementen mellan bindesstrecken
                start = Integer.parseInt(separateLine[0]);    //Start och end är intervallet för de element som skall läggas till
                end = Integer.parseInt(separateLine[separateLine.length - 1]);

                for (int i = start; i <= end; i++) {
                    stringlist.add("" + i);
                }
            } else {
                stringlist.add(separateComma[a]);
            }
        }

        //Lägger till deluppgifter om input för detta finns, tex a, b c.
        String elementToAdd;
        if (separateTaskParts.length > 1) {
            for (int i = 1; i < separateTaskParts.length; i++) {       //För varje deluppgift
                for (String s2 : stringlist) {                         //För varje vihuv uppgift
                    elementToAdd = s2 + separateTaskParts[i];       //Sätt ihop dessa Huvuduppgift 1 och deluppgift a blir 1a
                    StudyTask studyTask = new StudyTask(this, courseCode, chapter, elementToAdd, dbAdapter, AssignmentType.OTHER, null);
                    addToListOfTasks(studyTask);
                    addToDatabase(studyTask);

                    // }
                }
            }
        }
        //lägger till huvuduppgifterna då deluppgifter inte finns
        else {
            for (String s : stringlist) {         //För varje huvuduppgift
                StudyTask studyTask = new StudyTask(this, courseCode, chapter, s, dbAdapter, AssignmentType.OTHER, null);
                addToListOfTasks(studyTask);
                addToDatabase(studyTask);
                // }
            }

            taskMap.put(chapter, studyTaskList);        //Uppdatera Hashmappen för nyckeln för kapitlet

            String taskMapString = taskMap.toString();

            Log.d("String för taskMap: ", taskMapString);

        }
    }

    public void addReadAssignment(int chapter, String taskString) {

        String[] separateLine;

        int start;
        int end;

        if (taskString.contains("-")) {

            separateLine = taskString.split("-");   //Delar upp stringen till en array med elementen mellan bindesstrecken
            start = Integer.parseInt(separateLine[0]);    //Start och end är intervallet för de element som skall läggas till
            end = Integer.parseInt(separateLine[separateLine.length - 1]);

            StudyTask studyTask = new StudyTask(this, courseCode, chapter, start, end, dbAdapter, AssignmentType.READ, null);

            addToDatabase(studyTask);
            addToListOfTasks(studyTask);
            //dbAdapter.insertAssignment(courseCode, chapter, null, start, end, AssignmentType.READ, null);
            //listOfReadAssignments.addView(studyTask);

        } else {

            StudyTask studyTask = new StudyTask(this, courseCode, chapter, Integer.parseInt(taskString), Integer.parseInt(taskString), dbAdapter, AssignmentType.READ, null);

            addToDatabase(studyTask);
            addToListOfTasks(studyTask);
            //dbAdapter.insertAssignment(courseCode, chapter, null, Integer.parseInt(taskString), Integer.parseInt(taskString), AssignmentType.READ, null);
            //listOfReadAssignments.addView(new StudyTask(this, courseCode, chapter, Integer.parseInt(taskString), Integer.parseInt(taskString), dbAdapter, false));
        }

    }

    public void deleteTask(String taskString) {
        //TODO: Kolla om den finns i databasen och ta bort den
        Cursor cursor = dbAdapter.getAssignments();
        Log.d("Data hämtat från databasen: ", "" + cursor.getCount());
    }

    /*public void addToListOfTasks(int chapter, String elementToAdd) {
        StudyTask studyTask = new StudyTask(this, courseCode, chapter, elementToAdd, dbAdapter, , false); //TODO: sätta in rätt bool från databasen
        //studyTask.setLongClickable(true);
        listOfTasks.addView(studyTask);
    }*/

    public void addToListOfTasks(StudyTask studyTask) {

        if(studyTask.getType().equals(AssignmentType.READ)){
            listOfReadAssignments.addView(studyTask);
        }
        else{
            listOfTasks.addView(studyTask);
        }
    }

    /*public void addToDatabase(int chapter, String elementToAdd, int startPage, int endPage) {

        dbAdapter.insertAssignment(courseCode, chapter, elementToAdd, startPage, endPage, AssignmentType.OTHER, null);
    }*/

    public void addToDatabase(StudyTask studyTask) {

        dbAdapter.insertAssignment(
                studyTask.getCourseCode(),
                studyTask.getChapter(),
                studyTask.getTaskString(),
                studyTask.getStartPage(),
                studyTask.getEndPage(),
                studyTask.getType(),
                studyTask.getStatus());
    }

    public void addTasksFromDatabase() {

        Cursor cursor = dbAdapter.getAssignments();

        ArrayList<StudyTask> checkedArray = new ArrayList<>();
        ArrayList<StudyTask> uncheckedArray = new ArrayList<>();

        boolean done;

        if (cursor != null) {
            while (cursor.moveToNext()) {


                if (cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE))
                    done = true;
                else
                    done = false;

                AssignmentStatus assignmentStatus;
                AssignmentType assignmentType;
                if(cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())){
                    assignmentStatus = AssignmentStatus.DONE;
                }
                else{
                    assignmentStatus = null;
                }
                if(cursor.getString(cursor.getColumnIndex("type")).equals(AssignmentType.READ)){
                    assignmentType = AssignmentType.READ;
                }
                else{
                    assignmentType = AssignmentType.OTHER;
                }

                StudyTask studyTask = new StudyTask(
                        this,
                        cursor.getString(cursor.getColumnIndex("_ccode")),
                        cursor.getInt(cursor.getColumnIndex("chapter")),
                        cursor.getString(cursor.getColumnIndex("assNr")),
                        dbAdapter,
                        assignmentType,
                        assignmentStatus);

                if (studyTask.isChecked()) {
                    checkedArray.add(studyTask);
                } else
                    uncheckedArray.add(studyTask);

                for (StudyTask s : checkedArray) {
                    listOfTasks.addView(s);
                }
                for (StudyTask s : uncheckedArray) {
                    listOfTasks.addView(s);
                }

            }
        }
    }
}
