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

package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.database.AssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;

/**
 * Created by SoyaPanda on 15-05-06.
 */
public class RepetitionReminder {


    private AssignmentsDBAdapter assDBAdapter;
    private CoursesDBAdapter coursesDBAdapter;
    private ArrayList <String> coursesToRepeat;

    public RepetitionReminder() {
    }

    public ArrayList<String> reminderMessage(){
        ArrayList <String> messages = new ArrayList<>();
        if(hasCourses()) {
            if (haveAnyToRepeat()) {
                messages.add(coursesToRepeat.size() == 1 ? "Du har följande kurs att repetera" : "Du har följande kurser att repetera");
            } else {
                messages.add("Du har inga uppgifter att repetera");
                messages.add("och borde kanske därför göra några.");
                messages.add("Vill du lägga till några uppgifter?");
            }
        } else {
            messages.add("Du har inga kurser");
            messages.add("Vill du lägga till en kurs?");
        }
        return messages;
    }

    public ArrayList<String> getCoursesToRepeat(){
        return coursesToRepeat;
    }


    /*Checks if the student have any courses to repeat
    If so, the course is added to a list with courses to repeat.*/
    public boolean haveAnyToRepeat(){
        coursesToRepeat = new ArrayList<>();

        if (hasCourses()) {
            Cursor courses = getCourses();

            while (courses.moveToNext()) {
                String ccode = courses.getString(courses.getColumnIndex("_ccode"));
                if(assDBAdapter.getDoneAssignments(ccode).getCount() >= 1){
                    coursesToRepeat.add(ccode);
                }

            }
        }
        return coursesToRepeat.size() > 0;
    }

    /*public List <Integer> getRandomAssingments(String courseCode) {
        Cursor doneAssignments = assDBAdapter.getAssignments(courseCode);//dbAdapter.getDoneAssignments(courseCode);
        List<Integer> finishedAssignments = new ArrayList<>();

        while (doneAssignments.moveToNext()) {
            finishedAssignments.add(doneAssignments.getColumnIndex("_id"));
        }
        return randomAssignments(finishedAssignments);
    }*/

    public List <Integer> getRandomAssingments(String courseCode, int week) {
            Cursor doneAssignments = assDBAdapter.getDoneAssignments(courseCode);
            List<Integer> finishedAssignments = new ArrayList<>();

            while (doneAssignments.moveToNext()) {
                //if (doneAssignments.getInt(doneAssignments.getColumnIndex("week")) == week) {
                finishedAssignments.add(doneAssignments.getColumnIndex("_id"));
                //}
            }
            return finishedAssignments;
            //return randomAssignments(finishedAssignments);
    }

    /*public List <Integer> getRandomWeekAssingments(String courseCode) {
        if(canRepeatCourse(courseCode)) {

            Cursor doneAssignments = assDBAdapter.getAssignments(courseCode);//dbAdapter.getDoneAssignments(courseCode);
            List<Integer> finishedAssignments = new ArrayList<>();

            while (doneAssignments.moveToNext()) {
                //if (doneAssignments.getInt(doneAssignments.getColumnIndex("week")) == Utils.getCurrWeekNumber() - 2) {
                finishedAssignments.add(doneAssignments.getColumnIndex("_id"));
                //}
            }
            return randomAssignments(finishedAssignments);

        }
        return null;
    }*/

    private boolean canRepeatCourse(String courseCode) {
        if(haveAnyToRepeat()) {
            return coursesToRepeat.contains(courseCode);
        }
        return false;
    }

    public void setCoursesDBAdapter(CoursesDBAdapter cdba) {
        this.coursesDBAdapter = cdba;
    }

    public void setAssesDBAdapter(AssignmentsDBAdapter adba) {
        this.assDBAdapter = adba;
    }

    public boolean hasCourses() {
        Cursor courses = coursesDBAdapter.getCourses();
        return courses.getCount() > 0;
    }
    public Cursor getCourses() {
        return coursesDBAdapter.getCourses();
    }

    /* Randomizes done assignments for repetition */
    private List<Integer> randomAssignments(List<Integer> assignments) {
        List<Integer> randomAssignments = new ArrayList<>();

        while (!assignments.isEmpty()) {
            Random random = new Random();
            int numOfAssignments = assignments.size();

            Integer randomAssignment = assignments.get(random.nextInt(assignments.size()));
            randomAssignments.add(randomAssignment);

            if(numOfAssignments>4) {
                for (int i = 1; i <= numOfAssignments / 4; i++) {
                    if (!randomAssignments.contains(randomAssignment)){
                        randomAssignments.add(randomAssignment);
                    }
                }
            }
        }
        return randomAssignments;
    }
}

