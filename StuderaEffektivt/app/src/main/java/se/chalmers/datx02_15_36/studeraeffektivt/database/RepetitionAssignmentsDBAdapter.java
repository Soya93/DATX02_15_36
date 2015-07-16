package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;

/**
 * Created by SoyaPanda on 15-07-08.
 */
public class RepetitionAssignmentsDBAdapter extends AssignmentsDBAdapter  {

    public RepetitionAssignmentsDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for Repetition table.
    public static final String TABLE_REPEAT = "REPEAT";
    public static final String REPEAT__id = "_id";
    public static final String REPEAT_ccode = CoursesDBAdapter.COURSES__ccode;
    public static final String REPEAT_week = "week";
    public static final String REPEAT_type = "type";
    public static final String REPEAT_taskString = "taskString";
    public static final String REPEAT_sortedString = "sortedString";
    public static final String REPEAT_status = "status";

    //Variables for All Repeatable table.
    public static final String TABLE_ALL_REPEAT = "ALL_REPEAT";
    public static final String ALL_REPEAT__id = "_id";
    public static final String ALL_REPEAT_ccode = CoursesDBAdapter.COURSES__ccode;
    public static final String ALL_REPEAT_week = "week";
    public static final String ALL_REPEAT_type = "type";
    public static final String ALL_REPEAT_taskString = "taskString";
    public static final String ALL_REPEAT_sortedString = "sortedString";
    public static final String ALL_REPEAT_status = "status";


    public long insertAssignment(String courseCode, int id, int week, String taskString, String sortedString, AssignmentType type, AssignmentStatus status) {
        Log.i("DB", "in insert");

        ContentValues cv = new ContentValues();

        try {
            cv.put(REPEAT__id, id);
            cv.put(REPEAT_ccode, courseCode);
            cv.put(REPEAT_week, week);
            cv.put(REPEAT_taskString, taskString);
            cv.put(REPEAT_sortedString, sortedString);
            cv.put(REPEAT_type, type.toString());
            cv.put(REPEAT_status, status.toString());

            return db.insert(TABLE_REPEAT, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }


    public long deleteAssignment(int id){
        try{
            return db.delete(TABLE_REPEAT, REPEAT__id+ "=" +id, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getAssignments() {
        return db.query(TABLE_REPEAT, null, null, null, null, null, null);
    }

    public long deleteAssignments(String code){
        Cursor cur = getAssignments();
        Long totAsses= new Long(cur.getCount());
        long nbrRemoved = 0;
        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(REPEAT__id));
            nbrRemoved = deleteAssignment(id) > 0? nbrRemoved + 1: 0;
        }
        return totAsses - nbrRemoved;
    }

    public long setDone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(REPEAT_status, AssignmentStatus.DONE.toString());

        try {
            return db.update(TABLE_REPEAT, cv, REPEAT__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }


    public long setUndone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(REPEAT_status, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(TABLE_REPEAT, cv, REPEAT__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getDoneAssignments(String ccode){
        String selection = REPEAT_ccode + " = '" + ccode + "' AND "
                + REPEAT_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        return db.query(TABLE_REPEAT, null, selection, null, null, null, null);
    }

    public Cursor getAssignments(String ccode){
        String selection = REPEAT_ccode + " = '" + ccode + "'";
        return db.query(TABLE_REPEAT, null, selection, null, null, null, null);
    }

    public  String getCourse(int id){
        String selection = REPEAT__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_REPEAT, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(REPEAT_ccode));
    }
    public int getWeek(int id){
        String selection = REPEAT__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_REPEAT, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getInt(cur.getColumnIndex(REPEAT_week));
    }

    public  String getStatus(int id){
        String selection = REPEAT__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_REPEAT, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(REPEAT_status));
    }

    public  String getType(int id){
        String selection = REPEAT__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_REPEAT, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(REPEAT_type));
    }

    public  String getSortedString(int id){
        String selection = REPEAT__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_REPEAT, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(REPEAT_sortedString));
    }

    public  String getTaskString(int id){
        String selection = REPEAT__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_REPEAT, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(REPEAT_taskString));
    }

    /*All repeatable assignments*/
    public long insertAllAssignment(String courseCode, int id, int week, String taskString, String sortedString, AssignmentType type, AssignmentStatus status) {
        Log.i("DB", "in insert");

        ContentValues cv = new ContentValues();

        try {
            cv.put(ALL_REPEAT__id, id);
            cv.put(ALL_REPEAT_ccode, courseCode);
            cv.put(ALL_REPEAT_week, week);
            cv.put(ALL_REPEAT_taskString, taskString);
            cv.put(ALL_REPEAT_sortedString, sortedString);
            cv.put(ALL_REPEAT_type, type.toString());
            cv.put(ALL_REPEAT_status, status.toString());

            return db.insert(TABLE_REPEAT, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public Cursor getRandomUnDoneAllAssignments(String ccode){
        String selection = REPEAT_ccode + " = '" + ccode + "' AND "
                + REPEAT_status + " = '" + AssignmentStatus.UNDONE.toString()+"'";
        String orderBy = "RAND()";
        return db.query(TABLE_REPEAT, null, selection, null, null, null, orderBy);
    }
}
