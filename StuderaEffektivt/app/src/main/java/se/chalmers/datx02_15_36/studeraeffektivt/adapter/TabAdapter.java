package se.chalmers.datx02_15_36.studeraeffektivt.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CalendarFrag;
//import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CourseDetailedInfoFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.HomeFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.MyProfileFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.StatsFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.TimerFrag;

/**
 * Created by emmawestman on 15-02-27.
 */
public class TabAdapter extends FragmentPagerAdapter {
    private HomeFrag homeFrag = new HomeFrag();
    private CalendarFrag calendarFrag = new CalendarFrag();
    private TimerFrag timerFrag = new TimerFrag();
    private StatsFrag statsFrag = new StatsFrag();
    private MyProfileFrag myProfileFrag = new MyProfileFrag();

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
                return timerFrag;
            case 3:
                //Statistics fragment activity
                return statsFrag;
            case 4:
                //FifthTab fragment activity
                return myProfileFrag;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }
}
