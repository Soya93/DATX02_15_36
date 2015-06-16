package se.chalmers.datx02_15_36.studeraeffektivt;

import android.util.Log;

import junit.framework.TestCase;

import se.chalmers.datx02_15_36.studeraeffektivt.fragment.MyCountDownTimer;

/**
 * Created by Patricia on 2015-06-16.
 */
public class MyCountDownTimerTest extends TestCase {
    MyCountDownTimer mcdt = new MyCountDownTimer();

    public void testMillisToMinutes() {
        long millis = 4020000;
        int minutes = mcdt.milliSecondsToMin(millis);

        assertEquals(67, minutes);
    }

}
