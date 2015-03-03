package se.chalmers.datx02_15_36.studeraeffektivt.model;


import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import se.chalmers.datx02_15_36.studeraeffektivt.activity.CalendarFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.HomeFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.StatsFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.TimerFrag;

/**
 * Created by emmawestman on 15-02-27.
 */
public class TabAdapter extends FragmentPagerAdapter {
    HomeFrag homeFrag = new HomeFrag();
    CalendarFrag calendarFrag = new CalendarFrag();

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Home fragment activity
                return homeFrag;
            case 1:
                // Calendar fragment activity
                return calendarFrag;
            case 2:
                // Timer fragment activity
                return new TimerFrag();
            case 3:
                //Statistics fragment activity
                return new StatsFrag();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
}
