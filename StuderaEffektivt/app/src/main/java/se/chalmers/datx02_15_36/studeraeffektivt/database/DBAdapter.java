/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

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
            cv.put(dbHelper.COURSES_cstatus, AssignmentStatus.UNDONE.toString()); //set course as ongoing

            return db.insert(dbHelper.TABLE_COURSES, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long deleteCourse(String ccode){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try{
            return db.delete(dbHelper.TABLE_COURSES, dbHelper.COURSES__ccode + " = '"+ ccode + "'", null);
        }catch (Exception e){
            return -1;
        }
    }
    // Avslutad kurs
    public long setCourseDone(String ccode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.COURSES_cstatus, AssignmentStatus.DONE.toString());

        try {
            return db.update(dbHelper.TABLE_COURSES, cv, dbHelper.COURSES__ccode + " = '" + ccode + "'", null);
        }catch (Exception e){
            return -1;
        }
    }

    //Pågående kurs
    public long setCourseUndone(String ccode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.COURSES_cstatus, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(dbHelper.TABLE_COURSES, cv, dbHelper.COURSES__ccode + " = '"+ ccode + "'", null);
        }catch (Exception e){
            return -1;
        }
    }

    public String getCourseStatus(String ccode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = dbHelper.COURSES__ccode + " = '" + ccode + "'";
        Cursor cursor = db.query(dbHelper.TABLE_COURSES, null, selection, null, null, null, null);
        cursor.moveToNext();
        Log.d(selection+"", "database");
        Log.i("Database", cursor.getString(2));
        return cursor.getString(2);
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
            return db.delete(dbHelper.TABLE_ASSIGNMENTS, dbHelper.ASSIGNMENTS__id+ "=" +id, null);
        }catch (Exception e){
            return -1;
        }
    }


    public void deleteAssignments(String ccode) {
        Cursor cur = getAssignments(ccode);

        while (cur.moveToNext()){
            try {
                deleteAssignment(cur.getColumnIndex("ASSIGNMENTS__id"));
            } catch (Exception e) {
            }
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
        cv.put(dbHelper.ASSIGNMENTS_status, AssignmentStatus.UNDONE.toString());

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

            return db.update(dbHelper.TABLE_TIMEONCOURSE, cv, null, null);
            //return db.insert(dbHelper.TABLE_TIMEONCOURSE, null, cv);
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

    public Cursor getMinutes(int week, String ccode){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] columns = {"minutes"};

        return db.query(dbHelper.TABLE_SESSIONS,columns, dbHelper.SESSION_week + " = '" + week + "' AND "+
                dbHelper.SESSIONS_ccode + " = '" + ccode + "'", null, null, null, "minutes");
    }

    public int getSmallestWeek(String ccode){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] columns = {"min(week)"};
        Cursor cursor = db.query(dbHelper.TABLE_SESSIONS, columns, dbHelper.SESSIONS_ccode
                + " = '" + ccode + "'", null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() == 0 || cursor.getInt(0) == 0) {
            return 53;
        }else{
            return cursor.getInt(0);
        }
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
            return 0;
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
            return 0;
        }
    }

    /**
     * Get all courses.
     */
    public Cursor getCourses() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(dbHelper.TABLE_COURSES, null, null, null, null, null, null);
    }

    /**
     * Get all ongoing courses.
     */
    public Cursor getOngoingCourses() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = dbHelper.COURSES_cstatus + " = '" + AssignmentStatus.UNDONE.toString() + "'";
        return db.query(dbHelper.TABLE_COURSES, null, selection, null, null, null, null);
    }

    public Cursor getDoneCourses() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = dbHelper.COURSES_cstatus + " = '" + AssignmentStatus.DONE.toString() + "'";
        return db.query(dbHelper.TABLE_COURSES, null, selection, null, null, null, null);
    }

    /*The DBHelper class*/
    public static class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "pluggapp.db";
        private static final int DATABASE_VERSION = 1; //Has to be incremented to update.

        //Variables for the Courses table.
        private static final String TABLE_COURSES = "COURSES";
        private static final String COURSES__ccode = "_ccode";
        private static final String COURSES_cname = "cname";
        private static final String COURSES_cstatus = "cstatus";

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

        //Variables for LabAssignments table.
        private static final String TABLE_LABS = "LABS";
        private static final String LABS_ccode = COURSES__ccode;
        private static final String LABS_nr = "nr";
        private static final String LABS_week = "week";
        private static final String LABS_assNr = "assNr";
        private static final String LABS_status = "status";

        //Variables for ProblemAssignments table.
        private static final String TABLE_PROBLEMS = "PROBLEMS";
        private static final String PROBLEMS_ccode = COURSES__ccode;
        private static final String PROBLEMS_chapter = "chapter";
        private static final String PROBLEMS_week = "week";
        private static final String PROBLEMS_assNr = "assNr";
        private static final String PROBLEMS_status = "status";

        //Variables for ReadAssignments table.
        private static final String TABLE_READ = "READ";
        private static final String READ_ccode = COURSES__ccode;
        private static final String READ_chapter = "chapter";
        private static final String READ_week = "week";
        private static final String READ_startPage = "startPage";
        private static final String READ_stopPage = "stopPage";
        private static final String READ_status = "status";

        //Variables for HandinAssignments table.
        private static final String TABLE_HANDIN = "HANDIN";
        private static final String HANDIN_ccode = COURSES__ccode;
        private static final String HANDIN_nr = "nr";
        private static final String HANDIN_week = "week";
        private static final String HANDIN_assNr = "assNr";
        private static final String HANDIN_status = "status";

        //Variables for Obligatories table.
        private static final String TABLE_OBLIG = "OBLIGATORIES";
        private static final String OBLIG_ccode = COURSES__ccode;
        private static final String OBLIG_type = "type";
        private static final String OBLIG_date = "date";

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
                    + COURSES_cname + " VARCHAR(50), "
                    + COURSES_cstatus + " VARCHAR(50))");

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

            db.execSQL("CREATE TABLE " + TABLE_LABS + " ("
                    + LABS_ccode+ " VARCHAR(50), "
                    + LABS_nr + " VARCHAR(50), "
                    + LABS_week + " INT, "
                    + LABS_assNr + " VARCHAR(50), "
                    + LABS_status + " VARCHAR(50), "
                    + "FOREIGN KEY(" + LABS_ccode + ") REFERENCES " + COURSES__ccode + ")");

            db.execSQL("CREATE TABLE " + TABLE_PROBLEMS + " ("
                    + PROBLEMS_ccode + " VARCHAR(50), "
                    + PROBLEMS_chapter + " VARCHAR(50), "
                    + PROBLEMS_week + " INT, "
                    + PROBLEMS_assNr + " VARCHAR(50), "
                    + PROBLEMS_status + " VARCHAR(50), "
                    + "FOREIGN KEY(" + PROBLEMS_ccode + ") REFERENCES " + COURSES__ccode + ")");

            db.execSQL("CREATE TABLE " + TABLE_READ + " ("
                    + READ_ccode + " VARCHAR(50), "
                    + READ_chapter + " VARCHAR(50), "
                    + READ_week + " INT, "
                    + READ_startPage + " INT, "
                    + READ_stopPage + " INT, "
                    + READ_status + " VARCHAR(50), "
                    + "FOREIGN KEY(" + READ_ccode + ") REFERENCES " + COURSES__ccode + ")");

            db.execSQL("CREATE TABLE " + TABLE_HANDIN + " ("
                    + HANDIN_ccode+ " VARCHAR(50), "
                    + HANDIN_nr + " VARCHAR(50), "
                    + HANDIN_week + " INT, "
                    + HANDIN_assNr + " VARCHAR(50), "
                    + HANDIN_status + " VARCHAR(50), "
                    + "FOREIGN KEY(" + HANDIN_ccode + ") REFERENCES " + COURSES__ccode + ")");

            db.execSQL("CREATE TABLE " + TABLE_OBLIG + " ("
                    + OBLIG_ccode + " VARCHAR(50), "
                    + OBLIG_type + " VARCHAR(50), "
                    + OBLIG_date + " DATE, "
                    + "FOREIGN KEY(" + OBLIG_ccode + ") REFERENCES " + COURSES__ccode + ")");

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
