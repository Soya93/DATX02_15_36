package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class CalendarActivity extends ActionBarActivity {

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
    private ContentResolver cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

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
        cr = getContentResolver();
    }

    //Events in a calendar
    private static final String DEBUG_TAG = "MyActivity";
    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE          // 2
    };

    // The indices for the projection array above.
    private static final int PROJECTION_EVENT_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;


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


    public void readCalendar(View view) {
        readEvent();
    }

    private Date futureDate(Date date, int daysFromNow) {

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


    public void readEvent() {
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startMillis);
        ContentUris.appendId(eventsUriBuilder, endMillis);
        Uri eventsUri = eventsUriBuilder.build();
        cur = this.getContentResolver().query(eventsUri, INSTANCE_PROJECTION, null, null, CalendarContract.Instances.DTSTART + " ASC");

        while (cur.moveToNext()) {

            Log.d("title: ", cur.getString(PROJECTION_TITLE_INDEX));

        }
        TextView outputText = (TextView) findViewById(R.id.readCalOutput);

    }

    public void addEventManually(View view) {

        calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);

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
    }




    //TODO fix this!
    public void addEventAuto(View view) {

        TextView textView = (TextView) findViewById(R.id.calendar_text);
        textView.setText(year + " " + month  + " " + day);


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

}
