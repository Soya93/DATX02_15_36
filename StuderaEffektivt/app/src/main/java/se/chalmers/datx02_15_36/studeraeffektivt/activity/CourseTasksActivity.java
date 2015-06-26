package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
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

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.view.CourseView;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

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
/**
 * Created by SoyaPanda on 15-03-06.
 */
public class CourseTasksActivity extends ActionBarActivity {

    private FlowLayout assignmentsFlowLayout;

    private String courseCode;
    private String courseName;
    private Spinner assignmentTypesSpinner;
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_assignments);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        courseName = getIntent().getStringExtra("CourseName");
        courseCode = getIntent().getStringExtra("CourseCode");
        actionBar.setTitle("Uppgifter i kursen " + courseName);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }
        initComponents();
    }

    public void initComponents() {
        assignmentsFlowLayout = (FlowLayout) findViewById(R.id.assignmentsFlowLayout);
        assignmentTypesSpinner = (Spinner) findViewById(R.id.assignmentTypesSpinner);
        setAssignmentTypeSpinner();

        if(assignmentTypesSpinner.getSelectedItem()!=null) {
            updateAssignmentsLayout();
        }
    }

    private void setAssignmentTypeSpinner(){
        String[] assignmentTypes = new String[]{AssignmentType.READ.toString(), AssignmentType.OTHER.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, assignmentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignmentTypesSpinner.setAdapter(adapter);
        assignmentTypesSpinner.setSelection(0);
    }


    private void updateAssignmentsLayout(){
        assignmentsFlowLayout.removeAllViews();

        Log.i("selected in spinner", assignmentTypesSpinner.getSelectedItem() + "");
        Log.i("string for asstype", AssignmentType.READ.toString());

        if(assignmentTypesSpinner.getSelectedItem().toString().equals(AssignmentType.READ.toString())) {
            assignmentsFlowLayout.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);

        } else if(assignmentTypesSpinner.getSelectedItem().equals(AssignmentType.OTHER.toString())) {
            assignmentsFlowLayout.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.OTHER);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (assignmentTypesSpinner != null) {
            assignmentTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateAssignmentsLayout();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_tasks, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.action_download) {
            getAssignmetsFromWeb();
        } else if (id == R.id.action_add) {
            goToAddTasks();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
    }


    public void goToAddTasks() {
        Intent i = new Intent(this, StudyTaskActivity.class);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);
    }

    public void getAssignmetsFromWeb() {
        Intent i = new Intent(this, GetAssignmentsFromWeb.class);
        i.putExtra("CourseName", courseName);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);
    }
}
