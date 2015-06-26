package se.chalmers.datx02_15_36.studeraeffektivt.database;

import android.content.Context;

import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;

/**
 * Created by SoyaPanda on 15-06-26.
 */
public class AssignmentsDPBAdapter extends  DBAdapter{

    private Context ctx;

    public AssignmentsDPBAdapter(Context ctx){
        super(ctx);
        this.ctx = ctx;
    }

    public DBAdapter getDBAdapter(AssignmentType type){
        switch (type){
            case HANDIN:
                return new HandInAssignmentsDBAdapter(ctx);

            case LAB:
                return new LabAssignmentsDBAdapter(ctx);

            case PROBLEM:
                return new ProblemAssignmentsDBAdapter(ctx);

            case READ:
                return new ReadAssignmentsDBAdapter(ctx);

            case OTHER:
                return new OtherAssignmentsDBAdapter(ctx);

            default:
                //never invoked
                return  null;
        }
    }


}
