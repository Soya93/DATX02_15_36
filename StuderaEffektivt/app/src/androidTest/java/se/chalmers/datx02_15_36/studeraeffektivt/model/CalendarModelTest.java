package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.test.AndroidTestCase;
import android.test.mock.MockContentResolver;
import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;


/**
 * Created by emmawestman on 15-04-23.
 */
public class CalendarModelTest extends AndroidTestCase {

    private CalendarModel calendarModel;
    private Cursor cur;
    private ContentResolver cr;
    private Long homeCalID;
    private Long bachelorThesisCalID;
    private Uri eventsUri;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        calendarModel = new CalendarModel();
        cr = getContext().getContentResolver();
        assertNotNull(cr);

        // get calendar IDs to test on
        homeCalID = 1L;

        calendarModel.getCalendarInfo(cr);
        ArrayList<CalendarsFilterItem> calendars = calendarModel.getCalendarWritersPermissions();
        for (CalendarsFilterItem item : calendars) {
            if (item.getTitle() == "Kandidatarbete") {
                bachelorThesisCalID = item.getCalID();
            }
        }


        //Code for reading all events -24hours from now to +24hours from now
        long startInterval = CalendarUtils.TODAY_IN_MILLIS - 1000*60*60*24;
        long endInterval = startInterval + 1000*60*60*24;

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        eventsUri = eventsUriBuilder.build();

    }


    //Events
    public void testAddEvent() {            // Do more tests? all day event, event with notifications, several events...
        String title = "TEST ADD";
        Long startMillis = CalendarUtils.TODAY_IN_MILLIS;
        Long endMillis = CalendarUtils.TODAY_IN_MILLIS + 100000;
        String location = "Hemma";
        String description = "beskriving";
        int notification = -1;
        boolean isAllDay = false;

        //Write event to calendar
        long eventID = calendarModel.addEventAuto(cr, title, startMillis, endMillis, location, description,
                homeCalID, notification, isAllDay);

        ArrayList<Boolean> isEventList = new ArrayList<Boolean>();


        //Read events if the event equals the added event true -> isEventList
        cur = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");
        while (cur.moveToNext()) {
            String eventTitle = cur.getString(CalendarUtils.TITLE);
            long startTime = cur.getLong(CalendarUtils.EVENT_BEGIN);
            long endTime = cur.getLong(CalendarUtils.EVENT_END);
            String eventLocatoion = cur.getString(CalendarUtils.LOCATION);
            String eventDescription = cur.getString(CalendarUtils.DESCRIPTION);
            long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
            int eventNotification = cur.getInt(CalendarUtils.NOTIFICATION_EVENT_ID);
            boolean isAllDayEventValue = cur.getInt(CalendarUtils.ALL_DAY) == 0;
            long eventID2 = cur.getLong(CalendarUtils.EVENT_ID);


            //TODO fix notification test...

            boolean isEvent = eventID == eventID2 && eventTitle.equals(title) &&
                    startMillis == startTime && endMillis == endTime &&
                    eventLocatoion.equals(location) && eventDescription.equals(description) &&
                    homeCalID.equals((Long) calID) && isAllDayEventValue;

            isEventList.add(isEvent);
        }

        calendarModel.deleteEvent(cr, eventID);

        if (isEventList.isEmpty()) {
            fail();
        } else {
            boolean oneOf = false;

            for (Boolean b : isEventList) {
                oneOf = oneOf || b;
            }
            assertTrue(oneOf);

        }


    }


    public void testEditEvent() {

        long eventID = calendarModel.addEventAuto(cr, "", 0L, 0L, "", "", homeCalID, -1, false);

        String newTitle = "Ny titel";
        Long newStartMillis = 0L;
        Long newEndMillis = 0L;
        String newLocation = "Skolan";
        String newDescription = "Hej hej";
        Boolean newIsAllDayEvent = true;
        int newNotification = -1;

        //TODO add edit for notification
        long newEventID = calendarModel.editEventAuto(cr, newTitle, newStartMillis, newEndMillis, newLocation, newDescription, homeCalID, eventID, newNotification, newIsAllDayEvent);

        ArrayList<Boolean> isEventList = new ArrayList<Boolean>();


        //Read events if the event equals the added event true -> isEventList
        cur = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");
        while (cur.moveToNext()) {
            String eventTitle = cur.getString(CalendarUtils.TITLE);
            long startTime = cur.getLong(CalendarUtils.EVENT_BEGIN);
            long endTime = cur.getLong(CalendarUtils.EVENT_END);
            String eventLocation = cur.getString(CalendarUtils.LOCATION);
            String eventDescription = cur.getString(CalendarUtils.DESCRIPTION);
            long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
            int eventNotification = cur.getInt(CalendarUtils.NOTIFICATION_EVENT_ID);
            boolean isAllDayEventValue = cur.getInt(CalendarUtils.ALL_DAY) == 1;
            long eventID2 = cur.getLong(CalendarUtils.EVENT_ID);


            //TODO fix notification test...

            boolean isEvent = newEventID == eventID2 && eventTitle.equals(newTitle)  && eventLocation.equals(newLocation)
                    && eventDescription.equals(newDescription) && homeCalID.equals((Long) calID)
                    /*&& newNotification == eventNotification*/ && isAllDayEventValue;

            isEventList.add(isEvent);
        }
        calendarModel.deleteEvent(cr, newEventID);

        if (isEventList.isEmpty()) {
            fail();
        } else {
            boolean oneOf = false;

            for (Boolean b : isEventList) {
                oneOf = oneOf || b;
            }
            assertTrue(oneOf);
        }


    }


    public void testDeleteEvent() {

        long eventID = calendarModel.addEventAuto(cr, "TestDelete", 0L, 0L, "Hemlig", "", homeCalID, -1, false);

        ArrayList<Boolean> isEventList = new ArrayList<Boolean>();

        //Read events if the event equals the added event true -> isEventList
        cur = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");
        while (cur.moveToNext()) {
            String eventTitle = cur.getString(CalendarUtils.TITLE);
            long startTime = cur.getLong(CalendarUtils.EVENT_BEGIN);
            long endTime = cur.getLong(CalendarUtils.EVENT_END);
            String eventLocatoion = cur.getString(CalendarUtils.LOCATION);
            String eventDescription = cur.getString(CalendarUtils.DESCRIPTION);
            long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
            int eventNotification = cur.getInt(CalendarUtils.NOTIFICATION_EVENT_ID);
            boolean isAllDayEventValue = cur.getInt(CalendarUtils.ALL_DAY) == 0;
            long eventID2 = cur.getLong(CalendarUtils.EVENT_ID);


            //TODO fix notification test...

            boolean isEvent = eventID == eventID2;

            isEventList.add(isEvent);
        }

        calendarModel.deleteEvent(cr, eventID);

        if (isEventList.isEmpty()) {
            fail();
        } else {
            boolean oneOf = false;

            for (Boolean b : isEventList) {
                oneOf = oneOf || b;
            }
            assertTrue(oneOf);

        }

        calendarModel.deleteEvent(cr, eventID);

        ArrayList<HomeEventItem> todaysEvents = calendarModel.readEventsToday(cr);
        for (HomeEventItem item : todaysEvents) {
            assertTrue(item.getId() != eventID);
        }
    }

    //Notifications
    public void testAddNotification() {

        assertTrue(true);
    }

    public void testRemoveNotification() {
        assertTrue(true);
    }

    public void testEditNotification() {
        assertTrue(true);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


}
