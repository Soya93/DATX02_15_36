package se.chalmers.datx02_15_36.studeraeffektivt;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;


public class DbAccess{
    public static final String KEY_studentName = "studentName";
    public static final String KEY_time = "timeSpent";

    private static final String TAG = "Tag";

    private static final String Database_name = "Loggedtime";
    private static final String Database_table = "timeSpentTable";
    private int DBversion =1;

    private static final String Create_Database =
            "create table if not exists timeSpentTable (studentName VARCHAR not null primary key,"
                                  + "timeSpent long not null);";

    private SQLiteDatabase database;
    private DBHelper Dbhelper;
    private final Context context;

    public DbAccess(Context ctx){
        this.context= ctx;
        this.Dbhelper = new DBHelper(context);

    }

    public DbAccess open () throws SQLException{
        database = Dbhelper.getWritableDatabase();
        return this;

    }
    public void close () {
        Dbhelper.close();
    }


    // Insert record into database

    public long insertRecord (String name, long value){
        ContentValues tempContentValues = new ContentValues();
         tempContentValues.put(KEY_studentName,name);
         tempContentValues.put(KEY_time,value);
    return database.insert(Database_table,null,tempContentValues);}

    public boolean deleteRecond (String studentName){
        return database.delete(Database_table,KEY_studentName + "=" + studentName,null)> 0;
    }

    public Cursor getAllRecords ()
    {   String[] columns = new String [] {KEY_studentName,KEY_time};
        return database.query(Database_table,columns,null,null,null,null,null);

    }



    public Cursor getRecord (String studentName) throws SQLException{
     Cursor cursor = database.query(true,Database_table,new String[]{KEY_studentName, KEY_time},
             KEY_studentName + "=" + studentName,null,null,null,null,null);
                if(cursor!=null){
                    cursor.moveToFirst();
                }
        return cursor;
    }


    public boolean updateRecord (String studentName, long studyTime) {
        ContentValues c = new ContentValues();
        c.put(KEY_studentName,studentName);
        c.put(KEY_time,studyTime);
        return database.update(Database_table,c,KEY_studentName + "=" + studentName,null)>0;
    }

    private class DBHelper extends SQLiteOpenHelper{

        DBHelper(Context context){
            super(context,Database_name,null,DBversion);
        }

        @Override
        /*
        Creates database
         */
        public void onCreate(SQLiteDatabase database){
            try{
                database.execSQL(Database_name);
            }
            catch(SQLException e)
            {
             e.printStackTrace();
            }
        }
        /*
        Better way to move all the date to a new database
        and then drop the old database.
         */

        @Override
        public void onUpgrade(SQLiteDatabase database,
                              int oldVersion, int newVersion){
            Log.w(TAG, "Uppgrading database from version " + oldVersion + "to "
            + newVersion + " which will destroy all data");
            database.execSQL("DROP TABLE IF EXISTS timeSpentTable");
            onCreate(database);
        }

    }
}