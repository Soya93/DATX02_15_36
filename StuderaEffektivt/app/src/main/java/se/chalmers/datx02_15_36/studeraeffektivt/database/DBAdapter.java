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

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
            + SessionsDBAdapter.SESSIONS__id + " INT PRIMARY KEY, "
            + SessionsDBAdapter.SESSIONS_minutes + " INT, "
            + SessionsDBAdapter.SESSIONS_ccode + " VARCHAR(50), "
            + SessionsDBAdapter.SESSION_timestamp + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + SessionsDBAdapter.SESSION_week + " INT)";

    private static final String CREATE_LABS_TABLE = "CREATE TABLE " + LabAssignmentsDBAdapter.TABLE_LABS + " ("
            + LabAssignmentsDBAdapter.LABS__id + " INT PRIMARY KEY NOT NULL,"
            + LabAssignmentsDBAdapter.LABS_ccode + " VARCHAR(50) NOT NULL, "
            + LabAssignmentsDBAdapter.LABS_nr + " VARCHAR(50) NOT NULL, "
            + LabAssignmentsDBAdapter.LABS_week + " INT NOT NULL, "
            + LabAssignmentsDBAdapter.LABS_assNr + " VARCHAR(50) NOT NULL, "
            + LabAssignmentsDBAdapter.LABS_status + " VARCHAR(50) NOT NULL, "
            + "FOREIGN KEY(" + LabAssignmentsDBAdapter.LABS_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_PROBLEMS_TABLE = "CREATE TABLE " + ProblemAssignmentsDBAdapter.TABLE_PROBLEMS + " ("
            + ProblemAssignmentsDBAdapter.PROBLEMS__id + " INT PRIMARY KEY NOT NULL,"
            + ProblemAssignmentsDBAdapter.PROBLEMS_ccode + " VARCHAR(50) NOT NULL, "
            + ProblemAssignmentsDBAdapter.PROBLEMS_chapter + " VARCHAR(50) NOT NULL, "
            + ProblemAssignmentsDBAdapter.PROBLEMS_week + " INT NOT NULL, "
            + ProblemAssignmentsDBAdapter.PROBLEMS_assNr + " VARCHAR(50) NOT NULL, "
            + ProblemAssignmentsDBAdapter.PROBLEMS_status + " VARCHAR(50) NOT NULL, "
            + "FOREIGN KEY(" + ProblemAssignmentsDBAdapter.PROBLEMS_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_READ_TABLE = "CREATE TABLE " + ReadAssignmentsDBAdapter.TABLE_READ + " ("
            + ReadAssignmentsDBAdapter.READ__id + " INT PRIMARY KEY NOT NULL,"
            + ReadAssignmentsDBAdapter.READ_ccode + " VARCHAR(50) NOT NULL, "
            + ReadAssignmentsDBAdapter.READ_chapter + " VARCHAR(50) NOT NULL, "
            + ReadAssignmentsDBAdapter.READ_week + " INT NOT NULL, "
            + ReadAssignmentsDBAdapter.READ_startPage + " INT NOT NULL, "
            + ReadAssignmentsDBAdapter.READ_endPage + " INT NOT NULL, "
            + ReadAssignmentsDBAdapter.READ_status + " VARCHAR(50) NOT NULL, "
            + "FOREIGN KEY(" + ReadAssignmentsDBAdapter.READ_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_HANDIN_TABLE = "CREATE TABLE " + HandInAssignmentsDBAdapter.TABLE_HANDIN + " ("
            + HandInAssignmentsDBAdapter.HANDIN__id + " INT PRIMARY KEY NOT NULL,"
            + HandInAssignmentsDBAdapter.HANDIN_ccode+ " VARCHAR(50) NOT NULL, "
            + HandInAssignmentsDBAdapter.HANDIN_nr + " VARCHAR(50) NOT NULL, "
            + HandInAssignmentsDBAdapter.HANDIN_week + " INT NOT NULL, "
            + HandInAssignmentsDBAdapter.HANDIN_assNr + " VARCHAR(50) NOT NULL, "
            + HandInAssignmentsDBAdapter.HANDIN_status + " VARCHAR(50) NOT NULL, "
            + "FOREIGN KEY(" + HandInAssignmentsDBAdapter.HANDIN_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_OTHER_TABLE = "CREATE TABLE " + OtherAssignmentsDBAdapter.TABLE_OTHER + " ("
            + OtherAssignmentsDBAdapter.OTHER__id + " INT PRIMARY KEY NOT NULL,"
            + OtherAssignmentsDBAdapter.OTHER_ccode+ " VARCHAR(50) NOT NULL, "
            + OtherAssignmentsDBAdapter.OTHER_week + " INT NOT NULL, "
            + OtherAssignmentsDBAdapter.OTHER_assNr + " VARCHAR(50) NOT NULL, "
            + OtherAssignmentsDBAdapter.OTHER_status + " VARCHAR(50) NOT NULL, "
            + "FOREIGN KEY(" + OtherAssignmentsDBAdapter.OTHER_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_OBLIG_TABLE = "CREATE TABLE " + CoursesDBAdapter.TABLE_OBLIG + " ("
            + CoursesDBAdapter.OBLIG__id + " INT PRIMARY KEY NOT NULL,"
            + CoursesDBAdapter.OBLIG_ccode + " VARCHAR(50) NOT NULL, "
            + CoursesDBAdapter.OBLIG_type + " VARCHAR(50) NOT NULL, "
            + CoursesDBAdapter.OBLIG_date + " DATE NOT NULL, "
            + CoursesDBAdapter.OBLIG_status + " VARCHAR(50) NOT NULL, "
            + "FOREIGN KEY(" + CoursesDBAdapter.OBLIG_ccode + ") REFERENCES " + CoursesDBAdapter.COURSES__ccode + ")";

    private static final String CREATE_TIMEONCOURSE_TABLE = "CREATE TABLE " + CoursesDBAdapter.TABLE_TIMEONCOURSE + "("
            + CoursesDBAdapter.TIMEONCOURSE__ccode + " VARCHAR(50) PRIMARY KEY NOT NULL, "
            + CoursesDBAdapter.TIMEONCOURSE_time + " INT NOT NULL, "
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

            Log.i("DBAdapter", "onCreate");

            db.execSQL(CREATE_COURSES_TABLE);
            db.execSQL(CREATE_TIMEONCOURSE_TABLE);
            db.execSQL(CREATE_OBLIG_TABLE);

            db.execSQL(CREATE_SESSIONS_TABLE);

            db.execSQL(CREATE_HANDIN_TABLE);
            db.execSQL(CREATE_LABS_TABLE);
            db.execSQL(CREATE_OTHER_TABLE);
            db.execSQL(CREATE_PROBLEMS_TABLE);
            db.execSQL(CREATE_READ_TABLE);

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
