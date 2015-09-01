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
            cv.put(READ__id, id);
            cv.put(READ_ccode, courseCode);
            cv.put(READ_chapter, chapter);
            cv.put(READ_week, week);
            cv.put(READ_startPage, startPage);
            cv.put(READ_endPage, endPage);
            cv.put(READ_status, status.toString());

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

    public long deleteAssignments(String code){
        Cursor cur = getAssignments();
        Long totAsses= new Long(cur.getCount());
        long nbrRemoved = 0;
        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(READ__id));
            nbrRemoved = deleteAssignment(id) > 0? nbrRemoved + 1: 0;
        }
        return totAsses - nbrRemoved;
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
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(READ_ccode));
    }
    public int getWeek(int id){
        String selection = READ__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getInt(cur.getColumnIndex(READ_week));
    }

    public  String getChapter(int id){
        String selection = READ__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(READ_chapter));
    }

    public  String getStartPage(int id){
        String selection = READ__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(READ_startPage));
    }

    public  String getEndPage(int id){
        String selection = READ__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(READ_endPage));
    }

    public boolean checkIfExists(String code,String startPage, String endPage,String chapter,String week){
        String selection = READ_ccode  + " = '" + code + "' AND " +
                READ_startPage  + " = '" + startPage + "' AND " +
                READ_endPage + " = '" + endPage + "' AND " +
                READ_chapter  + " = '" + chapter+ "' AND" +
                READ_week + " = '" + week + "'";
        Cursor cur =  db.query(TABLE_READ, null, selection, null, null, null, null);
        boolean result = false;
        while(cur.moveToNext()){
            result=true;
        }
        return result;
    }

    public long deleteUndoneAssignments(String code){
        Cursor cur = getAssignments();
        Long totAsses= new Long(cur.getCount());
        long nbrRemoved = 0;
        while(cur.moveToNext()){
            if(cur.getString(cur.getColumnIndex(READ_status)).equals(AssignmentStatus.UNDONE.toString())) {
                int id = cur.getInt(cur.getColumnIndex(READ__id));
            }
            nbrRemoved ++;
        }
        return totAsses - nbrRemoved;
    }


}
