package se.chalmers.datx02_15_36.studeraeffektivt.util;

/**
 * Created by SoyaPanda on 15-07-08.
 */
public class AssignmentTypeUtil {

    public static AssignmentType stringToAssignmentType (String type){
        if (type.equals(AssignmentType.HANDIN.toString())) {
            return AssignmentType.HANDIN;

        } else if (type.equals(AssignmentType.LAB.toString())) {
            return AssignmentType.LAB;

        } else if (type.equals(AssignmentType.PROBLEM.toString())) {
            return AssignmentType.PROBLEM;

        } else if (type.equals(AssignmentType.READ.toString())) {
            return AssignmentType.READ;

        } else if (type.equals(AssignmentType.OBLIGATORY.toString())) {
            return AssignmentType.OBLIGATORY;

        } else if (type.equals(AssignmentType.OTHER.toString())) {
            return AssignmentType.OTHER;

        } else if (type.equals(AssignmentType.REPEAT.toString())) {
            return AssignmentType.REPEAT;

        } else {
            return AssignmentType.OTHER;
        }
    }
}
