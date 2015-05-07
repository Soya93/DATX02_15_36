package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;

/**
 * Created by SoyaPanda on 15-05-06.
 */
public class RepetitionReminder {


    private DBAdapter dbAdapter;
    private List <String> coursesToRepeat;

    public RepetitionReminder() {
    }

    public String reminderMessage(){
        if(hasCourses()) {
            if (haveAnyToRepeat()) {
                return coursesToRepeat.size() == 1 ? "Du har följande kurs att repetera" : "Du har följande kurser att repetera";
            } else {
                return "Du har inga uppgifter att repetera och borde kanske därför göra några";
            }
        } else {
            return "Du har inga kurser. Vill du lägga till en kurs?";
        }
    }

    public List<String> getCoursesToRepeat(){
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
                if(dbAdapter.getDoneAssignments(ccode).getCount() >= 1){
                    coursesToRepeat.add(ccode);
                }

            }
        }
        return coursesToRepeat.size() > 0;
    }

    public List <Integer> getRandomAssingments(String courseCode) {
        if(canRepeatCourse(courseCode)) {
            Cursor doneAssignments = dbAdapter.getDoneAssignments(courseCode);
            List<Integer> finishedAssignments = new ArrayList<>();

            while (doneAssignments.moveToNext()) {
                if (doneAssignments.getInt(doneAssignments.getColumnIndex("week")) == Utils.getCurrWeekNumber() - 2) {
                    finishedAssignments.add(doneAssignments.getColumnIndex("_id"));
                }
            }
            return randomAssignments(finishedAssignments);

        }
        //TODO: add exception or somehting?
        return null;
    }

    private boolean canRepeatCourse(String courseCode) {
        if(haveAnyToRepeat()) {
            return coursesToRepeat.contains(courseCode);
        }
        return false;
    }

    public void setDBAdapter(DBAdapter dbAdapter) {
        this.dbAdapter = dbAdapter;
    }

    private boolean hasCourses() {
        Cursor courses = dbAdapter.getCourses();
        if (courses.getCount() == 0) {
            return false;
        }
        return true;
    }
    private Cursor getCourses() {
        return dbAdapter.getCourses();
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
        //If has courses and did tasks before in it
        //Randomize tasks from that course - DONE

        //Else if has courses but did not have any tasks to repeat
        //Tell the user that he/she does not have

        //Else has no courses
        //Tell the user that there are no courses to repeat, and ask if they want to repeat.
}

