package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.LabAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OtherAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ProblemAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;

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
public class CourseAssignmentsActivity extends ActionBarActivity {

    private AssignmentCheckBoxLayout assignmentsFlowLayout;

    private String courseCode;
    private String courseName;
    private Spinner assignmentTypesSpinner;

    //The access point of the database.
    private HandInAssignmentsDBAdapter handInDB;
    private LabAssignmentsDBAdapter labDB;
    private OtherAssignmentsDBAdapter otherDB;
    private ProblemAssignmentsDBAdapter problemDB;
    private ReadAssignmentsDBAdapter readDB;
    private CoursesDBAdapter courseDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_assignments);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        courseName = getIntent().getStringExtra("CourseName");
        courseCode = getIntent().getStringExtra("CourseCode");
        actionBar.setTitle("Uppgifter i " + courseName);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));


        handInDB = new HandInAssignmentsDBAdapter(this);
        labDB = new LabAssignmentsDBAdapter(this);
        otherDB = new OtherAssignmentsDBAdapter(this);
        problemDB = new ProblemAssignmentsDBAdapter(this);
        readDB = new ReadAssignmentsDBAdapter(this);
        courseDB = new CoursesDBAdapter(this);
        initComponents();
    }

    public void initComponents() {
        assignmentsFlowLayout = (AssignmentCheckBoxLayout) findViewById(R.id.assignmentsFlowLayout);
        assignmentTypesSpinner = (Spinner) findViewById(R.id.assignmentTypesSpinner);
        setAssignmentTypeSpinner();

        if(assignmentTypesSpinner.getSelectedItem()!=null) {
            updateAssignmentsLayout();
        }
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAssignmentTypeSpinner(){
        String[] assignmentTypes = new String[]{AssignmentType.HANDIN.toString(), AssignmentType.LAB.toString(), AssignmentType.PROBLEM.toString(), AssignmentType.READ.toString(), AssignmentType.OBLIGATORY.toString(), AssignmentType.OTHER.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, assignmentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignmentTypesSpinner.setAdapter(adapter);
        assignmentTypesSpinner.setSelection(0);
    }

    private void updateAssignmentsLayout() {
        AssignmentType assignmentType = AssignmentType.OTHER;
        assignmentsFlowLayout.removeAllViews();

        if (assignmentTypesSpinner.getSelectedItem().toString().equals(AssignmentType.HANDIN.toString())) {
            assignmentsFlowLayout.addHandInsFromDatabase(courseCode, handInDB);
            assignmentType = AssignmentType.HANDIN;

        } else if (assignmentTypesSpinner.getSelectedItem().toString().equals(AssignmentType.LAB.toString())) {
            assignmentsFlowLayout.addLabsFromDatabase(courseCode, labDB);
            assignmentType = AssignmentType.LAB;

        } else if (assignmentTypesSpinner.getSelectedItem().toString().equals(AssignmentType.PROBLEM.toString())) {
            assignmentsFlowLayout.addProblemsFromDatabase(courseCode, problemDB);
            assignmentType = AssignmentType.PROBLEM;

        } else if (assignmentTypesSpinner.getSelectedItem().toString().equals(AssignmentType.READ.toString())) {
            assignmentsFlowLayout.addReadsFromDatabase(courseCode, readDB);
            assignmentType = AssignmentType.READ;

        } else if (assignmentTypesSpinner.getSelectedItem().toString().equals(AssignmentType.OBLIGATORY.toString())) {
            assignmentsFlowLayout.addObligatoriesFromDatabase(courseCode, courseDB);
            assignmentType = AssignmentType.OBLIGATORY;

        } else if (assignmentTypesSpinner.getSelectedItem().toString().equals(AssignmentType.OTHER.toString())) {
            assignmentsFlowLayout.addOthersFromDatabase(courseCode, otherDB);
            assignmentType = AssignmentType.OTHER;
        }
        isAssignmentsLayoutEmpty(assignmentType);
    }

    private void isAssignmentsLayoutEmpty(AssignmentType type){
        if(assignmentsFlowLayout.isEmpty()){
            TextView textView = new TextView(this);
            textView.setText("Du har för närvaranade inga " + type.toString().toLowerCase() + " för den här kursen, lägg till en uppgift genom att trycka på 'Lägg till uppgifter' i menyvalet.");
            textView.setPadding(15,5,15,5);
            assignmentsFlowLayout.addView(textView);
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


    public void onResume() {
        super.onResume();
    }
}
