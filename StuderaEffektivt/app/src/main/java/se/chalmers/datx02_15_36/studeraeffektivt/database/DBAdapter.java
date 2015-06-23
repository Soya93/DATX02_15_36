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
import android.database.SQLException;
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

    private static final String DATABASE_NAME = "studiecoach.db";
    private static final int DATABASE_VERSION = 1; //Has to be incremented to update.

    private final Context context;
    protected DatabaseHelper DBHelper;
    protected SQLiteDatabase db;

    private static final String CREATE_COURSES_TABLE = "CREATE TABLE " + CoursesDBAdapter.TABLE_COURSES + " ("
            + CoursesDBAdapter.COURSES__ccode + " VARCHAR(50) PRIMARY KEY, "
            + CoursesDBAdapter.COURSES_cname + " VARCHAR(50), "
            + CoursesDBAdapter.COURSES_cstatus + " VARCHAR(50))";

    private static final String CREATE_SESSIONS_TABLE = "CREATE TABLE " + SessionsDBAdapter.TABLE_SESSIONS + " ("
            + SessionsDBAdapter.SESSIONS__id + " INTEGER PRIMARY KEY, "
            + SessionsDBAdapter.SESSIONS_minutes + " INT, "
            + SessionsDBAdapter.SESSIONS_ccode + " VARCHAR(50), "
            + SessionsDBAdapter.SESSION_timestamp + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + SessionsDBAdapter.SESSION_week + " INT)";

    private static final String CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE " + AssignmentsDBAdapter.TABLE_ASSIGNMENTS + " ("
            + AssignmentsDBAdapter.ASSIGNMENTS__id + " PRIMARY KEY, "
            + AssignmentsDBAdapter.ASSIGNMENTS_ccode + " VARCHAR(50), "
            + AssignmentsDBAdapter.ASSIGNMENTS_chapter + " INT, "
            + AssignmentsDBAdapter.ASSIGNMENTS_week + " INT, "
            + AssignmentsDBAdapter.ASSIGNMENTS_assNr +" VARCHAR(50), "
            + AssignmentsDBAdapter.ASSIGNMENTS_startPage + " INT, "
            + AssignmentsDBAdapter.ASSIGNMENTS_stopPage + " INT, "
            + AssignmentsDBAdapter.ASSIGNMENTS_type + " VARCHAR(50), "
            + AssignmentsDBAdapter.ASSIGNMENTS_status + " VARCHAR(50))";

    private static final String CREATE_LABS_TABLE = "CREATE TABLE " + AssignmentsDBAdapter.TABLE_LABS + " ("
            + AssignmentsDBAdapter.LABS_ccode + " VARCHAR(50), "
            + AssignmentsDBAdapter.LABS_nr + " VARCHAR(50), "
            + AssignmentsDBAdapter.LABS_week + " INT, "
            + AssignmentsDBAdapter.LABS_assNr + " VARCHAR(50), "
            + AssignmentsDBAdapter.LABS_status + " VARCHAR(50), "
            + "FOREIGN KEY(" + AssignmentsDBAdapter.LABS_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_PROBLEMS_TABLE = "CREATE TABLE " + AssignmentsDBAdapter.TABLE_PROBLEMS + " ("
            + AssignmentsDBAdapter.PROBLEMS_ccode + " VARCHAR(50), "
            + AssignmentsDBAdapter.PROBLEMS_chapter + " VARCHAR(50), "
            + AssignmentsDBAdapter.PROBLEMS_week + " INT, "
            + AssignmentsDBAdapter.PROBLEMS_assNr + " VARCHAR(50), "
            + AssignmentsDBAdapter.PROBLEMS_status + " VARCHAR(50), "
            + "FOREIGN KEY(" + AssignmentsDBAdapter.PROBLEMS_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_READ_TABLE = "CREATE TABLE " + AssignmentsDBAdapter.TABLE_READ + " ("
            + AssignmentsDBAdapter.READ_ccode + " VARCHAR(50), "
            + AssignmentsDBAdapter.READ_chapter + " VARCHAR(50), "
            + AssignmentsDBAdapter.READ_week + " INT, "
            + AssignmentsDBAdapter.READ_startPage + " INT, "
            + AssignmentsDBAdapter.READ_stopPage + " INT, "
            + AssignmentsDBAdapter.READ_status + " VARCHAR(50), "
            + "FOREIGN KEY(" + AssignmentsDBAdapter.READ_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_HANDIN_TABLE = "CREATE TABLE " + AssignmentsDBAdapter.TABLE_HANDIN + " ("
            + AssignmentsDBAdapter.HANDIN_ccode+ " VARCHAR(50), "
            + AssignmentsDBAdapter.HANDIN_nr + " VARCHAR(50), "
            + AssignmentsDBAdapter.HANDIN_week + " INT, "
            + AssignmentsDBAdapter.HANDIN_assNr + " VARCHAR(50), "
            + AssignmentsDBAdapter.HANDIN_status + " VARCHAR(50), "
            + "FOREIGN KEY(" + AssignmentsDBAdapter.HANDIN_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_OBLIG_TABLE = "CREATE TABLE " + CoursesDBAdapter.TABLE_OBLIG + " ("
            + CoursesDBAdapter.OBLIG_ccode + " VARCHAR(50), "
            + CoursesDBAdapter.OBLIG_type + " VARCHAR(50), "
            + CoursesDBAdapter.OBLIG_date + " DATE, "
            + "FOREIGN KEY(" + CoursesDBAdapter.OBLIG_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_TIMEONCOURSE_TABLE = "CREATE TABLE " + CoursesDBAdapter.TABLE_TIMEONCOURSE + "("
            + CoursesDBAdapter.TIMEONCOURSE__ccode + " VARCHAR(50) PRIMARY KEY, "
            + CoursesDBAdapter.TIMEONCOURSE_time + " INT, "
            + "FOREIGN KEY(" + CoursesDBAdapter.TIMEONCOURSE__ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        this.DBHelper = new DatabaseHelper(this.context);
        this.db = DBHelper.getWritableDatabase();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_COURSES_TABLE);
            db.execSQL(CREATE_TIMEONCOURSE_TABLE);
            db.execSQL(CREATE_OBLIG_TABLE);

            db.execSQL(CREATE_SESSIONS_TABLE);

            db.execSQL(CREATE_ASSIGNMENTS_TABLE);
            db.execSQL(CREATE_PROBLEMS_TABLE);
            db.execSQL(CREATE_READ_TABLE);
            db.execSQL(CREATE_LABS_TABLE);
            db.execSQL(CREATE_HANDIN_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS COURSES");
            onCreate(db);
        }
    }

    /**
     * open the db
     * @return this
     * @throws android.database.SQLException
     * return type: DBAdapter
     */
    public DBAdapter open() throws SQLException
    {
        this.db = this.DBHelper.getWritableDatabase();
        return this;
    }

    /**
     * close the db
     * return type: void
     */
    public void close()
    {
        this.DBHelper.close();
    }

}
