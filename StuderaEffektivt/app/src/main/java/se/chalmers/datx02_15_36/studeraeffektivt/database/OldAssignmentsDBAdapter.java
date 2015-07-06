package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;

/**
 * Created by haxmaj0 on 2015-06-22.
 */
public class OldAssignmentsDBAdapter extends DBAdapter {

    public OldAssignmentsDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for the Assignments table.
    public static final String TABLE_ASSIGNMENTS = "ASSIGNMENTS";
    public static final String ASSIGNMENTS__id = "_id";
    public static final String ASSIGNMENTS_ccode = "_ccode";
    public static final String ASSIGNMENTS_chapter = "chapter";
    public static final String ASSIGNMENTS_week = "week";
    public static final String ASSIGNMENTS_assNr = "assNr";
    public static final String ASSIGNMENTS_startPage = "startPage";
    public static final String ASSIGNMENTS_stopPage = "stopPage";
    public static final String ASSIGNMENTS_type = "type";
    public static final String ASSIGNMENTS_status = "status";

    public long insertAssignment(String courseCode,int id, int chapter, int week, String assNr,
                                 int startPage, int stopPage, AssignmentType type, AssignmentStatus status) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(ASSIGNMENTS_ccode, courseCode);
            cv.put(ASSIGNMENTS_chapter, chapter);
            cv.put(ASSIGNMENTS_week, week);
            cv.put(ASSIGNMENTS_assNr, assNr);
            cv.put(ASSIGNMENTS_startPage, startPage);
            cv.put(ASSIGNMENTS_stopPage, stopPage);
            cv.put(ASSIGNMENTS_type, type.toString());
            cv.put(ASSIGNMENTS_status, status.toString());
            cv.put(ASSIGNMENTS__id, id);

            return db.insert(TABLE_ASSIGNMENTS, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long deleteAssignment(int id){
        try{
            return db.delete(TABLE_ASSIGNMENTS, ASSIGNMENTS__id+ "=" +id, null);
        }catch (Exception e){
            return -1;
        }
    }


    public long setDone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(ASSIGNMENTS_status, AssignmentStatus.DONE.toString());

        try {
            return db.update(TABLE_ASSIGNMENTS, cv, ASSIGNMENTS__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public long setUndone(int assignmentId){
        ContentValues cv = new ContentValues();

        String nullString = null;
        cv.put(ASSIGNMENTS_status, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(TABLE_ASSIGNMENTS, cv, ASSIGNMENTS__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getAssignments() {
        return db.query(TABLE_ASSIGNMENTS, null, null, null, null, null, null);
    }

    public Cursor getDoneAssignments(String ccode){

        String selection = ASSIGNMENTS_ccode + " = '" + ccode + "' AND "
                + ASSIGNMENTS_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        return db.query(TABLE_ASSIGNMENTS, null, selection, null, null, null, null);
    }

    public Cursor getAssignments(String ccode){

        /*Cursor cursor = getObligatories();
        int counter = 0;
        while (cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex(AssignmentsDBAdapter.ASSIGNMENTS_ccode)).equals(ccode)){
                counter++;
            }
        }
        return counter;*/

        String selection = "_ccode" + " = '" + ccode + "'";
        return db.query(TABLE_ASSIGNMENTS, null, selection, null, null, null, null);
    }
}
