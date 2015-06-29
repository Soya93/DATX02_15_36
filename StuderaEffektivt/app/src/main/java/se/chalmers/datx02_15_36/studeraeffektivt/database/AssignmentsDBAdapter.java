package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;

/**
 * Created by SoyaPanda on 15-06-29.
 */
public abstract class AssignmentsDBAdapter extends DBAdapter{

    private Context ctx;

    public AssignmentsDBAdapter(Context ctx){
        super(ctx);
        this.ctx = ctx;
    }

    public abstract  long deleteAssignment(int id);

    public abstract Cursor getAssignments();

    public abstract long setDone(int assignmentId);

    public abstract long setUndone(int assignmentId);

    public abstract Cursor getDoneAssignments(String ccode);

    public abstract Cursor getAssignments(String ccode);


    public int getAssignmentsCount(String ccode){
        String HandInSelection = HandInAssignmentsDBAdapter.HANDIN_ccode + " = '" + ccode + "'";
        int nrHandIn = db.query(HandInAssignmentsDBAdapter.TABLE_HANDIN, null, HandInSelection, null, null, null, null).getCount();

        String LabSelection = LabAssignmentsDBAdapter.LABS_ccode + " = '" + ccode + "'";
        int nrLab = db.query(LabAssignmentsDBAdapter.TABLE_LABS, null, LabSelection, null, null, null, null).getCount();

        String ProblemSelection = ProblemAssignmentsDBAdapter.PROBLEMS_ccode + " = '" + ccode + "'";
        int nrProb = db.query(ProblemAssignmentsDBAdapter.TABLE_PROBLEMS, null, ProblemSelection, null, null, null, null).getCount();

        String ReadSelection = ReadAssignmentsDBAdapter.READ_ccode + " = '" + ccode + "'";
        int nrRead = db.query(ReadAssignmentsDBAdapter.TABLE_READ, null, ReadSelection, null, null, null, null).getCount();

        String OtherSelection = OtherAssignmentsDBAdapter.OTHER_ccode + " = '" + ccode + "'";
        int nrOther = db.query(OtherAssignmentsDBAdapter.TABLE_OTHER, null, OtherSelection, null, null, null, null).getCount();

        return nrHandIn + nrLab + nrProb + nrRead + nrOther;
    }


    public int getDoneAssignmentsCount(String ccode){
        String HandInSelection = HandInAssignmentsDBAdapter.HANDIN_ccode + " = '" + ccode + "' AND "
                + HandInAssignmentsDBAdapter.HANDIN_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        int nrHandIn = db.query(HandInAssignmentsDBAdapter.TABLE_HANDIN, null, HandInSelection, null, null, null, null).getCount();

        String LabSelection = LabAssignmentsDBAdapter.LABS_ccode + " = '" + ccode + "' AND "
                + LabAssignmentsDBAdapter.LABS_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        int nrLab = db.query(LabAssignmentsDBAdapter.TABLE_LABS, null, LabSelection, null, null, null, null).getCount();

        String ProblemSelection = ProblemAssignmentsDBAdapter.PROBLEMS_ccode + " = '" + ccode + "' AND "
                + ProblemAssignmentsDBAdapter.PROBLEMS_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        int nrProb = db.query(ProblemAssignmentsDBAdapter.TABLE_PROBLEMS, null, ProblemSelection, null, null, null, null).getCount();

        String ReadSelection = ReadAssignmentsDBAdapter.READ_ccode + " = '" + ccode + "' AND "
                + ReadAssignmentsDBAdapter.READ_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        int nrRead = db.query(ReadAssignmentsDBAdapter.TABLE_READ, null, ReadSelection, null, null, null, null).getCount();

        String OtherSelection = OtherAssignmentsDBAdapter.OTHER_ccode + " = '" + ccode + "' AND "
                + OtherAssignmentsDBAdapter.OTHER_status + " = '" + AssignmentStatus.DONE.toString()+"'";
        int nrOther = db.query(OtherAssignmentsDBAdapter.TABLE_OTHER, null, OtherSelection, null, null, null, null).getCount();

        return nrHandIn + nrLab + nrProb + nrRead + nrOther;
    }
}
