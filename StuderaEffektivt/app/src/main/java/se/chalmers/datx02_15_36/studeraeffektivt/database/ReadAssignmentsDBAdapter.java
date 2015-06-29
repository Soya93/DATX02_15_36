package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;

/**
 * Created by SoyaPanda on 15-06-26.
 */
public class ReadAssignmentsDBAdapter  extends AssignmentsDBAdapter  {

    public ReadAssignmentsDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for ReadAssignments table.
    public static final String TABLE_READ = "READ";
    public static final String READ__id = "_id";
    public static final String READ_ccode = CoursesDBAdapter.COURSES__ccode;
    public static final String READ_chapter = "chapter";
    public static final String READ_week = "week";
    public static final String READ_startPage = "startPage";
    public static final String READ_endPage = "endPage";
    public static final String READ_status = "status";

    public long insertAssignments(String courseCode,int id, String chapter, int week, int startPage, int endPage, AssignmentStatus status) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(READ_ccode, courseCode);
            cv.put(READ_chapter, chapter);
            cv.put(READ_week, week);
            cv.put(READ_startPage, startPage);
            cv.put(READ_endPage, endPage);
            cv.put(READ_status, status.toString());
            cv.put(READ__id, id);

            return db.insert(TABLE_READ, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }


    public long deleteAssignment(int id){
        try{
            return db.delete(TABLE_READ, READ__id+ "=" +id, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getAssignments() {
        return db.query(TABLE_READ, null, null, null, null, null, null);
    }

    public long setDone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(READ_status, AssignmentStatus.DONE.toString());

        try {
            return db.update(TABLE_READ, cv, READ__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }


    public long setUndone(int assignmentId){
        ContentValues cv = new ContentValues();

        String nullString = null;
        cv.put(READ_status, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(TABLE_READ, cv, READ__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getDoneAssignments(String ccode){

        String selection = READ_ccode + " = '" + ccode + "' AND "
                + READ_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        return db.query(TABLE_READ, null, selection, null, null, null, null);
    }

    public Cursor getAssignments(String ccode){
        String selection = "_ccode" + " = '" + ccode + "'";
        return db.query(TABLE_READ, null, selection, null, null, null, null);
    }
}
