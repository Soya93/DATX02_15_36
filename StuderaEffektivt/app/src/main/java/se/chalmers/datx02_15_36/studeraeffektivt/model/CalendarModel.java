package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.view.CalendarView;

/**
 * A class representing the model of a calendar
 */
public class CalendarModel {

    private Calendar cal;
    private Calendar endDay;
    private Cursor cur;
    private ArrayList<CalendarsFilterItem> calendarsFilterItems;
    private ArrayList<CalendarChoiceItem> calendarChoiceItems;
    private ArrayList<CalendarsFilterItem> calendarWritersPermissions;


    public CalendarModel() {
        cal = Calendar.getInstance();

        //set end DAY
        endDay = Calendar.getInstance();
        endDay.setTime(futureDate(1));
        calendarsFilterItems = new ArrayList<>();

        cur = null;
    }

    public ArrayList<HomeEventItem> readEventsToday(ContentResolver cr) {
        ArrayList<HomeEventItem> eventsToday = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        long startInterval = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        long endInterval = cal.getTimeInMillis();

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();
        cur = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");

        //Prints out all the events in the given interval
        while (cur.moveToNext()) {
            HomeEventItem item = new HomeEventItem();

            //Set the time color and title
            item.setId(cur.getLong(CalendarUtils.EVENT_ID));
            long startTime = cur.getLong(CalendarUtils.EVENT_BEGIN);
            long endTime = cur.getLong(CalendarUtils.EVENT_END);
            item.setStartTime(startTime);
            item.setEndTime(endTime);

            int color = cur.getInt(CalendarUtils.EVENT_COLOR);
            color = color == 0 ? cur.getInt(CalendarUtils.CALENDAR_COLOR) : color;
            item.setColor(color);
            item.setCalId(cur.getLong(CalendarUtils.CALENDAR_ID));

            item.setTitleS(cur.getString(CalendarUtils.TITLE));

            // set the time according to which type of event it is
            if (cur.getInt(CalendarUtils.ALL_DAY) == 1) {
                item.setTimeS("Heldag");
            } else {
                //set the time for the event
                item.setTimeS(CalendarView.formatTime(startTime, cur.getLong(CalendarUtils.EVENT_END)));

                //set the time to the start of the event
                item.setTimeToStartS(CalendarView.formatTimeToEvent(CalendarUtils.getTimeToEventStart(startTime)));
                CalendarUtils.isOnGoing(startTime, endTime);
                if (CalendarUtils.isOnGoing(startTime, endTime)) {
                    item.setTimeToStartS("Nu");
                }

            }
            item.setLocationS(cur.getString(CalendarUtils.LOCATION));

            if (cur.getInt(CalendarUtils.ALL_DAY) == 1) {
                eventsToday.add(item);
            } else if (!CalendarUtils.startTimeHasPassed(startTime)) {
                eventsToday.add(item);

            } else if (item.getTimeToStartS().equals("Nu")) {
                eventsToday.add(item);
            }

        }
        cur.close();
        return eventsToday;
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
    public Cursor getEventDetailedInfo(ContentResolver cr, Long startInterval, Long endInterval, Long eventID) {
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();

        cur = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");


        //Prints out all the events in the given interval
        while (cur.moveToNext()) {
            if (cur.getLong(CalendarUtils.EVENT_ID) == eventID) {
                return cur;
            }
        }
        cur.close();
        //TODO: Exception
        return null;
    }

    public int getNotificationTime(ContentResolver cr, Long startInterval, Long endInterval, Long eventID) {
        Uri.Builder eventsUriBuilder = CalendarContract.Reminders.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();
        cur = CalendarContract.Reminders.query(cr, eventID, CalendarUtils.NOTIFICATION_PROJECTION);

        //Prints out all the events in the given interval
        while (cur.moveToNext()) {
            if (cur.getLong(CalendarUtils.NOTIFICATION_EVENT_ID) == eventID) {
                return cur.getInt(CalendarUtils.NOTIFICATION_TIME);
            }
        }
        cur.close();

        return -1;
    }


    private Long checkStartInterval(Long startDate) {
        return startDate == 0L ? CalendarUtils.TODAY_IN_MILLIS : startDate;
    }

    private Long checkEndInterval(Long endDate) {
        return endDate == 0L ? endDay.getTimeInMillis() : endDate;
    }

    public ArrayList<CalendarsFilterItem> getCalendarFilters() {
        return calendarsFilterItems;
    }

    public ArrayList<CalendarChoiceItem> getCalendarChoices() {
        return calendarChoiceItems;
    }

    public ArrayList<CalendarsFilterItem> getCalendarWritersPermissions() { return calendarWritersPermissions; }

    public Map<Long, String> getCalendarInfo(ContentResolver cr) {
        Map<Long, String> calendars = new LinkedHashMap<>();
        calendarsFilterItems = new ArrayList<>();
        calendarChoiceItems = new ArrayList<>();
        calendarWritersPermissions = new ArrayList<>();

        Cursor c = getCalendarCursor(cr);

        while (c.moveToNext()) {
            CalendarsFilterItem filterItem = new CalendarsFilterItem();
            CalendarChoiceItem choiceItem = new CalendarChoiceItem();



            Long id = c.getLong(CalendarUtils.CALENDAR_ID);
            String name = c.getString(CalendarUtils.CALENDAR_NAME);
            int isVisible = c.getInt(CalendarUtils.VISIBLE);
            int hasWritersPermission = c.getInt(CalendarUtils.CALENDAR_ACCESS_LEVEL);








            if (!calendars.containsKey(id) && !calendars.containsValue(name) && isVisible == 1) {
                filterItem.setTitle(name);

                if(hasWritersPermission == 500 || hasWritersPermission == 700 || hasWritersPermission == 600 || hasWritersPermission == 800) {

                    CalendarsFilterItem filterItemPermission = new CalendarsFilterItem();
                    filterItemPermission.setTitle(name);

                    int color = c.getInt(CalendarUtils.EVENT_COLOR);
                    color = color == 0 ? c.getInt(CalendarUtils.CALENDAR_COLOR) : color;
                    filterItemPermission.setColor(color);

                    calendarWritersPermissions.add(filterItemPermission);
                }

                int color = c.getInt(CalendarUtils.EVENT_COLOR);
                color = color == 0 ? c.getInt(CalendarUtils.CALENDAR_COLOR) : color;
                filterItem.setColor(color);

                choiceItem = filterItem;
                calendars.put(id, name);
                calendarsFilterItems.add(filterItem);
                calendarChoiceItems.add(choiceItem);
            }
        }




        c.close();
        return calendars;
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
        values.put(CalendarContract.Events.ALL_DAY, isAllDay ? 1 : 0);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        if (notification != -1) {
            addNotification(cr, eventID, notification, startMillis, endMillis);
        }
        //
        // ... do something with event ID
        return eventID;
    }

    public void editEventAuto(ContentResolver cr, String title, Long startMillis, Long endMillis, String location, String description, long calID, long eventID, int notification, boolean isAllDay) {
        deleteEvent(cr, eventID);
        addEventAuto(cr, title, startMillis, endMillis, location, description, calID, notification, isAllDay);
        //TODO remove notification if it is -1
    }

    //deleting an event from a calendar
    public void deleteEvent(ContentResolver cr, long eventID) {
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.delete(deleteUri, null, null);
    }

    private void addNotification(ContentResolver cr, long eventID, int min, long start, long end) {
        ContentValues values = new ContentValues();
        min = min == -1 ? CalendarUtils.NOTIFICATION_DEFAULT : min;
        if (min != -1) {
            values.put(CalendarContract.Reminders.MINUTES, min);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        } else {
            //removeNotification(cr,  getNotificationID(cr, start, end, eventID));
        }
    }


    public int getNotificationID(ContentResolver cr, Long startInterval, Long endInterval, Long eventID) {
        Uri.Builder eventsUriBuilder = CalendarContract.Reminders.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        cur = CalendarContract.Reminders.query(cr, eventID, CalendarUtils.NOTIFICATION_PROJECTION);

        //Prints out all the events in the given interval
        while (cur.moveToNext()) {
            if (cur.getLong(CalendarUtils.NOTIFICATION_EVENT_ID) == eventID) {
                return cur.getInt(CalendarUtils.NOTIFICATION_ID);
            }
        }
        cur.close();

        return -1;
    }

    private void removeNotification(ContentResolver cr, long reminderID) {
        Uri reminderUri = ContentUris.withAppendedId(
                CalendarContract.Reminders.CONTENT_URI, reminderID);
        cr.delete(reminderUri, null, null);
    }


    public void editEventColor(ContentResolver cr, long eventID, int color) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.EVENT_COLOR, color);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.update(updateUri, values, null, null);
    }

    private Cursor getCalendarCursor(ContentResolver cr) {
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();

        int year = CalendarUtils.YEAR;
        int month = CalendarUtils.MONTH;
        Calendar cal = Calendar.getInstance();
        cal.set(year - 1, month, 1);
        long startDay = cal.getTimeInMillis();
        cal.set(year + 1, month, 1);
        long endDay = cal.getTimeInMillis();

        ContentUris.appendId(eventsUriBuilder, startDay);
        ContentUris.appendId(eventsUriBuilder, endDay);
        Uri eventsUri = eventsUriBuilder.build();

        return cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");
    }


    public int getCalendarColor(ContentResolver cr, String calName) {
        Cursor c = getCalendarCursor(cr);

        while (c.moveToNext()) {
            if (calName.equals(c.getString(CalendarUtils.CALENDAR_NAME))) {
                return c.getInt(CalendarUtils.CALENDAR_COLOR);
            }
        }
        //TODO exceptionhandling
        return 0;

    }

}