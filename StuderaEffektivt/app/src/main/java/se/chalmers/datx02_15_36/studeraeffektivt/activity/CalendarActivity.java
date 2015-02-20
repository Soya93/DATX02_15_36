package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class CalendarActivity extends ActionBarActivity {

    private Intent calIntent;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
    }



    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //TODO fix gmail adress
    public void readCalendar(View view) {
        TextView outputText = (TextView) findViewById(R.id.readCalOutput);
        outputText.setText("Hej!!");
        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";

        String[] selectionArgs = new String[] {"eewestman@gmail.com", "com.google",
                "eewestman@gmail.com"};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);


           outputText.setText("calID: " + calID + " Name: " + displayName +
            " Account: " + accountName + " Owner: " + ownerName);


        }

    }
        //TODO fix so this works
    public void addEventManually(View view) {

        Date date = getDate();
        int year = getYear(date);
        int month = getMonth(date);
        int day = getDay(date);


        calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        //startActivity(calIntent);




        calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, "TEST!!!!!");
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "6207");
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Bra jobbat!!! :)");

        GregorianCalendar calDate = new GregorianCalendar(year, month, day);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.getTimeInMillis());


        startActivity(calIntent);
        //asSyncAdapter(uri, "eewestman@gmail.com", "com.google");




        //TODO insert toast message saying tha tan event has successfully been added to the calender

    }

        //TODO fix this!
    public void addEventAuto(View view) {

        TextView textView = (TextView) findViewById(R.id.calendar_text);
        textView.setText(getYear(getDate()) + " " + getMonth(getDate())  + " " + getDay(getDate()));


        int year = 2015;
        int month = 2;
        int day = 10;
        //TODO get date from phone
        // add hour and min and sec?



        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "TEST");
        values.put(CalendarContract.Events.DESCRIPTION, "TEST");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Sweden");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        //
        // ... do something with event ID

    }

    static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }

    private Date getDate() {
        return Calendar.getInstance().getTime();
    }

    private int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    private int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    private int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }

    private Date futureDate(Date date, int daysFromNow) {
        Calendar cal = Calendar.getInstance();
        int year = getYear(date);
        int month = getMonth(date);
        int day = getDay(date);

        if (month == 2) {
            int newYear = year + (month + (day + daysFromNow) / 28) / 12;
            int newMonth = (month + (day + daysFromNow) / 28) % 28;
            int newDay = (day + daysFromNow) % 28;
            cal.set(newYear, newMonth, newDay);

        } else if (month == 1 || month == 3 || month == 5 || month == 7 ||month == 8 || month == 10 || month == 12) {
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



    /** Go to Calendar View.
     * Called when the user clicks the CalendarView button. */
    public void goToCalendarView(View view){
        Intent intent = new Intent(this, CalendarView.class);
        startActivity(intent);
    }

}
