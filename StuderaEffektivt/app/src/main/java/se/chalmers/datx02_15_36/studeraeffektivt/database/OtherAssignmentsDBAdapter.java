package se.chalmers.datx02_15_36.studeraeffektivt.database;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;

        import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;

/**
 * Created by SoyaPanda on 15-06-26.
 */

public class OtherAssignmentsDBAdapter  extends AssignmentsDBAdapter {

    public OtherAssignmentsDBAdapter(Context ctx){
        super(ctx);
    }

    //Variables for OtherAssignments table.
    public static final String TABLE_OTHER = "OTHER";
    public static final String OTHER__id = "_id";
    public static final String OTHER_ccode = CoursesDBAdapter.COURSES__ccode;
    public static final String OTHER_week = "week";
    public static final String OTHER_assNr = "assNr";
    public static final String OTHER_status = "status";


    public long insertAssignment(String courseCode,int id, int week, String assNr, AssignmentStatus status) {
        ContentValues cv = new ContentValues();

        try {
            cv.put(OTHER_ccode, courseCode);
            cv.put(OTHER_week, week);
            cv.put(OTHER_assNr, assNr);
            cv.put(OTHER_status, status.toString());
            cv.put(OTHER__id, id);

            return db.insert(TABLE_OTHER, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public long deleteAssignment(int id){
        try{
            return db.delete(TABLE_OTHER, OTHER__id + "=" +id, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getAssignments() {
        return db.query(TABLE_OTHER, null, null, null, null, null, null);
    }

    public long deleteAssignments(String code){
        Cursor cur = getAssignments();
        Long totAsses= new Long(cur.getCount());
        long nbrRemoved = 0;
        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(OTHER__id));
            nbrRemoved = deleteAssignment(id) > 0? nbrRemoved + 1: 0;
        }
        return totAsses - nbrRemoved;
    }

    public long setDone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(OTHER_status, AssignmentStatus.DONE.toString());

        try {
            return db.update(TABLE_OTHER, cv, OTHER__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public long setUndone(int assignmentId){
        ContentValues cv = new ContentValues();

        cv.put(OTHER_status, AssignmentStatus.UNDONE.toString());

        try {
            return db.update(TABLE_OTHER, cv, OTHER__id + "=" + assignmentId, null);
        }catch (Exception e){
            return -1;
        }
    }

    public Cursor getDoneAssignments(String ccode){

        String selection = OTHER_ccode + " = '" + ccode + "' AND "
                + OTHER_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        return db.query(TABLE_OTHER, null, selection, null, null, null, null);
    }

    public Cursor getAssignments(String ccode){
        String selection = OTHER_ccode + " = '" + ccode + "'";
        return db.query(TABLE_OTHER, null, selection, null, null, null, null);
    }

    public  String getCourse(int id){
        String selection = OTHER__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_OTHER, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(OTHER_ccode));
    }

    public int getWeek(int id){
        String selection = OTHER__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_OTHER, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getInt(cur.getColumnIndex(OTHER_week));
    }

    public  String getAssNr(int id){
        String selection = OTHER__id + " = '" + id + "'";
        Cursor cur =  db.query(TABLE_OTHER, null, selection, null, null, null, null);
        cur.moveToNext();
        return cur.getString(cur.getColumnIndex(OTHER_assNr));
    }

}
