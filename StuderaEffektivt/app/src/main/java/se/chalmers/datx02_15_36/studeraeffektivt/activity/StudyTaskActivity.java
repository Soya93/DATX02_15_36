package se.chalmers.datx02_15_36.studeraeffektivt.activity;

/*
Saker att fixa är:

Verkar inte hämta från databasen? gjorde det innan men inte nu, detta gäller för både courseDetailedInfoFrag och StudyTaskActivity, den andra button säger att det inte finns något där
Göra så att bockade hamnar sist och obockade först.
Uppdatera då man kryssar av en ruta, någon sortering
göra så att något händer då man kryssar i en ruta. dI databas och för den specifika studytasken
göra så att man inte kan lägga till flera likadana uppgifter
Fixa så att man kan klicka "lägg till" utan att man skrivit något, nu krachar det
databasen töms då man tar bort en uppgift
 */

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
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
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

public class StudyTaskActivity extends ActionBarActivity {

    //Components in the view
    private EditText taskInput;
    private Button addButton;
    private EditText taskParts;
    private FlowLayout listOfTasks;
    private FlowLayout listOfReadAssignments;
    private Spinner chapterSpinner;
    private ToggleButton readOrTaskAssignment;

    private String courseCode;

    private HashMap<Integer, ArrayList<StudyTask>> hashMapOfStudyTasks;
    private HashMap<Integer, ArrayList<StudyTask>> hashMapOfReadingAssignments;

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

        hashMapOfStudyTasks = new HashMap();
        hashMapOfReadingAssignments = new HashMap<>();

        initComponents();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       //finish();
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
        readOrTaskAssignment = (ToggleButton) findViewById(R.id.readOrTaskAssignment);

        Integer[] items = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        chapterSpinner.setAdapter(adapter);

        addTasksFromDatabase();

        addButton.setOnClickListener(myOnlyhandler);


    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            if ((v) == addButton) {
                int chapter = Integer.parseInt(chapterSpinner.getSelectedItem().toString());
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
                    int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                    StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, elementToAdd, 0, 0, dbAdapter, AssignmentType.OTHER, AssignmentStatus.UNDONE);
                    addToListOfTasks(studyTask);
                    addToDatabase(studyTask);
                }
            }
        }
        //lägger till huvuduppgifterna då deluppgifter inte finns
        else {
            for (String s : stringList) {         //För varje huvuduppgift
                int randomNum = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
                StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, s, 0, 0, dbAdapter, AssignmentType.OTHER, AssignmentStatus.UNDONE);
                addToListOfTasks(studyTask);
                addToDatabase(studyTask);
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

            StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, "ReadAssignment", start, end, dbAdapter, AssignmentType.READ, AssignmentStatus.UNDONE);

            addToDatabase(studyTask);
            addToListOfTasks(studyTask);

        } else {

            StudyTask studyTask = new StudyTask(this, randomNum, courseCode, chapter, "ReadAssignment", Integer.parseInt(taskString), Integer.parseInt(taskString), dbAdapter, AssignmentType.READ, AssignmentStatus.UNDONE);

            addToDatabase(studyTask);
            addToListOfTasks(studyTask);
        }

    }

    public void addToListOfTasks(StudyTask studyTask) {

        //initCheckbox(studyTask);

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
                studyTask.getTaskString(),
                studyTask.getStartPage(),
                studyTask.getEndPage(),
                studyTask.getType(),
                studyTask.getStatus()
        );

        Log.d("Lägga till element i databas: ", "" + dbAdapter.getAssignments().getCount());
    }



    public void addTasksFromDatabase() {

        Cursor cursor = dbAdapter.getAssignments();

        ArrayList<StudyTask> checkedArray = new ArrayList<>();
        ArrayList<StudyTask> uncheckedArray = new ArrayList<>();

        TextView kapitelText = new TextView(this);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                AssignmentStatus assignmentStatus;
                AssignmentType assignmentType;
                if(cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())){
                    assignmentStatus = AssignmentStatus.DONE;
                }
                else{
                    assignmentStatus = null;
                }
                if(cursor.getString(cursor.getColumnIndex("type")).equals(AssignmentType.READ.toString())){
                    assignmentType = AssignmentType.READ;
                }
                else{
                    assignmentType = AssignmentType.OTHER;
                }

                StudyTask studyTask = new StudyTask(
                        this,
                        cursor.getInt(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("_ccode")),
                        cursor.getInt(cursor.getColumnIndex("chapter")),
                        cursor.getString(cursor.getColumnIndex("assNr")),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("startPage"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("stopPage"))),
                        dbAdapter,
                        assignmentType,
                        assignmentStatus);


                //initCheckbox(studyTask);

                if (studyTask.isChecked()) {
                    checkedArray.add(studyTask);
                } else
                    uncheckedArray.add(studyTask);

                if(hashMapOfStudyTasks.containsKey(studyTask.getChapter())){
                    hashMapOfStudyTasks.get(studyTask.getChapter()).add(studyTask);
                }
                else{
                    ArrayList<StudyTask> a = new ArrayList();
                    a.add(studyTask);
                    hashMapOfStudyTasks.put(studyTask.getChapter(),a);
                }
                if(hashMapOfReadingAssignments.containsKey(studyTask.getChapter())){
                    hashMapOfReadingAssignments.get(studyTask.getChapter()).add(studyTask);
                }
                else{
                    ArrayList<StudyTask> a = new ArrayList();
                    a.add(studyTask);
                    hashMapOfReadingAssignments.put(studyTask.getChapter(),a);
                }
            }

            listOfTasks.addMap(hashMapOfStudyTasks);
            listOfReadAssignments.addMap(hashMapOfReadingAssignments);

           /* for (Object value : hashMapOfStudyTasks.values()) {
                ArrayList<StudyTask> a = (ArrayList) value;
                kapitelText.setText("KAPITEL " + a.get(0).getChapter());
                kapitelText = new TextView(this);
                listOfTasks.addView(kapitelText);
                for(int i = 0; i < a.size(); i++){
                   listOfTasks.addView(a.get(i));
                }
            }*/

            /*for (StudyTask s : uncheckedArray) {
                if(s.getType() == AssignmentType.READ) {
                    listOfReadAssignments.removeView(s);
                    listOfReadAssignments.addView(s);
                }
                if(s.getType() == AssignmentType.OTHER) {
                    listOfTasks.removeView(s);
                    listOfTasks.addView(s);
                }
            }

            for (StudyTask s : checkedArray) {
                if(s.getType() == AssignmentType.READ) {
                    listOfReadAssignments.removeView(s);
                    listOfReadAssignments.addView(s);
                }
                if(s.getType() == AssignmentType.OTHER) {
                    listOfTasks.removeView(s);
                    listOfTasks.addView(s);
                }
            }*/
        }
    }

   /* public void initCheckbox(final StudyTask studyTask){
        studyTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (buttonView.isChecked()) {
                    dbAdapter.setDone(studyTask.getIdNr());
                } else {
                    dbAdapter.setUndone(studyTask.getIdNr());
                }
            }
        });

        studyTask.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(StudyTaskActivity.this, studyTask);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_remove_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(studyTask.getType() == AssignmentType.READ)
                            listOfReadAssignments.removeView(studyTask);
                        else
                            listOfTasks.removeView(studyTask);

                        dbAdapter.deleteAssignment(studyTask.getIdNr());
                        Toast.makeText(StudyTaskActivity.this,"Uppgift borttagen",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu
                return true;
            }
        });

        studyTask.setLongClickable(true);
        studyTask.setClickable(true);
    }*/
}
