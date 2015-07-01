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

package se.chalmers.datx02_15_36.studeraeffektivt.view;

/**
 * Created by jesper on 2015-03-27.
 */

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.LabAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OtherAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ProblemAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.AssignmentCheckBoxes.AssignmentCheckBox;
import se.chalmers.datx02_15_36.studeraeffektivt.model.AssignmentCheckBoxes.HandInCheckBox;
import se.chalmers.datx02_15_36.studeraeffektivt.model.AssignmentCheckBoxes.LabCheckBox;
import se.chalmers.datx02_15_36.studeraeffektivt.model.AssignmentCheckBoxes.OtherCheckBox;
import se.chalmers.datx02_15_36.studeraeffektivt.model.AssignmentCheckBoxes.ProblemCheckBox;
import se.chalmers.datx02_15_36.studeraeffektivt.model.AssignmentCheckBoxes.ReadCheckBox;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CheckedStudyTaskToDB;
import se.chalmers.datx02_15_36.studeraeffektivt.model.OldStudyTask;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;


/**
 * A view container with layout behavior like that of the Swing FlowLayout.
 * Originally from http://nishantvnair.wordpress.com/2010/09/28/flowlayout-in-android/
 *
 */
public class AssignmentCheckBoxLayout extends ViewGroup {
    private final static int PAD_H = 2, PAD_V = 2; // Space between child views.
    private int mHeight;

    private HashMap<String, ArrayList<AssignmentCheckBox>> handInHashMap;
    private HashMap<String, ArrayList<AssignmentCheckBox>> labHashMap;
    private HashMap<String, ArrayList<AssignmentCheckBox>> otherHashMap;
    private HashMap<String, ArrayList<AssignmentCheckBox>> problemHashMap;
    private HashMap<String, ArrayList<AssignmentCheckBox>> readHashMap;

    private HashMap<Integer, ArrayList<CheckedStudyTaskToDB>> hashMapOfStudyTasksForWeb = new HashMap<>();
    private HashMap<Integer, ArrayList<CheckedStudyTaskToDB>> hashMapOfReadingAssignmentsWeb = new HashMap<>();

    public AssignmentCheckBoxLayout(Context context) {
        super(context);
    }

