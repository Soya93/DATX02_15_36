package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.provider.CalendarContract;

/**
 * A class with constants for the Calendar model
 */
public class CalendarUtils {

    /*Projection array for events in the calendar
Creating indices for this array instead of doing dynamic lookups improves performance.*/
    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE          // 2
    };

    // The indices for the projection array above for the events in the calendar
    public static final int PROJECTION_EVENT_ID_INDEX = 0;
    public static final int PROJECTION_BEGIN_INDEX = 1;
    public static final int PROJECTION_TITLE_INDEX = 2;

    /*Projection array for calendar of an account.
    Creating indices for this array instead of doing dynamic lookups improves performance.*/
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above for the calendar of an account.
    public static final int PROJECTION_ID_INDEX = 0;
    public static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    public static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    public static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

}
