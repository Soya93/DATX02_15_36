package se.chalmers.datx02_15_36.studeraeffektivt;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DBaccesser{
    private static final String KEY_studentName = "studentName";
    private static final String DB_name = "Loggedtime";
    private static final String DB_table = "table";
    private static final String KEY_time = "timeSpent";
    private int DBversion=1;
    private static final String Create_Database = "create table DB_table (studentName text not null primary key,"
                                                                          + "time_logged long not null);";
    private SQLiteDatabase database;
    private DBHelper helper;
    private final Context context;

    public DBaccesser(Context ctx){
        this.context= ctx;
        this.helper = new DBHelper(context);

    }

    public DBaccesser open () throws SQLException{
        database = helper.getWritableDatabase();
        return this;

    }

    public long insertValue (String name, String value){
        ContentValues tempContentValues = new ContentValues();
         tempContentValues.put(KEY_studentName,name);
         tempContentValues.put(KEY_time,String.valueOf(value));
    return database.insert(DB_table,null,tempContentValues);}


    private class DBHelper extends SQLiteOpenHelper{

        DBHelper(Context context){
            super(context,DB_name,null,DBversion);
        }

        @Override
        public void onCreate(SQLiteDatabase database){
            try{
                database.execSQL(DB_name);
            }
            catch(SQLException e)
            {
             e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase database,
                              int oldVersion, int newVersion){
            database.execSQL(DB_name);
        }

    }
}