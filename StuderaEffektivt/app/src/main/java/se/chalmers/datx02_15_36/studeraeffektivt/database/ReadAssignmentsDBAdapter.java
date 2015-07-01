package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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

    public long insertAssignment(String courseCode, int id, String chapter, int week, int startPage, int endPage, AssignmentStatus status) {
        Log.i("DB", "in insert");

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
        String selection = READ_ccode + " = '" + ccode + "'";
        return db.query(TABLE_READ, null, selection, null, null, null, null);
    }

    public  String getCourse(int id){
        String selection = READ__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(READ_ccode));
    }
    public int getWeek(int id){
        String selection = READ__id + " = '" + id + "'";
        Log.i("ReadAssDB", "selection :" + selection);
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        int week  = -1;
        if( cur != null && cur.moveToNext() ) {
            week = cur.getInt(cur.getColumnIndex(READ_week));
            Log.i("ReadAssDB", "selection :" + selection);
            Log.i("ReadAssDB", "week :"  + week);
            cur.close();
        }
        return week;
    }

    public  String getChapter(int id){

        Cursor cursor = db.query(TABLE_READ, new String[] { READ_chapter }, READ__id + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
            cursor.moveToFirst();
            while(cursor.moveToNext()){

            }

            return cursor.getString(0);

       /* String selection = READ__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        cur.moveToFirst();
        return  cur.getString(cur.getColumnIndex(READ_chapter));*/
    }

    public  String getStartPage(int id){
        String selection = READ__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(READ_startPage));
    }

    public  String getEndPage(int id){
        String selection = READ__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        return cur.getString(cur.getColumnIndex(READ_endPage));
    }
}
