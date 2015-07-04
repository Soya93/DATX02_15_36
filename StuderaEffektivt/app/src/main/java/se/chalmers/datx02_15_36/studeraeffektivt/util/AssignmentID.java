package se.chalmers.datx02_15_36.studeraeffektivt.util;

import java.util.Random;

/**
 * Created by SoyaPanda on 15-07-04.
 */
public class AssignmentID {

    public static int getID() {
        Random rand = new Random();
        return rand.nextInt((99999999 - 10000000) + 1) + 10000000;
    }

}
