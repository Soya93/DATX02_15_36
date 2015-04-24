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
    private long thisEventID;
    private Long homeCalID;
    private Long bachelorThesisCalID;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        calendarModel = new CalendarModel();
        cr = getContext().getContentResolver();
        assertNotNull(cr);

        // get calendar IDs to test on
        homeCalID = new Long(1);

        calendarModel.getCalendarInfo(cr);
        ArrayList<CalendarsFilterItem> calendars = calendarModel.getCalendarWritersPermissions();
        for(CalendarsFilterItem item : calendars) {
            if(item.getTitle() == "Kandidatarbete") {
                bachelorThesisCalID = item.getCalID();
            }
        }


        //Code for reading all events today
        long startInterval = CalendarUtils.TODAY_IN_MILLIS;
        long endInterval = CalendarUtils.TODAY_IN_MILLIS + 100000;

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();
        cur = cr.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");

    }



    //Events
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void testAddEvent() {            // Do more tests? all day event, event with notifications, several events...
        String title = "TEST";
        Long startMillis = CalendarUtils.TODAY_IN_MILLIS;
        Long endMillis = CalendarUtils.TODAY_IN_MILLIS + 100000;
        String location = "Hemma";
        String description = "beskriving";
        int notification = -1;
        boolean isAllDay = false;

        //Write event to calendar
        calendarModel.addEventAuto(cr, title, startMillis, endMillis, location, description,
                homeCalID, notification, isAllDay);

        ArrayMap<Long, Boolean> isEvenMap = new ArrayMap<Long, Boolean>();

        long eventID = -1;

        //Read events if the event equals the added event true -> isEvenMap
        while (cur.moveToNext()) {
            String eventTitle = cur.getString(CalendarUtils.TITLE);
            long startTime = cur.getLong(CalendarUtils.EVENT_BEGIN);
            long endTime = cur.getLong(CalendarUtils.EVENT_END);
            String eventLocatrion = cur.getString(CalendarUtils.LOCATION);
            String eventDescription = cur.getString(CalendarUtils.DESCRIPTION);
            long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
            int eventNotification = cur.getInt(CalendarUtils.NOTIFICATION_EVENT_ID);
            boolean isAllDayEvent = cur.getInt(CalendarUtils.ALL_DAY) == 1;
            eventID = cur.getLong(CalendarUtils.EVENT_ID);


            //TODO fix epsilon on start and end time for the event
            //TODO fix notification test...

            boolean isEvent = eventTitle.equals(title)
                    && eventLocatrion.equals(location) && eventDescription.equals(description) &&
                    homeCalID.equals((Long)calID) && isAllDay == isAllDayEvent;

            isEvenMap.put(eventID, isEvent);
        }


        if(isEvenMap.isEmpty()) {
            fail();
        }else {
            boolean oneOf = false;
            ArrayList<Boolean> values = new ArrayList<Boolean>(isEvenMap.values());
            for (int i=0; i<values.size(); i++) {
                Boolean b = values.get(i);
                if(b) {
                    thisEventID = isEvenMap.keyAt(i);
                }
                oneOf = oneOf || b;
            }

            assertTrue(oneOf);
        }
    }

/*
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void testEditEvent() {
        String newTitle = "Ny titel";
        Long newStartMillis = 0L ;
        Long newEndMilllis = 0L;
        String newLocation = "Skolan";
        String newDescription = "Hej hej";
        Boolean newIsAllDayEvent = true;
        int newNotification = -1;

        //TODO add edit for notification
        calendarModel.editEventAuto(cr, newTitle, newStartMillis, newEndMilllis, newLocation, newDescription, homeCalID, thisEventID, newNotification, newIsAllDayEvent);

        ArrayMap<Long, Boolean> isEvenMap = new ArrayMap<Long, Boolean>();

        long eventID = -1;

        //Read events if the event equals the added event true -> isEvenMap
        while (cur.moveToNext()) {
            String eventTitle = cur.getString(CalendarUtils.TITLE);
            long startTime = cur.getLong(CalendarUtils.EVENT_BEGIN);
            long endTime = cur.getLong(CalendarUtils.EVENT_END);
            String eventLocatrion = cur.getString(CalendarUtils.LOCATION);
            String eventDescription = cur.getString(CalendarUtils.DESCRIPTION);
            long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
            int eventNotification = cur.getInt(CalendarUtils.NOTIFICATION_EVENT_ID);
            boolean isAllDayEvent = cur.getInt(CalendarUtils.ALL_DAY) == 0;
            eventID = cur.getLong(CalendarUtils.EVENT_ID);


            //TODO fix epsilon on start and end time for the event
            //TODO fix notification test...

            boolean isEvent = eventTitle.equals(newTitle) /*&& startTime == startMillis && endTime == endMillis*/
            //       && eventLocatrion.equals(newLocation) && eventDescription.equals(newDescription) &&
            //        homeCalID.equals((Long)calID) /*&& notification == eventNotification && newIsAllDayEvent == isAllDayEvent*/;

           // isEvenMap.put(eventID, isEvent);
      //  }

/*
        if(isEvenMap.isEmpty()) {
            fail();
        }else {
            boolean oneOf = false;
            ArrayList<Boolean> values = new ArrayList<Boolean>(isEvenMap.values());
            for (int i=0; i<values.size(); i++) {
                Boolean b = values.get(i);
                if(b) {
                    assertTrue(thisEventID == isEvenMap.keyAt(i));
                }
                oneOf = oneOf || b;
            }

            assertTrue(oneOf);
        }
    }
*/


    public void testDeleteEvent() {

        calendarModel.deleteEvent(cr,thisEventID);

       ArrayList<HomeEventItem> todaysEvents = calendarModel.readEventsToday(cr);
       for(HomeEventItem item : todaysEvents) {
           assertTrue(item.getId() != thisEventID);
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
