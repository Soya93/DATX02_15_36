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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

/**
 * A class representing the model of a calendar
 */
public class CalendarModel {

    private Intent calIntent;
    private Calendar cal;
    private Calendar endDay;
    private Cursor cur;

    public CalendarModel() {
        cal = Calendar.getInstance();

        //set end DAY
        endDay = Calendar.getInstance();
        endDay.setTime(futureDate(CalendarUtils.cal.getTime(), 1));

        cur = null;
    }

    public List<String> readEventsToday(ContentResolver cr) {
        return this.readEvents(cr, CalendarUtils.TODAY_IN_MILLIS, CalendarUtils.TODAY_IN_MILLIS);
    }

    public List<String> readEventsSunday(ContentResolver cr) {
        Calendar sunday = Calendar.getInstance();
        sunday.setTime(futureDate(sunday.getTime(), 2));
        return this.readEvents(cr, sunday.getTimeInMillis(), sunday.getTimeInMillis());
    }

    /**
     * Method which reads the events from a given start- and endinterval
     *
     * @param cr
     * @param startInterval
     * @param endInterval
     */
    public List<String> readEvents(ContentResolver cr, Long startInterval, Long endInterval) {
        List<String> eventTitles = new ArrayList<String>();

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
            eventTitles.add(cur.getString(CalendarUtils.PROJECTION_TITLE_INDEX));
        }
        cur.close();
        return eventTitles;
    }

    //Gets all the events in a certain timeframe (used in calendar for the view)
    public Cursor getEventsCursor(ContentResolver cr, Long startInterval, Long endInterval) {
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        startInterval = checkStartInterval(startInterval);
        endInterval = checkEndInterval(endInterval);
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();
        return cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");
    }


    //Gets the detailed information regarding a specific event from its ID (used when pressed a specific event in the calendar view)
    public Cursor getEventDetailedInfo(ContentResolver cr, Long startInterval, Long endInterval,  Long eventID){
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();

        cur = cr.query(eventsUri, CalendarUtils.EVENT_INFO_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");


        //Prints out all the events in the given interval
        while (cur.moveToNext()) {
            if(cur.getLong(CalendarUtils.EVENT_INFO_ID) == eventID) {
                return cur;
            }
        }
        cur.close();
        //TODO: Exception
        return null;
    }

    /**
     * Checks if the startdate is zero, if so it is set to a good default
     *
     * @param startDate
     */
    private Long checkStartInterval(Long startDate) {
        return startDate == 0L ? CalendarUtils.TODAY_IN_MILLIS : startDate;
    }

    /**
     * Checks if the endDate is zero, if so it is set to a good default
     *
     * @param endDate
     */
    private Long checkEndInterval(Long endDate) {
         return endDate == 0L ?  endDay.getTimeInMillis() : endDate;
    }

    /**
     * Returns the calendar of a user specified by its google email.
     *
     * @param cr
     */
    public List<String> getCalendarNames(ContentResolver cr) {

        List<String> calendarNames = new ArrayList<String>();

        String[] projection = {CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.IS_PRIMARY,
                CalendarContract.Calendars.VISIBLE};
        String selection = String.format("%s = 1", CalendarContract.Calendars.VISIBLE);
        Cursor c = cr.query(CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                null, null);
        while(c.moveToNext()) {
            // the cursor, c, contains all the projection data items
            // access the cursor’s contents by array index as declared in
            // your projection
            long id = c.getLong(0);
            String name = c.getString(1);
            calendarNames.add(name);

        }
        c.close();
        return calendarNames;

    }

    public List<Long> getCalendarIDs(ContentResolver cr) {

        List<Long> calendarIDs = new ArrayList<Long>();

        String[] projection = {CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.IS_PRIMARY,
                CalendarContract.Calendars.VISIBLE};
        String selection = String.format("%s = 1", CalendarContract.Calendars.VISIBLE);
        Cursor c = cr.query(CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                null, null);
        while(c.moveToNext()) {
            // the cursor, c, contains all the projection data items
            // access the cursor’s contents by array index as declared in
            // your projection
            long id = c.getLong(0);

            calendarIDs.add(id);

        }
        c.close();
        return calendarIDs;

    }

    /*
    getCalendars är en map nu... vet inte om denna metod används...
    public List<String> getRepAlt(ContentResolver cr, String accountEmail, String accountType) {
        List<String> events = getCalendars(cr);
        List<String> studySessions = filter(events, "Studiepass");
        return studySessions;
    }
    */

    public List<String> filter(List<String> events, String filterOn) {
        List<String> filteredList = new ArrayList<String>();
        for (String e : events) {
            if (e == filterOn) {
                filteredList.add(e);
            }
        }
        return filteredList;
    }



    /**
     * Calculates the future date depending on the number set in daysFromNow
     *
     * @param date
     * @param daysFromNow
     * @return
     */
    private Date futureDate(Date date, int daysFromNow) {

        if (CalendarUtils.MONTH == 2) {
            int newYear = CalendarUtils.YEAR + (CalendarUtils.MONTH + (CalendarUtils.DAY + daysFromNow) / 28) / 12;
            int newMonth = (CalendarUtils.MONTH + (CalendarUtils.DAY + daysFromNow) / 28) % 28;
            int newDay = (CalendarUtils.DAY + daysFromNow) % 28;
            cal.set(newYear, newMonth, newDay);

        } else if (CalendarUtils.MONTH == 1 || CalendarUtils.MONTH == 3 || CalendarUtils.MONTH == 5 || CalendarUtils.MONTH == 7 || CalendarUtils.MONTH == 8 || CalendarUtils.MONTH == 10 || CalendarUtils.MONTH == 12) {
            int newYear = CalendarUtils.YEAR + (CalendarUtils.MONTH + (CalendarUtils.DAY + daysFromNow) / 31) / 12;
            int newMonth = (CalendarUtils.MONTH + (CalendarUtils.DAY + daysFromNow) / 31) % 31;
            int newDay = (CalendarUtils.DAY + daysFromNow) % 31;
            cal.set(newYear, newMonth, newDay);

        } else {
            int newYear = CalendarUtils.YEAR + (CalendarUtils.MONTH + (CalendarUtils.DAY + daysFromNow) / 30) / 12;
            int newMonth = (CalendarUtils.MONTH + (CalendarUtils.DAY + daysFromNow) / 30) % 30;
            int newDay = (CalendarUtils.DAY + daysFromNow) % 30;
            cal.set(newYear, newMonth, newDay);
        }
        return cal.getTime();
        //TODO fixa så det funkar för skottår också
        //TODO testa
    }


    public Long addEventAuto(ContentResolver cr, String title, Long startMillis, Long endMillis, String location, String description, long calID) {

        startMillis = checkStartInterval(startMillis);
        endMillis = checkEndInterval(endMillis);

        Log.i("start", startMillis + "");
        Log.i("end", endMillis + "");

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        //
        // ... do something with event ID
        return eventID;
    }

    public void editEventAuto(ContentResolver cr, String title, Long startMillis, Long endMillis, String location, String description, long calID, long eventID, int notification) {
        Log.i("CalMod: ", startMillis+"");
        Log.i("CalMod: ", endMillis+"");

        editStartTime(cr, eventID, startMillis);
        editEndTime(cr, eventID, endMillis);
        editTitle(cr, eventID, title);
        editLocation(cr, eventID, location);
        editDescription(cr, eventID, description);
        addNotification(cr, eventID, notification);
        editCalID(cr, eventID, calID);
    }


    //Builds a static Uri used for synchronizing
    static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }

    //deleting an event from a calendar
    public void deleteEvent(ContentResolver cr, long eventID) {
        ContentValues values = new ContentValues(); // remove this line???
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.delete(deleteUri, null, null);
    }


    // methods for modifying an event
    public void editTitle(ContentResolver cr, long eventID, String newTitle) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, newTitle);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }

    public void editStartTime(ContentResolver cr, long eventID, long startMillis) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }

    public void editEndTime(ContentResolver cr, long eventID, long endMillis) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTEND, endMillis);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }

    public void editDescription(ContentResolver cr, long eventID, String description) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DESCRIPTION, description);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }

    public void editLocation(ContentResolver cr, long eventID, String newLocation) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.EVENT_LOCATION, newLocation);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }

    public void editEventColor(ContentResolver cr, long eventID, int color) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.EVENT_COLOR, color);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }

    public void addNotification(ContentResolver cr, long eventID, int min){
        ContentValues values = new ContentValues();
        min = min == -1? CalendarContract.Reminders.MINUTES_DEFAULT: min;
        values.put(CalendarContract.Reminders.MINUTES, min);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

    public void removeNotification(ContentResolver cr, long eventID) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, CalendarContract.Reminders.MINUTES_DEFAULT);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

    public void editCalID(ContentResolver cr, long eventID,  long calID) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }
}