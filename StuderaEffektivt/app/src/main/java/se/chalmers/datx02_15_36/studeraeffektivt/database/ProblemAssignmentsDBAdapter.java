package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;

/**
 * Created by SoyaPanda on 15-06-26.
 */

public class ProblemAssignmentsDBAdapter  extends AssignmentsDBAdapter {

    public ProblemAssignmentsDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for ProblemAssignments table.
    public static final String TABLE_PROBLEMS = "PROBLEMS";
    public static final String PROBLEMS__id = "_id";
    public static final String PROBLEMS_ccode = CoursesDBAdapter.COURSES__ccode;
    public static final String PROBLEMS_chapter = "chapter";
    public static final String PROBLEMS_week = "week";
    public static final String PROBLEMS_assNr = "assNr";
    public static final String PROBLEMS_status = "status";

    public long insertAssignment(String courseCode,int id, String chapter, int week, String assNr, AssignmentStatus status) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(PROBLEMS_ccode, courseCode);
            cv.put(PROBLEMS_chapter, chapter);
            cv.put(PROBLEMS_week, week);
            cv.put(PROBLEMS_assNr, assNr);
            cv.put(PROBLEMS_status, status.toString());
            cv.put(PROBLEMS__id, id);

            return db.insert(TABLE_PROBLEMS, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long deleteAssignment(int id){
        try{
            return db.delete(TABLE_PROBLEMS, PROBLEMS__id + "=" +id, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getAssignments() {
        return db.query(TABLE_PROBLEMS, null, null, null, null, null, null);
    }

    public long setDone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(PROBLEMS_status, AssignmentStatus.DONE.toString());

        try {
            return db.update(TABLE_PROBLEMS, cv, PROBLEMS__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public long setUndone(int assignmentId){
        ContentValues cv = new ContentValues();

        String nullString = null;
        cv.put(PROBLEMS_status, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(TABLE_PROBLEMS, cv, PROBLEMS__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getDoneAssignments(String ccode){

        String selection = PROBLEMS_ccode + " = '" + ccode + "' AND "
                + PROBLEMS_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        return db.query(TABLE_PROBLEMS, null, selection, null, null, null, null);
    }

    public Cursor getAssignments(String ccode){
        String selection = PROBLEMS_ccode + " = '" + ccode + "'";
        return db.query(TABLE_PROBLEMS, null, selection, null, null, null, null);
    }

    public  String getCourse(int id){
        String selection = PROBLEMS__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_PROBLEMS, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(PROBLEMS_ccode));
    }

    public  int getWeek(int id){
        String selection = PROBLEMS__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_PROBLEMS, null, selection, null, null, null, null);
        return cur.getInt(cur.getColumnIndex(PROBLEMS_week));
    }

    public  String getChapter(int id){
        String selection = PROBLEMS__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_PROBLEMS, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(PROBLEMS_chapter));
    }

    public  String getAssNr(int id){
        String selection = PROBLEMS__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_PROBLEMS, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(PROBLEMS_assNr));
    }
}
