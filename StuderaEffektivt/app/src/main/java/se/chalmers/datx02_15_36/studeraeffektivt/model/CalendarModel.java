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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
        endDay.setTime(futureDate(1));

        cur = null;
    }

    public ArrayList<HomeEventItem> readEventsToday(ContentResolver cr) {
        Log.i("calModel", "readEventsToday");

        ArrayList <HomeEventItem> eventsToday = new ArrayList<>();

        /*

        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        long startInterval = cal.getTimeInMillis();


        cal2.set(Calendar.MINUTE, 59);
        long endInterval = cal2.getTimeInMillis();
        */

        long startInterval = 0L;
        long endInterval = 0L;
        Log.i("calModel", startInterval + " : " + endInterval);

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
            HomeEventItem item = new HomeEventItem();
            item.setTitleS(cur.getString(CalendarUtils.TITLE));
            item.setTimeS(cur.getString(CalendarUtils.EVENT_BEGIN) + "-" + cur.getString(CalendarUtils.EVENT_END));
            item.setLocationS(cur.getString(CalendarUtils.LOCATION));
            item.setTimeToStartS(Calendar.getInstance().getTimeInMillis() - cur.getLong(CalendarUtils.EVENT_BEGIN) +"");
            eventsToday.add(item);
        }
        cur.close();
        return eventsToday;
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
            eventTitles.add(cur.getString(CalendarUtils.TITLE));
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

        cur = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");


        //Prints out all the events in the given interval
        while (cur.moveToNext()) {
            if(cur.getLong(CalendarUtils.EVENT_ID) == eventID) {
                return cur;
            }
        }
        cur.close();
        //TODO: Exception
        return null;
    }

    public int getNotificationTime (ContentResolver cr, Long startInterval, Long endInterval,  Long eventID){
        Uri.Builder eventsUriBuilder = CalendarContract.Reminders.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();
        cur =  CalendarContract.Reminders.query(cr, eventID, CalendarUtils.NOTIFICATION_PROJECTION);

        //Prints out all the events in the given interval
        while (cur.moveToNext()) {
            if(cur.getLong(CalendarUtils.NOTIFICATION_ID) == eventID) {
                Log.i("getNotTIme", "eventid: " + cur.getLong(CalendarUtils.NOTIFICATION_ID) + " time: " + cur.getInt(CalendarUtils.NOTIFICATION_TIME));
                return cur.getInt(CalendarUtils.NOTIFICATION_TIME);
            }
        }
        cur.close();

        return -1;
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

    public Map<Long, String> getCalendarInfo(ContentResolver cr) {
        Map<Long, String> calendarNames = new LinkedHashMap<>();
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();

        int year = CalendarUtils.YEAR;
        int month = CalendarUtils.MONTH;
        Calendar cal = Calendar.getInstance();
        cal.set(year-1, month, 1);
        long startDay = cal.getTimeInMillis();
        cal.set(year+1, month, 1);
        long endDay = cal.getTimeInMillis();

        ContentUris.appendId(eventsUriBuilder, startDay);
        ContentUris.appendId(eventsUriBuilder, endDay);
        Uri eventsUri = eventsUriBuilder.build();

        Cursor c = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");

        while(c.moveToNext()) {
            // the cursor, c, contains all the projection data items
            // access the cursor’s contents by array index as declared in
            // your projection
            Long id = c.getLong(CalendarUtils.CALENDAR_ID);
            String name = c.getString(CalendarUtils.CALENDAR_NAME);

            int isVisible = c.getInt(CalendarUtils.VISIBLE);
            if(!calendarNames.containsKey(id) && isVisible == 1)
                calendarNames.put(id,name);
        }
        c.close();
        return calendarNames;
    }


    public List<String> getCalendarNamesInstances(ContentResolver cr) {

       List<String> calendarNames = new ArrayList<>();
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();

        int year = CalendarUtils.YEAR;
        int month = CalendarUtils.MONTH;
        Calendar cal = Calendar.getInstance();
        cal.set(year-1, month, 1);
        long startDay = cal.getTimeInMillis();
        cal.set(year+1, month, 1);
        long endDay = cal.getTimeInMillis();

        ContentUris.appendId(eventsUriBuilder, startDay);
        ContentUris.appendId(eventsUriBuilder, endDay);
        Uri eventsUri = eventsUriBuilder.build();

        Cursor c = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");

        while(c.moveToNext()) {
            // the cursor, c, contains all the projection data items
            // access the cursor’s contents by array index as declared in
            // your projection
            String name = c.getString(CalendarUtils.CALENDAR_NAME);
            int isVisible = c.getInt(CalendarUtils.VISIBLE);
            if(!calendarNames.contains(name) && isVisible == 1)
                calendarNames.add(name);
        }
        c.close();
        return calendarNames;
    }

    public List<Long> getCalendarIDsInstances(ContentResolver cr) {

        List<Long> calendarIDs = new ArrayList<>();
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();

        int year = CalendarUtils.YEAR;
        int month = CalendarUtils.MONTH;
        Calendar cal = Calendar.getInstance();
        cal.set(year-1, month, 1);
        long startDay = cal.getTimeInMillis();
        cal.set(year+1, month, 1);
        long endDay = cal.getTimeInMillis();

        ContentUris.appendId(eventsUriBuilder, startDay);
        ContentUris.appendId(eventsUriBuilder, endDay);
        Uri eventsUri = eventsUriBuilder.build();

        Cursor c = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");

        while(c.moveToNext()) {
            // the cursor, c, contains all the projection data items
            // access the cursor’s contents by array index as declared in
            // your projection
            Long id = c.getLong(CalendarUtils.CALENDAR_ID);
            int isVisible = c.getInt(CalendarUtils.VISIBLE);
            if(!calendarIDs.contains(id) && isVisible == 1)
                calendarIDs.add(id);
        }
        c.close();
        return calendarIDs;
    }

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
     * @param daysFromNow
     * @return
     */
    private Date futureDate(int daysFromNow) {

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

        public Long addEventAuto(ContentResolver cr, String title, Long startMillis, Long endMillis, String location, String description, long calID, int notification, boolean isAllDay) {

        Log.i("String", cr.toString());

        startMillis = checkStartInterval(startMillis);
        endMillis = checkEndInterval(endMillis);

        TimeZone timeZone = TimeZone.getDefault();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.ALL_DAY, isAllDay? 1:0);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        addNotification(cr, eventID, notification);
        //
        // ... do something with event ID
        return eventID;
    }

    public void editEventAuto(ContentResolver cr, String title, Long startMillis, Long endMillis, String location, String description, long calID, long eventID, int notification, boolean isAllDay) {
        deleteEvent(cr, eventID);
        addEventAuto(cr, title, startMillis, endMillis, location, description, calID, notification, isAllDay);
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
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.delete(deleteUri, null, null);
    }

    private void addNotification(ContentResolver cr, long eventID, int min){
        ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, min);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }


    public void editEventColor(ContentResolver cr, long eventID, int color) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.EVENT_COLOR, color);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }

}