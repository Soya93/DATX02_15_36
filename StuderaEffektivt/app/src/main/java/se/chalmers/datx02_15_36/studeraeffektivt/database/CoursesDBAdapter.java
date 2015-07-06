package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;

/**
 * Created by haxmaj0 on 2015-06-22.
 */
public class CoursesDBAdapter extends DBAdapter {

    public CoursesDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for the Courses table.
    public static final String TABLE_COURSES = "COURSES";
    public static final String COURSES__ccode = "_ccode";
    public static final String COURSES_cname = "cname";
    public static final String COURSES_cstatus = "cstatus";

    //Variables for Obligatories table.
    public static final String TABLE_OBLIG = "OBLIGATORIES";
    public static final String OBLIG__id = "_id";
    public static final String OBLIG_ccode = COURSES__ccode;
    public static final String OBLIG_type = "type";
    public static final String OBLIG_date = "date";
    public static final String OBLIG_status = "status";


    //Variables for the TimeOnCourse table.
    public static final String TABLE_TIMEONCOURSE = "TIMEONCOURSE";
    public static final String TIMEONCOURSE__ccode = COURSES__ccode;
    public static final String TIMEONCOURSE_time = "time";

    public long insertCourse(String courseCode, String courseName) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(COURSES__ccode, courseCode);
            cv.put(COURSES_cname, courseName);
            cv.put(COURSES_cstatus, AssignmentStatus.UNDONE.toString()); //set course as ongoing

            return db.insert(TABLE_COURSES, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long deleteCourse(String ccode){
        try{
            return db.delete(TABLE_COURSES, COURSES__ccode + " = '" + ccode + "'", null);
        }catch (Exception e){
            return -1;
        }
    }

    public boolean exists(String courseCode){
        Cursor courses = getCourses();
        while( courses.moveToNext() ){
            if( courses.getString( courses.getColumnIndex(COURSES__ccode) ).equals(courseCode) ){
                return true;
            }
        }
        return false;
    }

    public long setCourseDone(String ccode) {
        ContentValues cv = new ContentValues();

        cv.put(COURSES_cstatus, AssignmentStatus.DONE.toString());

        try {
            return db.update(TABLE_COURSES, cv, COURSES__ccode + " = '" + ccode + "'", null);
        }catch (Exception e){
            return -1;
        }
    }

    public long setCourseUndone(String ccode) {
        ContentValues cv = new ContentValues();

        cv.put(COURSES_cstatus, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(TABLE_COURSES, cv, COURSES__ccode + " = '" + ccode + "'", null);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getCourseStatus(String ccode) {
        String selection = COURSES__ccode + " = '" + ccode + "'";
        Cursor cursor = db.query(TABLE_COURSES, null, selection, null, null, null, null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndex(COURSES_cstatus));
    }

    public long insertTimeOnCourse(String ccode, int minutes) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(TIMEONCOURSE__ccode, ccode);
            cv.put(TIMEONCOURSE_time, minutes);

            return db.update(TABLE_TIMEONCOURSE, cv, null, null);
            //return db.insert(dbHelper.TABLE_TIMEONCOURSE, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getTimeOnCourse(String ccode) {
        String[] columns = {TIMEONCOURSE_time};
        String selection = TIMEONCOURSE__ccode + " = '" + ccode + "'";
        Cursor cursor = db.query(TABLE_TIMEONCOURSE, columns, selection, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int i = cursor.getInt(0);
            cursor.close();
            return i;
        } else {
            cursor.close();
            return 0;
        }
    }

    //Courses
    public Cursor getCourses() {
        return db.query(TABLE_COURSES, null, null, null, null, null, null);
    }

    public Cursor getOngoingCourses() {
        String selection = COURSES_cstatus + " = '" + AssignmentStatus.UNDONE.toString() + "'";
        return db.query(TABLE_COURSES, null, selection, null, null, null, null);
    }

    public Cursor getDoneCourses() {
        String selection = COURSES_cstatus + " = '" + AssignmentStatus.DONE.toString() + "'";
        return db.query(TABLE_COURSES, null, selection, null, null, null, null);
    }


    //Obligatories
    public long insertObligatory(String courseCode, int id, String type, String date, AssignmentStatus status) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(OBLIG_ccode, courseCode);
            cv.put(OBLIG_type, type);
            cv.put(OBLIG_date, date);
            cv.put(OBLIG_status, status.toString());
            cv.put(OBLIG__id, id);

            return db.insert(TABLE_OBLIG, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public Cursor getObligatories(String ccode){
        String selection = OBLIG_ccode + " = '" + ccode + "'";
        return db.query(TABLE_OBLIG, null, selection, null, null, null, null);
    }

    public long deleteObligatories(String ccode){
        Cursor cur = getObligatories(ccode);
        Long totAsses= new Long(cur.getCount());
        long nbrRemoved = 0;
        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(OBLIG__id));
            nbrRemoved = deleteObligatory(id) > 0? nbrRemoved + 1: 0;
        }
        return totAsses - nbrRemoved;
    }

    public long deleteObligatory(int id){
        try{
            return db.delete(TABLE_OBLIG, OBLIG__id + "=" +id, null);
        }catch (Exception e){
            return -1;
        }
    }
}
