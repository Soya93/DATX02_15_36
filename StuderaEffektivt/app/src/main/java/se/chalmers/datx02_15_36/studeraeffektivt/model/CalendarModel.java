package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

/**
 * A class representing the model of a calendar
 */
public class CalendarModel {

    private Intent calIntent;
    private Uri uri;
    private Calendar cal;
    private Calendar beginDay;
    private Calendar endDay;
    private int year;
    private int month;
    private int day;
    private long startMillis;
    private long endMillis;
    private Cursor cur;

    public CalendarModel() {
        uri = CalendarContract.Calendars.CONTENT_URI;
        cal = Calendar.getInstance();

        //get todays date
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DATE);

        //set start day
        beginDay = Calendar.getInstance();
        beginDay.set(year, month, day);
        startMillis = beginDay.getTimeInMillis();
        //set end day
        endDay = Calendar.getInstance();
        endDay.setTime(futureDate(beginDay.getTime(), 1));
        endMillis = endDay.getTimeInMillis();

        cur = null;
    }

    /**
     * Method which reads the events from a given start- and endinterval
     * @param cr
     * @param startInterval
     * @param endInterval
     */
    public void readEvents(ContentResolver cr, Long startInterval, Long endInterval) {
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        startInterval = checkStartInterval(startInterval);
        endInterval = checkEndInterval(endInterval);
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();
        cur = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");

        //Prints out all the events in the given interval
        while (cur.moveToNext()) {
            Log.d("title: ", cur.getString(CalendarUtils.PROJECTION_TITLE_INDEX));
        }
    }

    /**
     * Checks if the startdate is zero, if so it is set to a good default
     * @param startDate
     * @return
     */
    private Long checkStartInterval(Long startDate){
        return startDate == 0L? startMillis: startDate;

    }

    /**
     * Checks if the endDate is zero, if so it is set to a good default
     * @param endDate
     * @return
     */
    private Long checkEndInterval(Long endDate){
        return endDate == 0L? endMillis: endDate;

    }

    /**
     * Returns the calendar of a user specified by its google email.
     * @param cr
     * @param accountEmail
     * @param accountType
     */
    public List<String> getCalendars(ContentResolver cr, String accountEmail, String accountType) {
        uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{accountEmail, accountType,
                accountEmail};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, CalendarUtils.EVENT_PROJECTION, selection, selectionArgs, null);

        List<String> calendars = new ArrayList<String>();

        int i = 0;
        while (cur.moveToNext()) {

            calendars.add(cur.getColumnName(i));

            Log.d("title: ", cur.getColumnName(i));
            i++;

        }
        return calendars;
    }

    public List<String> getRepAlt(ContentResolver cr, String accountEmail, String accountType) {
        List<String> events = getCalendars(cr, accountEmail, accountType);
        List<String> studySessions = filter(events, "Studiepass");
        return studySessions;
    }

    public List<String> filter(List<String> events, String filterOn) {
        List<String> filteredList = new ArrayList<String>();
        for(String e :events) {
            if(e == filterOn) {
                filteredList.add(e);
            }
        }
        return filteredList;
    }

    /**
     * Adds a manual event to the calendar with the given parameters as information
     * @param startTime
     * @param endTime
     * @param allDay
     * @param title
     * @param location
     * @param description
     * @return
     */
    public Intent addEventManually(Long startTime, Long endTime, Boolean allDay, String title, String location, String description) {
        calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, title);
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allDay);
        startTime = checkStartInterval(startTime);
        endTime = checkStartInterval(endTime);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        if (endTime.equals(0L)) {
            startTime = this.startMillis;
        }
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        return calIntent;
        //Log.i("cal", CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
    }

    /**
     * Opens the calendar of the users phone where the user may choose which one
     * @return
     */
    public Intent openCalendar() {
        calIntent = new Intent(Intent.ACTION_VIEW);
        calIntent.setData(Uri.parse("content://com.android.calendar/time"));
        return calIntent;
    }

        /**
         * Calculates the future date depending on the number set in daysFromNow
         * @param date
         * @param daysFromNow
         * @return
         */
    private Date futureDate(Date date, int daysFromNow) {

        if (month == 2) {
            int newYear = year + (month + (day + daysFromNow) / 28) / 12;
            int newMonth = (month + (day + daysFromNow) / 28) % 28;
            int newDay = (day + daysFromNow) % 28;
            cal.set(newYear, newMonth, newDay);

        } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            int newYear = year + (month + (day + daysFromNow) / 31) / 12;
            int newMonth = (month + (day + daysFromNow) / 31) % 31;
            int newDay = (day + daysFromNow) % 31;
            cal.set(newYear, newMonth, newDay);

        } else {
            int newYear = year + (month + (day + daysFromNow) / 30) / 12;
            int newMonth = (month + (day + daysFromNow) / 30) % 30;
            int newDay = (day + daysFromNow) % 30;
            cal.set(newYear, newMonth, newDay);
        }
        return cal.getTime();

        //TODO fixa så det funkar för skottår också
        //TODO testa
    }

    /**
     * Adds an automatic event to the calendar
     *
     * @param cr
     * @return the event ID
     */
    public Long addEventAuto(ContentResolver cr) {
        long calID = 3;
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Studiepass");
        values.put(CalendarContract.Events.DESCRIPTION, "Studiepass");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Sweden");
        uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        return Long.parseLong(uri.getLastPathSegment());
        //TODO: fixa så att det sparar
    }


    //Builds a static Uri used for synchronizing
    static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }
}