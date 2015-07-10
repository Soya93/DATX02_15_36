package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;

/**
 * Created by SoyaPanda on 15-06-26.
 */


public class LabAssignmentsDBAdapter  extends AssignmentsDBAdapter {

    public LabAssignmentsDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for LabAssignments table.
    public static final String TABLE_LABS = "LABS";
    public static final String LABS__id = "_id";
    public static final String LABS_ccode = CoursesDBAdapter.COURSES__ccode;
    public static final String LABS_nr = "nr";
    public static final String LABS_week = "week";
    public static final String LABS_assNr = "assNr";
    public static final String LABS_date = "date";
    public static final String LABS_status = "status";

    public long insertAssignment(String courseCode,int id, String nr, int week, String assNr, String date, AssignmentStatus status) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(LABS_ccode, courseCode);
            cv.put(LABS_nr, nr);
            cv.put(LABS_week, week);
            cv.put(LABS_assNr, assNr);
            cv.put(LABS_date, date);
            cv.put(LABS_status, status.toString());
            cv.put(LABS__id, id);

            return db.insert(TABLE_LABS, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long deleteAssignment(int id){
        try{
            return db.delete(TABLE_LABS, LABS__id + "=" + id, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getAssignments() {
        return db.query(TABLE_LABS, null, null, null, null, null, null);
    }

    public long setDone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(LABS_status, AssignmentStatus.DONE.toString());

        try {
            return db.update(TABLE_LABS, cv, LABS__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }


    public long setUndone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(LABS_status, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(TABLE_LABS, cv, LABS__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getDoneAssignments(String ccode){

        String selection = LABS_ccode + " = '" + ccode + "' AND "
                + LABS_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        return db.query(TABLE_LABS, null, selection, null, null, null, null);
    }

    public Cursor getAssignments(String ccode){
        String selection = LABS_ccode + " = '" + ccode + "'";
        return db.query(TABLE_LABS, null, selection, null, null, null, null);
    }

    public long deleteAssignments(String code){
        Cursor cur = getAssignments();
        Long totAsses= new Long(cur.getCount());
        long nbrRemoved = 0;
        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(LABS__id));
            nbrRemoved = deleteAssignment(id) > 0? nbrRemoved + 1: 0;
        }
        return totAsses - nbrRemoved;
    }

    public  String getCourse(int id){
        String selection = LABS__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_LABS, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(LABS_ccode));
    }

    public  int getWeek(int id){
        String selection = LABS__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_LABS, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getInt(cur.getColumnIndex(LABS_week));
    }

    public  String getNr(int id){
        String selection = LABS__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_LABS, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(LABS_nr));
    }

    public  String getAssNr(int id){
        String selection = LABS__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_LABS, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(LABS_assNr));
    }
}
