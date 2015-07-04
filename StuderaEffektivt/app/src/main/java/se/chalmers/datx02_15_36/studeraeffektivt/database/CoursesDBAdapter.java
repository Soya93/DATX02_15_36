package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.sql.Date;

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
    public static final String OBLIG_ccode = COURSES__ccode;
    public static final String OBLIG_type = "type";
    public static final String OBLIG_date = "date";

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

    /**
     * Get all courses.
     */
    public Cursor getCourses() {
        return db.query(TABLE_COURSES, null, null, null, null, null, null);
    }

    /**
     * Get all ongoing courses.
     */
    public Cursor getOngoingCourses() {
        String selection = COURSES_cstatus + " = '" + AssignmentStatus.UNDONE.toString() + "'";
        return db.query(TABLE_COURSES, null, selection, null, null, null, null);
    }

    public Cursor getDoneCourses() {
        String selection = COURSES_cstatus + " = '" + AssignmentStatus.DONE.toString() + "'";
        return db.query(TABLE_COURSES, null, selection, null, null, null, null);
    }

    public long insertAssignment(String courseCode, String type, String date) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(OBLIG_ccode, courseCode);
            cv.put(OBLIG_type, type);
            cv.put(OBLIG_date, date);

            return db.insert(TABLE_OBLIG, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }
}
