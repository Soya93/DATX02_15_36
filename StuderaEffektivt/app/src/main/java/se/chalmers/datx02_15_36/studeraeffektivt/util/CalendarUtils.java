package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.provider.CalendarContract;

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
            CalendarContract.Instances.ALL_DAY              //12
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
    public static final int YEAR = cal.get(Calendar.YEAR);
    public static final int MONTH = cal.get(Calendar.MONTH);
    public static final int DAY = cal.get(Calendar.DATE);
    public static final int HOUR = cal.get(Calendar.HOUR_OF_DAY);
    public static final int MINUTE = cal.get(Calendar.MINUTE);
    public static final long TODAY_IN_MILLIS = cal.getTimeInMillis();
}
