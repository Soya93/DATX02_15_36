package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A database adapter that is used to read and write to/from the database.
 * Three tables: Courses, Sessions, Assignments.
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
        cv.put(dbHelper.SESSIONS__startTimestamp, "CURRENT_TIMESTAMP");

        return db.insert(dbHelper.TABLE_SESSIONS, null, cv);
    }

    /**
     * Insert an Assignment to the database.
     * @param courseCode
     * @param chapter
     * @param assNr
     * @param startPage
     * @param stopPage
     * @param type
     * @return
     */
    public long insertAssignment(String courseCode, int chapter, String assNr, int startPage, int stopPage, String type){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.ASSIGNMENTS_ccode, courseCode);
        cv.put(dbHelper.ASSIGNMENTS_chapter, chapter);
        cv.put(dbHelper.ASSIGNMENTS__assNr, assNr);
        cv.put(dbHelper.ASSIGNMENTS__startPage, startPage);
        cv.put(dbHelper.ASSIGNMENTS__stopPage, stopPage);
        cv.put(dbHelper.ASSIGNMENTS_type, type);

        return db.insert(dbHelper.TABLE_ASSIGNMENTS, null, cv);
    }

    public long insertTimeOnCourse(String ccode, int minutes){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.TIMEONCOURSE__ccode, ccode);
        cv.put(dbHelper.TIMEONCOURSE__time, minutes);

        return db.insert(dbHelper.TABLE_TIMEONCOURSE, null, cv);
    }

    /**
     * Get all Assignments.
     * @return
     */
    public Cursor getAssignments(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(dbHelper.TABLE_ASSIGNMENTS, null, null, null, null, null, null);
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
        private static final String SESSIONS__ccode = COURSES__ccode;
        private static final String SESSIONS__startTimestamp = "startTimestamp";
        private static final String SESSIONS_minutes = "minutes";

        //Variables for the Assignments table.
        private static final String TABLE_ASSIGNMENTS = "ASSIGNMENTS";
        private static final String ASSIGNMENTS__id = "id";
        private static final String ASSIGNMENTS_ccode = COURSES__ccode;
        private static final String ASSIGNMENTS_chapter = "chapter";
        private static final String ASSIGNMENTS__assNr = "assNr";
        private static final String ASSIGNMENTS__startPage = "startPage";
        private static final String ASSIGNMENTS__stopPage = "stopPage";
        private static final String ASSIGNMENTS_type = "type";

        //Variables for the TimeOnCourse table.
        private static final String TABLE_TIMEONCOURSE = "TIMEONCOURSE";
        private static final String TIMEONCOURSE__ccode = COURSES__ccode;
        private static final String TIMEONCOURSE__time = "time";

        /*Constructor.*/
        public DBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Called when database is created.
            //Creation of schemas and initial insert of data.
            db.execSQL("CREATE TABLE "+TABLE_COURSES+" ("+COURSES__ccode+" VARCHAR(50) PRIMARY KEY, "+COURSES_cname+" VARCHAR(50))");

            db.execSQL("CREATE TABLE "+TABLE_SESSIONS+" ("+SESSIONS__startTimestamp+" DATETIME DEFAULT CURRENT_TIMESTAMP, "+
                    SESSIONS_minutes+" INT, "+COURSES__ccode+" VARCHAR(50), FOREIGN KEY("+SESSIONS__ccode+") REFERENCES "+
                    TABLE_COURSES+"("+COURSES__ccode+"), PRIMARY KEY("+SESSIONS__ccode+", "+SESSIONS__startTimestamp+"))");

            db.execSQL("CREATE TABLE "+TABLE_ASSIGNMENTS+" ("+ASSIGNMENTS__id+" PRIMARY KEY, "
                    +ASSIGNMENTS_ccode+" VARCHAR(50), " +ASSIGNMENTS_chapter+" INT, "+ASSIGNMENTS__assNr+
                    " VARCHAR(50), "+ASSIGNMENTS__startPage+" INT, "+ASSIGNMENTS__stopPage+" INT, "
                    +ASSIGNMENTS_type +" VARCHAR(50))");

            db.execSQL("CREATE TABLE "+TABLE_TIMEONCOURSE+"("+TIMEONCOURSE__ccode+" VARCHAR(50) PRIMARY KEY, "+
                    TIMEONCOURSE__time+" INT, FOREIGN KEY("+TIMEONCOURSE__ccode+") REFERENCES "+COURSES__ccode+")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS COURSES");
            onCreate(db);
        }
    }
}
