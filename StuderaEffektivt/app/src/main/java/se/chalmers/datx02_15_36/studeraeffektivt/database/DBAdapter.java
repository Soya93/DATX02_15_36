package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;

/**
 * A database adapter that is used to read and write to/from the database.
 * The database currently has one table for Courses with coursecode and coursename.
 * Created by Patricia on 2015-03-11.
 */
public class DBAdapter  {

    private DBHelper dbHelper;

    public DBAdapter(Context context){
        dbHelper = new DBHelper(context);
        Log.i("DB", "DBAdapter created.");
    }

    /**
     * Insert a Course to the database.
     * @param courseCode
     * @param courseName
     * @return
     */
    public long insertCourse(String courseCode, String courseName){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(dbHelper.COURSES__ccode, courseCode);
        cv.put(dbHelper.COURSES_cname, courseName);
        return db.insert(dbHelper.TABLE_COURSES, null, cv);
    }

    /**
     * Insert a Session of studytime into the database.
     * @param courseCode
     * @param minutes
     * @return
     */
    public long insertSession(String courseCode, int minutes){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(dbHelper.COURSES__ccode, courseCode);
        cv.put(dbHelper.SESSIONS_minutes, minutes);
        cv.put(dbHelper.SESSIONS_startTimestamp, "CURRENT_TIMESTAMP");
        return db.insert(dbHelper.TABLE_SESSIONS, null, cv);
    }

    /**
     * Get all Sessions.
     * @return
     */
    public Cursor getSessions(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(dbHelper.TABLE_SESSIONS, null, null, null, null, null, null);
    }

    /**
     * Get all courses.
     */
    public Cursor getCourses(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(dbHelper.TABLE_COURSES, null, null, null, null, null, null);
    }

    /*The DBHelper class*/
    public static class DBHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME = "pluggapp.db";
        private static final int DATABASE_VERSION = 1; //Has to be incremented to update.

        //Variables for the Courses table.
        private static final String TABLE_COURSES = "COURSES";
        private static final String COURSES__ccode = "_ccode";
        private static final String COURSES_cname = "cname";

        //Variables for the Sessions table.
        private static final String TABLE_SESSIONS = "SESSIONS";
        private static final String SESSIONS_startTimestamp = "startTimestamp";
        private static final String SESSIONS_minutes = "minutes";

        //Variables for the Assignments table.
        private static final String TABLE_ASSIGNMENTS = "ASSIGNMENTS";

        /*Constructor.*/
        public DBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Called when database is created.
            //Creation of schemas and initial insert of data.
            db.execSQL("CREATE TABLE "+TABLE_COURSES+" ("+COURSES__ccode+" VARCHAR(50) PRIMARY KEY, "+COURSES_cname+" VARCHAR(50))");

            db.execSQL("CREATE TABLE "+TABLE_SESSIONS+" ("+SESSIONS_startTimestamp+" DATETIME DEFAULT CURRENT_TIMESTAMP, "+
                    SESSIONS_minutes+" INT, "+COURSES__ccode+" VARCHAR(50), FOREIGN KEY("+COURSES__ccode+") REFERENCES "+
                    TABLE_COURSES+"("+COURSES__ccode+"), PRIMARY KEY("+COURSES__ccode+", "+SESSIONS_startTimestamp+"))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS COURSES");
            onCreate(db);
        }
    }
}