    public AssignmentCheckBoxLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);
        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int childHeightMeasureSpec;
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        else
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mHeight = 0;
        for(int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() != GONE) {
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                final int childw = child.getMeasuredWidth();
                mHeight = Math.max(mHeight, child.getMeasuredHeight() + PAD_V);
                if(xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += mHeight;
                }
                xpos += childw + PAD_H;
            }
        }
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + mHeight;
        } else if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if(ypos + mHeight < height) {
                height = ypos + mHeight;
            }
        }
        height += 5; // Fudge to avoid clipping bottom of last row.
        setMeasuredDimension(width, height);
    } // end onMeasure()

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        for(int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                if(xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += mHeight;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + PAD_H;
            }
        }
    } // end onLayout()


    public boolean contains(String sortingString, String taskString){
        for(int i = 0; i < this.getChildCount(); i++){
            if(
                    !(getChildAt(i).getClass().equals(TextView.class)) &&
                            ((AssignmentCheckBox) getChildAt(i)).getSortingString().equals(sortingString) &&
                            ((AssignmentCheckBox) getChildAt(i)).getTaskString().equals(taskString)){

                return true;
            }

        }
        return false;
    }


    public boolean contains(String sortingString, String startPage, String endPage){
        for(int i = 0; i < this.getChildCount(); i++){
            String taskString = ((AssignmentCheckBox) getChildAt(i)).getTaskString();
            String checkBoxStartPage = taskString.split("-")[0];
            String checkBoxEndPage = taskString.split("-")[1];


            if(!(getChildAt(i).getClass().equals(TextView.class)) &&
                            ((AssignmentCheckBox) getChildAt(i)).getSortingString().equals(sortingString) &&
                            checkBoxStartPage.equals(startPage) &&
                                checkBoxEndPage.equals(endPage)){

                return true;
            }

        }
        return false;
    }

    public void addMap(HashMap<String, ArrayList<AssignmentCheckBox>> hashMap, AssignmentType type){
        Map<String, ArrayList> treeMap = new TreeMap<String, ArrayList>(hashMap);
        for (Object value : treeMap.values()) {
            ArrayList<AssignmentCheckBox> a = (ArrayList) value;
            TextView sortingText = new TextView(this.getContext());

            switch (type) {
                case HANDIN: case LAB:
                    sortingText.setText("  Nummer " + a.get(0).getSortingString());

                case OTHER:
                    sortingText.setText("  Vecka " + a.get(0).getSortingString());

                case PROBLEM: case READ:
                    sortingText.setText("  Kapitel " + a.get(0).getSortingString());
                default:
            }
            int width = this.getWidth();
            sortingText.setWidth(width);
            this.addView(sortingText);
            int counter = this.getChildCount();
            for(int i = 0; i < a.size(); i++){

                if(a.get(i).getParent()!=null){
                    ((ViewGroup) a.get(i).getParent()).removeView(a.get(i));
                }
                if(!a.get(i).isChecked()) {
                    this.addView(a.get(i), counter);
                    counter++;
                }
                else{
                    this.addView(a.get(i),counter);
                }
            }
        }
    }

    public void addHandInsFromDatabase(String courseCode, HandInAssignmentsDBAdapter handInAssignmentsDBAdapter) {
        handInHashMap = new HashMap<>();
        Cursor cursor = handInAssignmentsDBAdapter.getAssignments();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("_ccode")).equals(courseCode)) {
                    AssignmentStatus assignmentStatus = cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())? AssignmentStatus.DONE: AssignmentStatus.UNDONE;

                    HandInCheckBox handInCheckBox = new HandInCheckBox(this.getContext(),
                            cursor.getInt(cursor.getColumnIndex("_id")),assignmentStatus);

                    if (handInHashMap.containsKey(handInCheckBox.getSortingString())) {
                        handInHashMap.get(handInCheckBox.getSortingString()).add(handInCheckBox);
                    } else {
                        ArrayList<AssignmentCheckBox> arrayList = new ArrayList();
                        arrayList.add(handInCheckBox);
                        handInHashMap.put(handInCheckBox.getSortingString(), arrayList);
                    }
                }
            }

            if(handInHashMap!=null)
                addMap(handInHashMap, AssignmentType.HANDIN);
        }

    }

    public void addLabsFromDatabase(String courseCode, LabAssignmentsDBAdapter labAssignmentsDBAdapter) {
        labHashMap = new HashMap<>();
        Cursor cursor = labAssignmentsDBAdapter.getAssignments();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("_ccode")).equals(courseCode)) {
                    AssignmentStatus assignmentStatus = cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())? AssignmentStatus.DONE: AssignmentStatus.UNDONE;

                    LabCheckBox labCheckBox = new LabCheckBox(this.getContext(),
                            cursor.getInt(cursor.getColumnIndex("_id")),assignmentStatus);

                    if (labHashMap.containsKey(labCheckBox.getSortingString())) {
                        labHashMap.get(labCheckBox.getSortingString()).add(labCheckBox);
                    } else {
                        ArrayList<AssignmentCheckBox> arrayList = new ArrayList();
                        arrayList.add(labCheckBox);
                        labHashMap.put(labCheckBox.getSortingString(), arrayList);
                    }
                }
            }

            if(labHashMap!=null)
                addMap(labHashMap, AssignmentType.LAB);
        }
    }


    public void addOthersFromDatabase(String courseCode, OtherAssignmentsDBAdapter otherAssignmentsDBAdapter) {
        otherHashMap = new HashMap<>();
        Cursor cursor = otherAssignmentsDBAdapter.getAssignments();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("_ccode")).equals(courseCode)) {
                    AssignmentStatus assignmentStatus = cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())? AssignmentStatus.DONE: AssignmentStatus.UNDONE;

                    OtherCheckBox otherCheckBox = new OtherCheckBox(this.getContext(),
                            cursor.getInt(cursor.getColumnIndex("_id")),assignmentStatus);

                    if (otherHashMap.containsKey(otherCheckBox.getSortingString())) {
                        otherHashMap.get(otherCheckBox.getSortingString()).add(otherCheckBox);
                    } else {
                        ArrayList<AssignmentCheckBox> arrayList = new ArrayList();
                        arrayList.add(otherCheckBox);
                        otherHashMap.put(otherCheckBox.getSortingString(), arrayList);
                    }
                }
            }

            if(otherHashMap!=null)
                addMap(otherHashMap, AssignmentType.OTHER);
        }
    }


    public void addProblemsFromDatabase(String courseCode, ProblemAssignmentsDBAdapter problemAssignmentsDBAdapter) {
        problemHashMap = new HashMap<>();
        Cursor cursor = problemAssignmentsDBAdapter.getAssignments();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("_ccode")).equals(courseCode)) {
                    AssignmentStatus assignmentStatus = cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())? AssignmentStatus.DONE: AssignmentStatus.UNDONE;

                    ProblemCheckBox problemCheckBox = new ProblemCheckBox(this.getContext(),
                            cursor.getInt(cursor.getColumnIndex("_id")),assignmentStatus);

                    if (problemHashMap.containsKey(problemCheckBox.getSortingString())) {
                        problemHashMap.get(problemCheckBox.getSortingString()).add(problemCheckBox);
                    } else {
                        ArrayList<AssignmentCheckBox> arrayList = new ArrayList();
                        arrayList.add(problemCheckBox);
                        problemHashMap.put(problemCheckBox.getSortingString(), arrayList);
                    }
                }
            }

            if(problemHashMap!=null)
                addMap(problemHashMap, AssignmentType.PROBLEM);
        }
    }

    public void addReadsFromDatabase(String courseCode, ReadAssignmentsDBAdapter readAssignmentsDBAdapter) {
        readHashMap = new HashMap<>();
        Cursor cursor = readAssignmentsDBAdapter.getAssignments();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("_ccode")).equals(courseCode)) {
                    AssignmentStatus assignmentStatus = cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())? AssignmentStatus.DONE: AssignmentStatus.UNDONE;

                    ReadCheckBox readCheckBox = new ReadCheckBox(this.getContext(),
                            cursor.getInt(cursor.getColumnIndex("_id")),assignmentStatus);

                    if (readHashMap.containsKey(readCheckBox.getSortingString())) {
                        readHashMap.get(readCheckBox.getSortingString()).add(readCheckBox);
                    } else {
                        ArrayList<AssignmentCheckBox> arrayList = new ArrayList();
                        arrayList.add(readCheckBox);
                        readHashMap.put(readCheckBox.getSortingString(), arrayList);
                    }
                }
            }

            if(readHashMap!=null)
                addMap(readHashMap, AssignmentType.READ);
        }
    }


    public void addTasksFromDatabase(OldAssignmentsDBAdapter assDBAdapter, String courseCode, AssignmentType assignmentType, int week) {
       /* if (cursor != null) {
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("_ccode")).equals(courseCode)
                        && cursor.getString(cursor.getColumnIndex("type")).equals(assignmentType.toString())
                        && cursor.getInt(cursor.getColumnIndex("week")) == week
                        && cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.UNDONE.toString())) {*/

    }
    public boolean isEmpty(){
        return (this.getChildCount()<1);
    }




    //Webstuff
    public void addTasksFromWeb( String courseCode, int chapter, int week, String assNr,
                                 int startPage, int stopPage, String status, String type,OldAssignmentsDBAdapter assDBAdapter) {


        AssignmentStatus assignmentStatus;
        AssignmentType assignmentType;
        if (status.equals(AssignmentStatus.DONE.toString())) {
            assignmentStatus = AssignmentStatus.DONE;
        } else {
            assignmentStatus = AssignmentStatus.UNDONE;
        }
        if (type.equals(AssignmentType.READ.toString())) {
            assignmentType = AssignmentType.READ;
        } else {
            assignmentType = AssignmentType.PROBLEM;
        }

        CheckedStudyTaskToDB studyTask = new CheckedStudyTaskToDB(
                this.getContext(),
                courseCode,
                chapter,
                week,
                assNr,
                startPage,
                stopPage,
                assDBAdapter,
                assignmentType,
                assignmentStatus);
        if (studyTask.getType() == AssignmentType.PROBLEM) {

            if (hashMapOfStudyTasksForWeb.containsKey(studyTask.getChapter())) {
                hashMapOfStudyTasksForWeb.get(studyTask.getChapter()).add(studyTask);
            } else {
                ArrayList<CheckedStudyTaskToDB> a = new ArrayList();
                a.add(studyTask);
                hashMapOfStudyTasksForWeb.put(studyTask.getChapter(), a);
            }

        } else {
            if (hashMapOfReadingAssignmentsWeb.containsKey(studyTask.getChapter())) {
                hashMapOfReadingAssignmentsWeb.get(studyTask.getChapter()).add(studyTask);
            } else {
                ArrayList<CheckedStudyTaskToDB> a = new ArrayList();
                a.add(studyTask);
                hashMapOfReadingAssignmentsWeb.put(studyTask.getChapter(), a);
            }
        }
    }

    public HashMap<Integer, ArrayList<CheckedStudyTaskToDB>> getReadHashMapOfStudyTasks2() {
        return this.hashMapOfReadingAssignmentsWeb;
    }


    public HashMap<Integer, ArrayList<CheckedStudyTaskToDB>> getOtherHashMapOfStudyTask2(){
        return this.hashMapOfStudyTasksForWeb;
    }

    public void addReadAssignmets(){
        Map<Integer, ArrayList> treeMap;
        treeMap = new TreeMap<Integer, ArrayList>(hashMapOfReadingAssignmentsWeb);
        for (Object value : treeMap.values()) {
            ArrayList<CheckedStudyTaskToDB> a = (ArrayList) value;
            Log.d("sizeofMap",a.size()+"");
            TextView kapitelText = new TextView(this.getContext());
            kapitelText.setText("  KAPITEL " + a.get(0).getChapter());
            int width = this.getWidth();
            kapitelText.setWidth(width);
            this.addView(kapitelText);
            int counter = this.getChildCount();
            for(int i = 0; i < a.size(); i++){

                if(a.get(i).getParent()!=null){
                    ((ViewGroup) a.get(i).getParent()).removeView(a.get(i));
                }
                if(!a.get(i).isChecked()) {
                    this.addView(a.get(i), counter);
                    counter++;
                }
                else{
                    this.addView(a.get(i),counter);
                }
            }
        }
        hashMapOfReadingAssignmentsWeb.clear();
    }

    public void addOtherAssignmets(){
        Map<Integer, ArrayList> treeMap;
        treeMap = new TreeMap<Integer, ArrayList>(hashMapOfStudyTasksForWeb);
        for (Object value : treeMap.values()) {
            ArrayList<CheckedStudyTaskToDB> a = (ArrayList) value;
            Log.d("sizeofMap",a.size()+"");
            TextView kapitelText = new TextView(this.getContext());
            kapitelText.setText("  KAPITEL " + a.get(0).getChapter());
            int width = this.getWidth();
            kapitelText.setWidth(width);
            this.addView(kapitelText);
            int counter = this.getChildCount();
            for(int i = 0; i < a.size(); i++){

                if(a.get(i).getParent()!=null){
                    ((ViewGroup) a.get(i).getParent()).removeView(a.get(i));
                }
                if(!a.get(i).isChecked()) {
                    this.addView(a.get(i), counter);
                    counter++;
                }
                else{
                    this.addView(a.get(i),counter);
                }
            }
        }
        hashMapOfStudyTasksForWeb.clear();
    }
}