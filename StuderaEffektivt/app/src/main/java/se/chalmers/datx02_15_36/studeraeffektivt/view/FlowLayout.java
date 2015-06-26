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

import se.chalmers.datx02_15_36.studeraeffektivt.database.AssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CheckedStudyTaskToDB;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;


/**
 * A view container with layout behavior like that of the Swing FlowLayout.
 * Originally from http://nishantvnair.wordpress.com/2010/09/28/flowlayout-in-android/
 *
 */
public class FlowLayout extends ViewGroup {
    private final static int PAD_H = 2, PAD_V = 2; // Space between child views.
    private int mHeight;

    private HashMap<Integer, ArrayList<StudyTask>> hashMapOfStudyTasks;
    private HashMap<Integer, ArrayList<StudyTask>> hashMapOfReadingAssignments;
    private HashMap<Integer, ArrayList<CheckedStudyTaskToDB>> hashMapOfStudyTasks2 = new HashMap<>();
    private HashMap<Integer, ArrayList<CheckedStudyTaskToDB>> hashMapOfReadingAssignments2 = new HashMap<>();

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
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


    public boolean contains(int chapter, String taskString){
        for(int i = 0; i < this.getChildCount(); i++){
            if(
                    !(getChildAt(i).getClass().equals(TextView.class)) &&
                            ((StudyTask) getChildAt(i)).getChapter() == chapter &&
                            ((StudyTask) getChildAt(i)).getTaskString().equals(taskString)){

                return true;
            }

        }
        return false;
    }

    public boolean contains(int chapter, int startPage, int endPage){
        for(int i = 0; i < this.getChildCount(); i++){
            if(
                    !(getChildAt(i).getClass().equals(TextView.class)) &&
                            ((StudyTask) getChildAt(i)).getChapter() == chapter &&
                            ((StudyTask) getChildAt(i)).getStartPage() == startPage &&
                            ((StudyTask) getChildAt(i)).getEndPage() == endPage){

                return true;
            }

        }
        return false;
    }

