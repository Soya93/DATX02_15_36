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

package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.provider.CalendarContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A class with constants for the Calendar model
 */
public class CalendarUtils {


    /*Projection array for events in the calendar
    Creating indices for this array instead of doing dynamic lookups improves performance.*/
    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE,         // 2
            CalendarContract.Instances.END,            // 3
            CalendarContract.Instances.EVENT_COLOR,   // 4
            CalendarContract.Instances.DESCRIPTION,     // 5
            CalendarContract.Instances.CALENDAR_DISPLAY_NAME,   // 6
            CalendarContract.Instances.EVENT_LOCATION,  // 7
            CalendarContract.Instances.CALENDAR_ID,     // 8
            CalendarContract.Instances.OWNER_ACCOUNT,    // 9
            CalendarContract.Instances.CALENDAR_COLOR,       //10
            CalendarContract.Instances.VISIBLE,              //11
            CalendarContract.Instances.ALL_DAY,             //12
            CalendarContract.Instances.CALENDAR_ACCESS_LEVEL //13


    };

    // The indices for the projection array above for the events in the calendar
    public static final int EVENT_ID = 0;
    public static final int EVENT_BEGIN = 1;
    public static final int TITLE = 2;
    public static final int EVENT_END = 3;
    public static final int EVENT_COLOR= 4;
    public static final int DESCRIPTION = 5;
    public static final int CALENDAR_NAME = 6;
    public static final int LOCATION = 7;
    public static final int CALENDAR_ID = 8;
    public static final int OWNER_ACCOUNT = 9;
    public static final int CALENDAR_COLOR = 10;
    public static final int VISIBLE = 11;
    public static final int ALL_DAY = 12;
    public static final int CALENDAR_ACCESS_LEVEL = 13;



    public static final String[] NOTIFICATION_PROJECTION = new String[]{
            CalendarContract.Reminders._ID, //0
            CalendarContract.Reminders.EVENT_ID, // 1
            CalendarContract.Reminders.MINUTES, //2
    };
    public static final int NOTIFICATION_ID = 0;
    public static final int NOTIFICATION_EVENT_ID = 1;
    public static final int NOTIFICATION_TIME = 2;
    public static final int NOTIFICATION_DEFAULT = CalendarContract.Reminders.MINUTES_DEFAULT;

    public static final Calendar cal = Calendar.getInstance();

    //get todays date
    public static int YEAR = cal.get(Calendar.YEAR);
    public static int MONTH = cal.get(Calendar.MONTH);
    public static int DAY = cal.get(Calendar.DATE);
    public static int HOUR = cal.get(Calendar.HOUR_OF_DAY);
    public static int MINUTE = cal.get(Calendar.MINUTE);
    public static long TODAY_IN_MILLIS = cal.getTimeInMillis();

    public static void update() {
        Calendar newCal = Calendar.getInstance();
        YEAR = newCal.get(Calendar.YEAR);
        MONTH = newCal.get(Calendar.MONTH);
        DAY = newCal.get(Calendar.DATE);
        HOUR = newCal.get(Calendar.HOUR_OF_DAY);
        MINUTE = newCal.get(Calendar.MINUTE);
        TODAY_IN_MILLIS = newCal.getTimeInMillis();
    }


    public static final String[] CALENDAR_CC_PROJECTION = {CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_TIME_ZONE,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.IS_PRIMARY,
            CalendarContract.Calendars.VISIBLE};

    public static final int C_CALENDAR_ID = 0;
    public static final int C_CALENDAR_COLOR =4;



    public static long getTimeToEventStart(long eventStart) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(eventStart);
        int eventH = cal.get(Calendar.HOUR_OF_DAY);
        int eventM = cal.get(Calendar.MINUTE);

        update();
        cal.setTimeInMillis(CalendarUtils.TODAY_IN_MILLIS);
        int todayH = cal.get(Calendar.HOUR_OF_DAY);
        int todayM = cal.get(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, eventH - todayH);
        cal.set(Calendar.MINUTE, eventM - todayM);

        return  cal.getTimeInMillis();
    }

    public static long getTimeUntilTomorrow(long tomorrowMillis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(tomorrowMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        int tomorrowH = cal.get(Calendar.HOUR_OF_DAY);
        int tomorrowM = cal.get(Calendar.MINUTE);

        cal.setTimeInMillis(CalendarUtils.TODAY_IN_MILLIS);
        int todayH = cal.get(Calendar.HOUR_OF_DAY);
        int todayM = cal.get(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, tomorrowH - todayH);
        cal.set(Calendar.MINUTE, tomorrowM - todayM);

        return cal.getTimeInMillis();
    }

    public static boolean startTimeHasPassed(long eventStart){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(eventStart);
        int eventTimeInMin = (cal.get(Calendar.HOUR_OF_DAY)*60) + (cal.get(Calendar.MINUTE));

        cal.setTimeInMillis(CalendarUtils.TODAY_IN_MILLIS);
        int todayTimeInMin = (cal.get(Calendar.HOUR_OF_DAY)*60) + (cal.get(Calendar.MINUTE));

        return eventTimeInMin < todayTimeInMin;
    }

    public static boolean isBeforeTomorrow(long eventStart, long tomorrowMillis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimeToEventStart(eventStart));
        int timeInMin = (cal.get(Calendar.HOUR_OF_DAY)*60) + (cal.get(Calendar.MINUTE));

        cal.setTimeInMillis(getTimeUntilTomorrow(tomorrowMillis));
        int tomorrowInMin = (cal.get(Calendar.HOUR_OF_DAY)*60) + (cal.get(Calendar.MINUTE));

        return tomorrowInMin > timeInMin;
    }

    public static boolean isOnGoing(long eventStart, long eventEnd){
        cal.setTimeInMillis(CalendarUtils.TODAY_IN_MILLIS);
        int nowInMin = (cal.get(Calendar.HOUR_OF_DAY)*60) + (cal.get(Calendar.MINUTE));
        int leftMinutes = (24*60) - ((cal.get(Calendar.HOUR_OF_DAY)*60) + cal.get(Calendar.MINUTE));

        cal.setTimeInMillis(eventEnd);
        int endTimeInMin = (cal.get(Calendar.HOUR_OF_DAY)*60) + (cal.get(Calendar.MINUTE));

        cal.setTimeInMillis(getTimeToEventStart(eventStart));
        int toEventStartMinutes = (cal.get(Calendar.HOUR_OF_DAY)*60) + (cal.get(Calendar.MINUTE));

        return toEventStartMinutes > leftMinutes && endTimeInMin > nowInMin;
    }
}
