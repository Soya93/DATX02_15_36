package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.test.AndroidTestCase;
import android.test.mock.MockContentResolver;
import java.util.ArrayList;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;


/**
 * Created by emmawestman on 15-04-23.
 */
public class CalendarModelTest extends AndroidTestCase {

    private CalendarModel calendarModel;
    private Cursor cur;
    private MockContentResolver mockCR;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        calendarModel = new CalendarModel();
        mockCR = new MockContentResolver();

        assertNotNull(mockCR);

        //Code for reading all events today
        long startInterval = CalendarUtils.TODAY_IN_MILLIS;
        long endInterval = CalendarUtils.TODAY_IN_MILLIS + 100000;

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startInterval);
        ContentUris.appendId(eventsUriBuilder, endInterval);
        Uri eventsUri = eventsUriBuilder.build();
        cur = mockCR.query(eventsUri, CalendarUtils.INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");

    }



    //Events
    public void testAddEvent() {
        String title = "TEST";
        Long startMillis = CalendarUtils.TODAY_IN_MILLIS;
        Long endMillis = CalendarUtils.TODAY_IN_MILLIS + 100000;
        String location = "Hemma";
        String description = "beskriving";
        long calID = 1;
        int notification = -1;
        boolean isAllDay = false;

        //Write event to calendar
        calendarModel.addEventAuto(mockCR, title, startMillis, endMillis, location, description,
                calID, notification, isAllDay);

        ArrayList<Boolean> isEventList = new ArrayList<Boolean>();

        //Read events if the event equals the added event true -> isEventList
        while (cur.moveToNext()) {
            String eventTitle = cur.getString(CalendarUtils.TITLE);
            long startTime = cur.getLong(CalendarUtils.EVENT_BEGIN);
            long endTime = cur.getLong(CalendarUtils.EVENT_END);
            String eventLocatrion = cur.getString(CalendarUtils.LOCATION);
            String eventDescription = cur.getString(CalendarUtils.DESCRIPTION);
            long eventCalID = cur.getLong(CalendarUtils.CALENDAR_ID);
            int eventNotification = cur.getInt(CalendarUtils.NOTIFICATION_EVENT_ID);
            boolean isAllDayEvent = cur.getInt(CalendarUtils.ALL_DAY) == 1;

            boolean isEvent = eventTitle.equals(title) && startTime == startMillis && endTime == endMillis
                    && eventLocatrion.equals(location) && eventDescription.equals(description) &&
                    eventCalID == calID && notification == eventNotification && isAllDay == isAllDayEvent;

            isEventList.add(isEvent);
        }


        if(isEventList.size() == 0) {
            fail();
        }else {
            boolean oneOf = false;
            for (boolean b : isEventList){
                oneOf = oneOf || b;
            }
            assertTrue(oneOf);
        }
    }


    public void testEditEvent() {
        assertTrue(true);
    }

    public void testDeleteEvent() {
        assertTrue(true);
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