    public void addMap(HashMap<Integer, ArrayList<StudyTask>> hashMap){
        Map<Integer, ArrayList> treeMap;
        treeMap = new TreeMap<Integer, ArrayList>(hashMap);
        for (Object value : treeMap.values()) {
            ArrayList<StudyTask> a = (ArrayList) value;
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
    }

    public void addTasksFromDatabase(AssignmentsDBAdapter assDBAdapter, String courseCode, AssignmentType assignmentType) {

        Cursor cursor = assDBAdapter.getAssignments();

        hashMapOfStudyTasks = new HashMap<>();
        hashMapOfReadingAssignments = new HashMap<>();

        int i = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {

                if(cursor.getString(cursor.getColumnIndex("_ccode")).equals(courseCode) &&
                        cursor.getString(cursor.getColumnIndex("type")).equals(assignmentType.toString())) {

                    i++;
                    Log.d("dbref", "i: "+i);

                    AssignmentStatus assignmentStatus;
                    if (cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())) {
                        assignmentStatus = AssignmentStatus.DONE;
                    } else {
                        assignmentStatus = AssignmentStatus.UNDONE;
                    }
                    if (cursor.getString(cursor.getColumnIndex("type")).equals(AssignmentType.READ.toString())) {
                        assignmentType = AssignmentType.READ;
                    } else {
                        assignmentType = AssignmentType.PROBLEM;
                    }

                    StudyTask studyTask = new StudyTask(
                            this.getContext(),
                            cursor.getInt(cursor.getColumnIndex("_id")),
                            cursor.getString(cursor.getColumnIndex("_ccode")),
                            cursor.getInt(cursor.getColumnIndex("chapter")),
                            cursor.getInt(cursor.getColumnIndex("week")),
                            cursor.getString(cursor.getColumnIndex("assNr")),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex("startPage"))),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex("stopPage"))),
                            assDBAdapter,
                            assignmentType,
                            assignmentStatus);

                    if (studyTask.getType() == AssignmentType.PROBLEM) {

                        if (hashMapOfStudyTasks.containsKey(studyTask.getChapter())) {
                            hashMapOfStudyTasks.get(studyTask.getChapter()).add(studyTask);
                        } else {
                            ArrayList<StudyTask> a = new ArrayList();
                            a.add(studyTask);
                            hashMapOfStudyTasks.put(studyTask.getChapter(), a);
                        }

                    } else {
                        if (hashMapOfReadingAssignments.containsKey(studyTask.getChapter())) {
                            hashMapOfReadingAssignments.get(studyTask.getChapter()).add(studyTask);
                        } else {
                            ArrayList<StudyTask> a = new ArrayList();
                            a.add(studyTask);
                            hashMapOfReadingAssignments.put(studyTask.getChapter(), a);
                        }
                    }
                }
            }

            if(hashMapOfStudyTasks!=null)
                addMap(hashMapOfStudyTasks);
            if(hashMapOfReadingAssignments!=null)
                addMap(hashMapOfReadingAssignments);

        }
        Log.d("dbref", "asses i: "+i + " course " + courseCode);
    }

    public void addTasksFromDatabase(AssignmentsDBAdapter assDBAdapter, String courseCode, AssignmentType assignmentType, int week) {

        Cursor cursor = assDBAdapter.getAssignments();
        //Log.d("dbref", "asses total: "+cursor.getCount());

        hashMapOfStudyTasks = new HashMap<>();
        hashMapOfReadingAssignments = new HashMap<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("_ccode")).equals(courseCode)
                        && cursor.getString(cursor.getColumnIndex("type")).equals(assignmentType.toString())
                        && cursor.getInt(cursor.getColumnIndex("week")) == week
                        && cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.UNDONE.toString())) {


                    AssignmentStatus assignmentStatus;
                    if (cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())) {
                        assignmentStatus = AssignmentStatus.DONE;
                    } else {
                        assignmentStatus = AssignmentStatus.UNDONE;
                    }
                    if (cursor.getString(cursor.getColumnIndex("type")).equals(AssignmentType.READ.toString())) {
                        assignmentType = AssignmentType.READ;
                    } else {
                        assignmentType = AssignmentType.PROBLEM;
                    }

                    StudyTask studyTask = new StudyTask(
                            this.getContext(),
                            cursor.getInt(cursor.getColumnIndex("_id")),
                            cursor.getString(cursor.getColumnIndex("_ccode")),
                            cursor.getInt(cursor.getColumnIndex("chapter")),
                            cursor.getInt(cursor.getColumnIndex("week")),
                            cursor.getString(cursor.getColumnIndex("assNr")),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex("startPage"))),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex("stopPage"))),
                            assDBAdapter,
                            assignmentType,
                            assignmentStatus);

                    if (studyTask.getType() == AssignmentType.PROBLEM) {

                        if (hashMapOfStudyTasks.containsKey(studyTask.getChapter())) {
                            hashMapOfStudyTasks.get(studyTask.getChapter()).add(studyTask);
                        } else {
                            ArrayList<StudyTask> a = new ArrayList();
                            a.add(studyTask);
                            hashMapOfStudyTasks.put(studyTask.getChapter(), a);
                        }

                    } else {
                        if (hashMapOfReadingAssignments.containsKey(studyTask.getChapter())) {
                            hashMapOfReadingAssignments.get(studyTask.getChapter()).add(studyTask);
                        } else {
                            ArrayList<StudyTask> a = new ArrayList();
                            a.add(studyTask);
                            hashMapOfReadingAssignments.put(studyTask.getChapter(), a);
                        }
                    }
                }
            }

            if(hashMapOfStudyTasks!=null)
                addMap(hashMapOfStudyTasks);
            if(hashMapOfReadingAssignments!=null)
                addMap(hashMapOfReadingAssignments);

        }

    }
    public boolean isEmpty(){
        return (this.getChildCount()<1);
    }

    public void addTasksFromWeb( String courseCode, int chapter, int week, String assNr,
                                int startPage, int stopPage, String status, String type,AssignmentsDBAdapter assDBAdapter) {



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

            if (hashMapOfStudyTasks2.containsKey(studyTask.getChapter())) {
                hashMapOfStudyTasks2.get(studyTask.getChapter()).add(studyTask);
            } else {
                ArrayList<CheckedStudyTaskToDB> a = new ArrayList();
                a.add(studyTask);
                hashMapOfStudyTasks2.put(studyTask.getChapter(), a);
            }

        } else {
            if (hashMapOfReadingAssignments2.containsKey(studyTask.getChapter())) {
                hashMapOfReadingAssignments2.get(studyTask.getChapter()).add(studyTask);
            } else {
                ArrayList<CheckedStudyTaskToDB> a = new ArrayList();
                a.add(studyTask);
                hashMapOfReadingAssignments2.put(studyTask.getChapter(), a);
            }
        }
    }

    public HashMap<Integer, ArrayList<CheckedStudyTaskToDB>> getReadHashMapOfStudyTasks2() {
        return this.hashMapOfReadingAssignments2;
    }


    public HashMap<Integer, ArrayList<CheckedStudyTaskToDB>> getOtherHashMapOfStudyTask2(){
        return this.hashMapOfStudyTasks2;
    }

    public void addReadAssignmets(){
        Map<Integer, ArrayList> treeMap;
        treeMap = new TreeMap<Integer, ArrayList>(hashMapOfReadingAssignments2);
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
        hashMapOfReadingAssignments2.clear();
    }

    public void addOtherAssignmets(){
        Map<Integer, ArrayList> treeMap;
        treeMap = new TreeMap<Integer, ArrayList>(hashMapOfStudyTasks2);
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
        hashMapOfStudyTasks2.clear();
    }



}