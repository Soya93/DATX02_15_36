package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Random;

/**
 * Created by haxmaj0 on 2015-06-22.
 */
public class SessionsDBAdapter extends DBAdapter {

    public SessionsDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for the Sessions table.
    public static final String TABLE_SESSIONS = "SESSIONS";
    public static final String SESSIONS_ccode = "_ccode";
    public static final String SESSIONS__id = "_id";
    public static final String SESSION_timestamp = "timestamp";
    public static final String SESSION_week = "week";
    public static final String SESSIONS_minutes = "minutes";

    public long insertSession(String courseCode, int week, int minutes) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(SESSIONS_ccode, courseCode);
            cv.put(SESSION_week, week);
            cv.put(SESSIONS_minutes, minutes);
            cv.put(SESSIONS__id, getID());

            return db.insert(TABLE_SESSIONS, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public Cursor getSessions() {
        return db.query(TABLE_SESSIONS, null, null, null, null, null, null);
    }

    public Cursor getSessions(int week){
        return db.query(TABLE_SESSIONS, null, SESSION_week + " = '" + week + "'", null, null, null, null);
    }

    public Cursor getMinutes(int week, String ccode){
        String[] columns = {"minutes"};

        return db.query(TABLE_SESSIONS,columns, SESSION_week + " = '" + week + "' AND "+
                SESSIONS_ccode + " = '" + ccode + "'", null, null, null, "minutes");
    }

    public int getSmallestWeek(String ccode){
        String[] columns = {"min(week)"};
        Cursor cursor = db.query(TABLE_SESSIONS, columns, SESSIONS_ccode
                + " = '" + ccode + "'", null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() == 0 || cursor.getInt(0) == 0) {
            return 53;
        }else{
            return cursor.getInt(0);
        }
    }

    public int getSpentTime(String ccode) {
        String[] columns = {"SUM(" + SESSIONS_minutes + ")"};
        String selection = SESSIONS_ccode + " = '" + ccode + "'";
        Cursor cursor = db.query(TABLE_SESSIONS, columns, selection, null, SESSIONS_ccode, null, null);
        cursor.moveToNext();
        if (cursor.getCount() > 0) {
            int i = cursor.getInt(0);
            cursor.close();
            return i;
        } else {
            return 0;
        }
    }

    public long removeSessionAll(String courseCode, int week, int minutes) {

        try{
            return db.delete(TABLE_SESSIONS,
                    SESSIONS_ccode + " = '" + courseCode + "' AND "+
                    SESSION_week + " = '" + week + "' AND " +
                    SESSIONS_minutes + " = '" + minutes + "'",null);
        }catch (Exception e){
            return -1;
        }
    }
    public long removeSession(String courseCode, int week, int minutes) {

            Cursor cur = db.query(TABLE_SESSIONS, null,
                    SESSIONS_ccode + " = '" + courseCode + "' AND "+
                            SESSION_week + " = '" + week + "' AND " +
                            SESSIONS_minutes + " = '" + minutes + "'", null, null, null, null);

            if(cur.moveToNext()) {
                int id = cur.getInt(cur.getColumnIndex(SESSIONS__id));

                Log.i("SDA", "id " + id);
                Log.i("SDA", "cur " + id);


                try {
                    return db.delete(TABLE_SESSIONS, SESSIONS__id + "=" + id, null);
                } catch (Exception e) {
                    return -1;
                }
            }
        return -2;
    }

        private int getID() {
            Random rand = new Random();
            return rand.nextInt((99999999 - 10000000) + 1) + 10000000;
        }

    public long deleteSessions(String ccode){
        try{
            return db.delete(TABLE_SESSIONS, SESSIONS_ccode + " = '" + ccode + "'", null);
        }catch (Exception e){
            return -1;
        }
    }
}
