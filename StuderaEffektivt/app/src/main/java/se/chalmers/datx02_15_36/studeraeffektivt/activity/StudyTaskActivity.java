package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;

public class StudyTaskActivity extends ActionBarActivity {

    //Components in the view
    private EditText taskInput;
    private TextView taskOutput;
    private Button addButton;
    private Button deleteButton;
    private EditText chapterEditText;
    private EditText taskParts;
    private ScrollView scrollViewOfTasks;
    private LinearLayout layoutWithinScrollViewOfTasks;
    private Spinner chapterSpinner;

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

        initComponents();

       // bundleFromPreviousFragment = this.getArguments();



        //Create the database access point but check if the context is null first.
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }


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

    public void initComponents(){
        addButton = (Button) findViewById(R.id.addButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        taskInput = (EditText) findViewById(R.id.taskInput);
        //taskOutput = (TextView) findViewById(R.id.taskOutput);
        chapterEditText = (EditText) findViewById(R.id.chapterEditText);
        taskParts = (EditText) findViewById(R.id.taskParts);
        scrollViewOfTasks = (ScrollView) findViewById(R.id.scrollViewOfTasks);
        layoutWithinScrollViewOfTasks = (LinearLayout) findViewById(R.id.layoutWithinScrollViewOfTasks);
        chapterSpinner = (Spinner) findViewById(R.id.chapterSpinner);

        Integer[] items = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items);
        chapterSpinner.setAdapter(adapter);

        Cursor cursor = dbAdapter.getAssignments();

        while(cursor.moveToNext()){
            layoutWithinScrollViewOfTasks.addView(new StudyTask(this,cursor.getString(cursor.getColumnIndex("ASSIGNMENTS_ccode")),cursor.getInt(cursor.getColumnIndex("ASSIGNMENTS_chapter")),cursor.getString(cursor.getColumnIndex("ASSIGNMENTS__assNr"))));
        }

        addButton.setOnClickListener(myOnlyhandler);
        deleteButton.setOnClickListener(myOnlyhandler);



    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            if(((Button) v)==addButton) {
                int chapter = Integer.parseInt(chapterSpinner.getSelectedItem().toString());
                /*if (chapterEditText.getText().equals("")) {
                    chapter = 999;
                } else {
                    chapter = Integer.parseInt(chapterEditText.getText().toString());
                }*/
                addTask(chapter, taskInput.getText().toString(), taskParts.getText().toString());
            }
            else if(((Button) v)==deleteButton){
                deleteTask(taskInput.getText().toString());
            }

        }
    };

    //Meod för att lägga till en uppgift

    public void addTask(int chapter, String taskString, String taskParts) {
        //StudyTask studytask = new StudyTask(taskInput.getText().toString());
        //ArrayList<String> studyTaskList = studytask.getlist();

        ArrayList<String> stringlist = new ArrayList();
        String[] separateLine;
        String[] separateComma;
        String[] separateTaskParts;
        separateTaskParts = taskParts.split("");



        taskString.replaceAll("\\s+", "");

        int start;
        int end;

        //Kolla om kapitlet man vill lägga till i redan finns bland uppgifterna, om inte: skapa en nyckel för detta kapitel

        /*if (taskMap.containsKey(chapter)) {
            studyTaskList = taskMap.get(chapter);
        } else {
            studyTaskList = new ArrayList<>();
        }*/

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
                    /*if (!studyTaskList.contains(elementToAdd)) {
                        studyTaskList.add(new StudyTask(this,
                                this.bundleFromPreviousFragment.getString("CourseCode"),
                                chapter,
                                elementToAdd));             //Lägger till dessa i listan för det aktuella kapitlet och elementet inte finns.*/
                        addToDatabase(chapter, elementToAdd);
                    scrollViewOfTasks.addView(new StudyTask(this, courseCode, chapter, elementToAdd));
                   // }
                }
            }
        }
        //lägger till huvuduppgifterna då deluppgifter inte finns
        else {
            for (String s : stringlist) {         //För varje huvuduppgift
                /*if (!studyTaskList.contains(s)) {  //Lägg till om den inte redan finns
                    studyTaskList.add(new StudyTask(this,
                            this.bundleFromPreviousFragment.getString("CourseCode"),
                            chapter,
                            s));*/
                    addToDatabase(chapter, s);
               // }
            }

            taskMap.put(chapter, studyTaskList);        //Uppdatera Hashmappen för nyckeln för kapitlet

            String taskMapString = taskMap.toString();

            Log.d("String för taskMap: ", taskMapString);

        }
    }

    public void deleteTask(String taskString){
        Cursor cursor = dbAdapter.getAssignments();
        Log.d("Data hämtat från databasen: ", "" + cursor.getCount());
    }

    public void addToDatabase(int chapter, String elementToAdd){
        dbAdapter.insertAssignment(courseCode, chapter, elementToAdd, 0, 1);
        layoutWithinScrollViewOfTasks.addView(new StudyTask(this, courseCode, chapter, elementToAdd));
    }
}
