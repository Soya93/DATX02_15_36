package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.LabAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OtherAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ProblemAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.RepetitionAssignmentsDBAdapter;

/**
 * Created by SoyaPanda on 15-07-08.
 */
public class RepetitionUtil {

    //The access point of the database.
    private HandInAssignmentsDBAdapter handInDB;
    private LabAssignmentsDBAdapter labDB;
    private OtherAssignmentsDBAdapter otherDB;
    private ProblemAssignmentsDBAdapter problemDB;
    private ReadAssignmentsDBAdapter readDB;
    private CoursesDBAdapter coursesDB;
    private RepetitionAssignmentsDBAdapter repeatDB;


    public RepetitionUtil(Context context) {
        handInDB = new HandInAssignmentsDBAdapter(context);
        labDB = new LabAssignmentsDBAdapter(context);
        otherDB = new OtherAssignmentsDBAdapter(context);
        problemDB = new ProblemAssignmentsDBAdapter(context);
        readDB = new ReadAssignmentsDBAdapter(context);
        coursesDB = new CoursesDBAdapter(context);
        repeatDB = new RepetitionAssignmentsDBAdapter(context);
    }

    public boolean canRepeat(String courseCode) {
        int week = setWeek();
        boolean canRepeatHandIn = canRepeatHandIn(courseCode, week);
        boolean canRepeatLab = canRepeatLab(courseCode, week);
        boolean canRepeatOther = canRepeatOther(courseCode, week);
        boolean canRepeatProblem = canRepeatProblem(courseCode, week);
        boolean canRepeatRead = canRepeatRead(courseCode, week);

        return canRepeatHandIn && canRepeatLab && canRepeatOther && canRepeatProblem
                && canRepeatRead;
    }


    private boolean canRepeatHandIn(String courseCode, int week) {
        Cursor cur = handInDB.getDoneAssignments(courseCode);
        int numberToRepeat = 0;
        while (cur.moveToNext()) {
            if (cur.getInt(cur.getColumnIndex(HandInAssignmentsDBAdapter.HANDIN_week)) == week) {
                numberToRepeat++;
                int id = cur.getInt(cur.getColumnIndex(HandInAssignmentsDBAdapter.HANDIN__id));
                repeatDB.insertAllAssignment(courseCode,id, week, handInDB.getAssNr(id), handInDB.getNr(id),AssignmentType.HANDIN, AssignmentStatus.UNDONE);
            }
        }
        return numberToRepeat > 0;
    }

    private boolean canRepeatLab(String courseCode, int week) {
        Cursor cur = labDB.getDoneAssignments(courseCode);
        int numberToRepeat = 0;
        while (cur.moveToNext()) {
            if (cur.getInt(cur.getColumnIndex(LabAssignmentsDBAdapter.LABS_week)) == week) {
                numberToRepeat++;
                int id = cur.getInt(cur.getColumnIndex(LabAssignmentsDBAdapter.LABS__id));
                repeatDB.insertAllAssignment(courseCode,id, week, labDB.getAssNr(id), labDB.getNr(id),AssignmentType.LAB, AssignmentStatus.UNDONE);
            }
        }
        return numberToRepeat > 0;
    }

    private boolean canRepeatOther(String courseCode, int week) {
        Cursor cur = otherDB.getDoneAssignments(courseCode);
        int numberToRepeat = 0;
        while (cur.moveToNext()) {
            if (cur.getInt(cur.getColumnIndex(OtherAssignmentsDBAdapter.OTHER_week)) == week) {
                numberToRepeat++;
                int id = cur.getInt(cur.getColumnIndex(OtherAssignmentsDBAdapter.OTHER__id));
                repeatDB.insertAllAssignment(courseCode,id, week, otherDB.getAssNr(id), Integer.toString(otherDB.getWeek(id)),AssignmentType.OTHER, AssignmentStatus.UNDONE);
            }
        }
        return numberToRepeat > 0;
    }

    private boolean canRepeatProblem(String courseCode, int week) {
        Cursor cur = problemDB.getDoneAssignments(courseCode);
        int numberToRepeat = 0;
        while (cur.moveToNext()) {
            if (cur.getInt(cur.getColumnIndex(ProblemAssignmentsDBAdapter.PROBLEMS_week)) == week) {
                numberToRepeat++;
                int id = cur.getInt(cur.getColumnIndex(ProblemAssignmentsDBAdapter.PROBLEMS__id));
                repeatDB.insertAllAssignment(courseCode,id, week, problemDB.getAssNr(id), problemDB.getChapter(id),AssignmentType.PROBLEM, AssignmentStatus.UNDONE);
            }
        }
        return numberToRepeat > 0;
    }

    private boolean canRepeatRead(String courseCode, int week) {
        Cursor cur = readDB.getDoneAssignments(courseCode);
        int numberToRepeat = 0;
        while (cur.moveToNext()) {
            if (cur.getInt(cur.getColumnIndex(ReadAssignmentsDBAdapter.READ_week)) == week) {
                numberToRepeat++;
                int id = cur.getInt(cur.getColumnIndex(ReadAssignmentsDBAdapter.READ__id));
                repeatDB.insertAllAssignment(courseCode,id, week, readDB.getStartPage(id) + "-" + readDB.getEndPage(id), readDB.getChapter(id),AssignmentType.READ, AssignmentStatus.UNDONE);
            }
        }
        return numberToRepeat > 0;
    }

    private int setWeek() {
        int week = CalendarUtils.getCurrWeekNumber() - 1;

        if (CalendarUtils.getCurrWeekNumber() == 1) {
            week = 51;
        } else if (CalendarUtils.getCurrWeekNumber() == 2) {
            week = 52;
        }
        return week;
    }


    public void addRandomAssignments(String courseCode) {
        Cursor cur = repeatDB.getRandomUnDoneAllAssignments(courseCode);
        int nrOfRepeatable = cur.getCount();
        int nrToAdd = 0;
        while(cur.moveToNext() && nrToAdd < nrOfRepeatable/4){
            int id = cur.getInt(cur.getColumnIndex(RepetitionAssignmentsDBAdapter.ALL_REPEAT__id));
            int week = cur.getInt(cur.getColumnIndex(RepetitionAssignmentsDBAdapter.ALL_REPEAT_week));
            String taskString = cur.getString(cur.getColumnIndex(RepetitionAssignmentsDBAdapter.ALL_REPEAT_taskString));
            String sortedString = cur.getString(cur.getColumnIndex(RepetitionAssignmentsDBAdapter.ALL_REPEAT_sortedString));
            String type = cur.getString(cur.getColumnIndex(RepetitionAssignmentsDBAdapter.ALL_REPEAT_type));
            String status = cur.getString(cur.getColumnIndex(RepetitionAssignmentsDBAdapter.ALL_REPEAT_status));
            AssignmentType assignmentType = AssignmentTypeUtil.stringToAssignmentType(type);
            AssignmentStatus assignmentStatus = status.equals("DONE")? AssignmentStatus.DONE: AssignmentStatus.UNDONE;
            repeatDB.insertAssignment(courseCode,id,week,taskString,sortedString,assignmentType,assignmentStatus);
        }
    }
}
