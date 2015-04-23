package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;

/**
 * A database adapter that is used to read and write to/from the database.
 * Three tables: Courses, Sessions, Assignments.
 * Created by Patricia on 2015-03-11.
 */
public class DBAdapter {

    private DBHelper dbHelper;

    public DBAdapter(Context context) {
        dbHelper = new DBHelper(context);
        Log.i("DB", "DBAdapter created.");
    }

    public long insertCourse(String courseCode, String courseName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put(dbHelper.COURSES__ccode, courseCode);
            cv.put(dbHelper.COURSES_cname, courseName);

            return db.insert(dbHelper.TABLE_COURSES, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long insertSession(String courseCode, int week, int minutes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put(dbHelper.COURSES__ccode, courseCode);
            cv.put(dbHelper.SESSION_week, week);
            cv.put(dbHelper.SESSIONS_minutes, minutes);

            return db.insert(dbHelper.TABLE_SESSIONS, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long insertAssignment(String courseCode, int chapter, int week, String assNr,
                                 int startPage, int stopPage, AssignmentType type, AssignmentStatus status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put(dbHelper.ASSIGNMENTS_ccode, courseCode);
            cv.put(dbHelper.ASSIGNMENTS_chapter, chapter);
            cv.put(dbHelper.ASSIGNMENTS_week, week);
            cv.put(dbHelper.ASSIGNMENTS_assNr, assNr);
            cv.put(dbHelper.ASSIGNMENTS_startPage, startPage);
            cv.put(dbHelper.ASSIGNMENTS_stopPage, stopPage);
            cv.put(dbHelper.ASSIGNMENTS_type, type.toString());
            cv.put(dbHelper.ASSIGNMENTS_status, status.toString());

            return db.insert(dbHelper.TABLE_ASSIGNMENTS, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long insertAssignment(String courseCode,int id, int chapter, int week, String assNr,
                                 int startPage, int stopPage, AssignmentType type, AssignmentStatus status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put(dbHelper.ASSIGNMENTS_ccode, courseCode);
            cv.put(dbHelper.ASSIGNMENTS_chapter, chapter);
            cv.put(dbHelper.ASSIGNMENTS_week, week);
            cv.put(dbHelper.ASSIGNMENTS_assNr, assNr);
            cv.put(dbHelper.ASSIGNMENTS_startPage, startPage);
            cv.put(dbHelper.ASSIGNMENTS_stopPage, stopPage);
            cv.put(dbHelper.ASSIGNMENTS_type, type.toString());
            cv.put(dbHelper.ASSIGNMENTS_status, status.toString());
            cv.put(dbHelper.ASSIGNMENTS__id, id);

            return db.insert(dbHelper.TABLE_ASSIGNMENTS, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long deleteAssignment(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try{
            return db.delete(dbHelper.TABLE_ASSIGNMENTS, dbHelper.ASSIGNMENTS__id+" = "+id, null);
        }catch (Exception e){
            return -1;
        }
    }

    public long setDone(int assignmentId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.ASSIGNMENTS_status, AssignmentStatus.DONE.toString());

        try {
            return db.update(dbHelper.TABLE_ASSIGNMENTS, cv, dbHelper.ASSIGNMENTS__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public long setUndone(int assignmentId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String nullString = null;
        cv.put(dbHelper.ASSIGNMENTS_status, nullString);

        try {
            return db.update(dbHelper.TABLE_ASSIGNMENTS, cv, dbHelper.ASSIGNMENTS__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public long insertTimeOnCourse(String ccode, int minutes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put(dbHelper.TIMEONCOURSE__ccode, ccode);
            cv.put(dbHelper.TIMEONCOURSE_time, minutes);

            return db.insert(dbHelper.TABLE_TIMEONCOURSE, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public Cursor getAssignments() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(dbHelper.TABLE_ASSIGNMENTS, null, null, null, null, null, null);
    }

    public Cursor getDoneAssignments(String ccode){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = dbHelper.ASSIGNMENTS_ccode + " = '" + ccode + "' AND "
                + dbHelper.ASSIGNMENTS_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        return db.query(dbHelper.TABLE_ASSIGNMENTS, null, selection, null, null, null, null);
    }

    public Cursor getAssignments(String ccode){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = dbHelper.ASSIGNMENTS_ccode + " = '" + ccode + "'";
        return db.query(dbHelper.TABLE_ASSIGNMENTS, null, selection, null, null, null, null);
    }

    public Cursor getSessions() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(dbHelper.TABLE_SESSIONS, null, null, null, null, null, null);
    }

    public Cursor getSessions(int week){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(dbHelper.TABLE_SESSIONS, null, dbHelper.SESSION_week + " = '" + week + "'", null, null, null, null);
    }

    public int getSpentTime(String ccode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] columns = {"SUM(" + dbHelper.SESSIONS_minutes + ")"};
        String selection = dbHelper.SESSIONS_ccode + " = '" + ccode + "'";
        Cursor cursor = db.query(dbHelper.TABLE_SESSIONS, columns, selection, null, dbHelper.SESSIONS_ccode, null, null);

        cursor.moveToNext();
        if (cursor.getCount() > 0) {
            int i = cursor.getInt(0);
            cursor.close();
            return i;
        } else {
            return -1;
        }
    }

    public int getTimeOnCourse(String ccode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] columns = {dbHelper.TIMEONCOURSE_time};
        String selection = dbHelper.TIMEONCOURSE__ccode + " = '" + ccode + "'";
        Cursor cursor = db.query(dbHelper.TABLE_TIMEONCOURSE, columns, selection, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int i = cursor.getInt(0);
            cursor.close();
            return i;
        } else {
            cursor.close();
            return -1;
        }
    }

    /**
     * Get all courses.
     */
    public Cursor getCourses() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(dbHelper.TABLE_COURSES, null, null, null, null, null, null);
    }

    /*The DBHelper class*/
    public static class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "pluggapp.db";
        private static final int DATABASE_VERSION = 1; //Has to be incremented to update.

        //Variables for the Courses table.
        private static final String TABLE_COURSES = "COURSES";
        private static final String COURSES__ccode = "_ccode";
        private static final String COURSES_cname = "cname";

        //Variables for the Sessions table.
        private static final String TABLE_SESSIONS = "SESSIONS";
        private static final String SESSIONS_ccode = "_ccode";
        private static final String SESSIONS__id = "_id";
        private static final String SESSION_timestamp = "timestamp";
        private static final String SESSION_week = "week";
        private static final String SESSIONS_minutes = "minutes";

        //Variables for the Assignments table.
        private static final String TABLE_ASSIGNMENTS = "ASSIGNMENTS";
        private static final String ASSIGNMENTS__id = "_id";
        private static final String ASSIGNMENTS_ccode = COURSES__ccode;
        private static final String ASSIGNMENTS_chapter = "chapter";
        private static final String ASSIGNMENTS_week = "week";
        private static final String ASSIGNMENTS_assNr = "assNr";
        private static final String ASSIGNMENTS_startPage = "startPage";
        private static final String ASSIGNMENTS_stopPage = "stopPage";
        private static final String ASSIGNMENTS_type = "type";
        private static final String ASSIGNMENTS_status = "status";

        //Variables for the TimeOnCourse table.
        private static final String TABLE_TIMEONCOURSE = "TIMEONCOURSE";
        private static final String TIMEONCOURSE__ccode = COURSES__ccode;
        private static final String TIMEONCOURSE_time = "time";

        /*Constructor.*/
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Called when database is created.
            //Creation of schemas and initial insert of data.
            db.execSQL("CREATE TABLE " + TABLE_COURSES + " ("
                    + COURSES__ccode + " VARCHAR(50) PRIMARY KEY, "
                    + COURSES_cname + " VARCHAR(50))");

            db.execSQL("CREATE TABLE " + TABLE_SESSIONS + " ("
                    + SESSIONS__id + " INTEGER PRIMARY KEY, "
                    + SESSIONS_minutes + " INT, "
                    + SESSIONS_ccode + " VARCHAR(50), "
                    + SESSION_timestamp + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + SESSION_week + " INT)");

            db.execSQL("CREATE TABLE " + TABLE_ASSIGNMENTS + " ("
                    + ASSIGNMENTS__id + " PRIMARY KEY, "
                    + ASSIGNMENTS_ccode + " VARCHAR(50), "
                    + ASSIGNMENTS_chapter + " INT, "
                    + ASSIGNMENTS_week + " INT, "
                    + ASSIGNMENTS_assNr +" VARCHAR(50), "
                    + ASSIGNMENTS_startPage + " INT, "
                    + ASSIGNMENTS_stopPage + " INT, "
                    + ASSIGNMENTS_type + " VARCHAR(50), "
                    + ASSIGNMENTS_status + " VARCHAR(50))");

            db.execSQL("CREATE TABLE " + TABLE_TIMEONCOURSE + "("
                    + TIMEONCOURSE__ccode + " VARCHAR(50) PRIMARY KEY, "
                    + TIMEONCOURSE_time + " INT, "
                    + "FOREIGN KEY(" + TIMEONCOURSE__ccode + ") REFERENCES " + COURSES__ccode + ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS COURSES");
            onCreate(db);
        }
    }
}
