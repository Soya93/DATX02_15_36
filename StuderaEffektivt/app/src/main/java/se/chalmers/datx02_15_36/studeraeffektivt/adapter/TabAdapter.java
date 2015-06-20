/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

package se.chalmers.datx02_15_36.studeraeffektivt.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CalendarFrag;
//import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CourseDetailedInfoFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.HomeFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.MyStudiesPage;
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
    private MyStudiesPage myStudiesPage = new MyStudiesPage();

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
                return myStudiesPage;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }
}
