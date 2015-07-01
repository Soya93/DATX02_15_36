package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;

/**
 * Created by SoyaPanda on 15-06-26.
 */
public class HandInAssignmentsDBAdapter  extends AssignmentsDBAdapter {

    public HandInAssignmentsDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for HandinAssignments table.
    public static final String TABLE_HANDIN = "HANDIN";
    public static final String HANDIN__id = "_id";
    public static final String HANDIN_ccode = CoursesDBAdapter.COURSES__ccode;
    public static final String HANDIN_nr = "nr";
    public static final String HANDIN_week = "week";
    public static final String HANDIN_assNr = "assNr";
    public static final String HANDIN_status = "status";

    public long insertAssignment(String courseCode, int id, String nr, int week, String assNr, AssignmentStatus status) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(HANDIN_ccode, courseCode);
            cv.put(HANDIN_nr, nr);
            cv.put(HANDIN_week, week);
            cv.put(HANDIN_assNr, assNr);
            cv.put(HANDIN_status, status.toString());
            cv.put(HANDIN__id, id);

            return db.insert(TABLE_HANDIN, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long deleteAssignment(int id){
        try{
            return db.delete(TABLE_HANDIN, HANDIN__id + "=" +id, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getAssignments() {
        return db.query(TABLE_HANDIN, null, null, null, null, null, null);
    }

    public long setDone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(HANDIN_status, AssignmentStatus.DONE.toString());

        try {
            return db.update(TABLE_HANDIN, cv, HANDIN__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }


    public long setUndone(int assignmentId){
        ContentValues cv = new ContentValues();

        String nullString = null;
        cv.put(HANDIN_status, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(TABLE_HANDIN, cv, HANDIN__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getDoneAssignments(String ccode){
        String selection = HANDIN_ccode + " = '" + ccode + "' AND "
                + HANDIN_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        return db.query(TABLE_HANDIN, null, selection, null, null, null, null);
    }

    public Cursor getAssignments(String ccode){
        String selection = HANDIN_ccode + " = '" + ccode + "'";
        return db.query(TABLE_HANDIN, null, selection, null, null, null, null);
    }

    public String getCourse(int id){
        String selection = HANDIN__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_HANDIN, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(HANDIN_ccode));
    }

    public int getWeek(int id){
        String selection = HANDIN__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_HANDIN, null, selection, null, null, null, null);
        return cur.getInt(cur.getColumnIndex(HANDIN_week));
    }

    public String getNr(int id){
        String selection = HANDIN__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_HANDIN, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(HANDIN_nr));
    }

    public  String getAssNr(int id){
        String selection = HANDIN__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_HANDIN, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(HANDIN_assNr));
    }
}
