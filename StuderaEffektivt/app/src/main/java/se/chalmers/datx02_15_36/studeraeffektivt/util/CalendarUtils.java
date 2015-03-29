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
            CalendarContract.Instances.EVENT_COLOR     // 4
    };

    // The indices for the projection array above for the events in the calendar
    public static final int PROJECTION_EVENT_ID_INDEX = 0;
    public static final int PROJECTION_BEGIN_INDEX = 1;
    public static final int PROJECTION_TITLE_INDEX = 2;
    public static final int PROJECTION_END_INDEX = 3;
    public static final int PROJECTION_COLOR_INDEX = 4;

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


    /*Projection array for calendar of an account. //TODO:change to Events instead of Instances?
    Creating indices for this array instead of doing dynamic lookups improves performance.*/
    public static final String[] EVENT_INFO_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE,         // 2
            CalendarContract.Instances.END,           // 3
            CalendarContract.Instances.EVENT_LOCATION, // 4
            CalendarContract.Instances.DESCRIPTION,     // 5
            CalendarContract.Instances.CALENDAR_DISPLAY_NAME //6
    };

    // The indices for the projection array above for the events in the calendar
    public static final int EVENT_INFO_ID = 0;
    public static final int EVENT_INFO_BEGIN = 1;
    public static final int EVENT_INFO_TITLE = 2;
    public static final int EVENT_INFO_END = 3;
    public static final int EVENT_INFO_LOCATION = 4;
    public static final int EVENT_INFO_DESCRIPTION = 5;
    public static final int EVENT_INFO_CALENDAR = 6;


    public static final Calendar cal = Calendar.getInstance();



    //get todays date
    public static final int YEAR = cal.get(Calendar.YEAR);
    public static final int MONTH = cal.get(Calendar.MONTH);
    public static final int DAY = cal.get(Calendar.DATE);
    public static final int HOUR = cal.get(Calendar.HOUR_OF_DAY);
    public static final int MINUTE = cal.get(Calendar.MINUTE);
    public static final long TODAY_IN_MILLIS = cal.getTimeInMillis();


    public static final String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Instances.CALENDAR_ID,                 // 0
            CalendarContract.Instances.CALENDAR_DISPLAY_NAME,       //1
            CalendarContract.Instances.CALENDAR_COLOR               //2

    };

    // The indices for the projection array above for the events in the calendar
    public static final int CALENDAR_ID = 0;
    public static final int CALENDAR_DISPLAY_NAME = 1;
    public static final int CALENDAR_COLOR = 2;




    public static String formatDate(long startDateInMillis, long endDateInMillis){
        SimpleDateFormat startFormat = new SimpleDateFormat("EEEE d MMMM, HH:mm");
        SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm");
       return startFormat.format(new Date(startDateInMillis)) + "-" + endFormat.format(new Date(endDateInMillis));
    }
}
