package se.chalmers.datx02_15_36.studeraeffektivt.model;


import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import se.chalmers.datx02_15_36.studeraeffektivt.activity.HomeActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.CalendarActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.LoginActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.Statistics;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.TimerActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.TipActivity;

/**
 * Created by emmawestman on 15-02-27.
 */
public class TabAdapter extends FragmentPagerAdapter {
    HomeActivity homeActivity = new HomeActivity();
    CalendarActivity calendarActivity = new CalendarActivity();

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Home fragment activity
                return homeActivity;
            case 1:
                // Calendar fragment activity
                return calendarActivity;
            case 2:
                // Timer fragment activity
                return new TimerActivity();
            case 3:
                //Statistics fragment activity
                return new Statistics();
            case 4:
                //Fifth fragment activity
                return new TipActivity();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }
}
