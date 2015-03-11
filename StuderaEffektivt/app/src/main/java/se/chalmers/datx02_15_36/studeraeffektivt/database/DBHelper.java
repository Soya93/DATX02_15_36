package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Patricia on 2015-03-11.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pluggapp.db";
    private static final int DATABASE_VERSION = 1;


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Called when database is created.
        //Creation of schemas and initial insert of data.
        db.execSQL("CREATE TABLE COURSES (_coursecode VARCHAR(50) PRIMARY KEY, Name VARCHAR(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS COURSES");
        onCreate(db);
    }
}
