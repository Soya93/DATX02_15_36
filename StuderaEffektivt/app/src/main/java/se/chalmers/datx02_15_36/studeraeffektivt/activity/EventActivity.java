package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

public class EventActivity extends ActionBarActivity {
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private TextView titleView;
    private TextView locationView;
    private TextView descriptionView;
    private TextView calendarView;
    private String title;
    private String location;
    private String description;
    private String calendar;
    private int notification;
    private Calendar calStart;
    private Calendar calEnd;
    private long startTimeM;
    private long endTimeM;
    private long calendarID;
    private Bundle savedInstanceState;

    private long startTimeMillis;
    private long endTimeMillis;
    private CalendarFrag calendarFrag;
    private long curEventID;
    private boolean isInAddMode;

    //Setting variables for the time/datepicker
    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;


    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        calendarFrag = new CalendarFrag();
        isInAddMode = getIntent().getBooleanExtra("isInAddMode", true);
        curEventID = getIntent().getLongExtra("eventID", 0L);
        startTimeMillis = getIntent().getLongExtra("startTime", 0L);
        endTimeMillis = getIntent().getLongExtra("endTime", 0L);
        title = getIntent().getStringExtra("title");
        location = getIntent().getStringExtra("location");
        description = getIntent().getStringExtra("description");
        calendarID = getIntent().getLongExtra("calID", 1);
        calendar = calendarFrag.getCalendarModel().getCalendars(getContentResolver()).get(0);


        Log.i("oncreate event avtivity: ", calendarID +"");
                initComponents();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
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


    private void initComponents() {

        View.OnClickListener myTextViewHandler = new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("onClick", "");
                goToTextView((TextView) v);
            }
        };

        titleView = (TextView) findViewById(R.id.title_input);
        titleView.setText(title);

        locationView = (TextView) findViewById(R.id.location_input);
        locationView.setText(location);

        descriptionView = (TextView) findViewById(R.id.description_input);
        descriptionView.setText(description);

        calendarView = (TextView) findViewById(R.id.calendar_lable_input);
        calendarView.setText(calendar);
        calendarView.setOnClickListener(myTextViewHandler);

        startDate = (TextView) findViewById(R.id.start_date_input);
        startDate.setOnClickListener(myTextViewHandler);

        startTime = (TextView) findViewById(R.id.start_time_input);
        startTime.setOnClickListener(myTextViewHandler);

        endDate = (TextView) findViewById(R.id.end_date_input);
        endDate.setOnClickListener(myTextViewHandler);

        endTime = (TextView) findViewById(R.id.end_time_input);
        endTime.setOnClickListener(myTextViewHandler);

        if (isInAddMode) {
            startYear = CalendarUtils.YEAR;
            startMonth = CalendarUtils.MONTH + 1;
            startDay = CalendarUtils.DAY;
            startHour = CalendarUtils.HOUR;
            startMinute = CalendarUtils.MINUTE;

            endYear = CalendarUtils.YEAR;
            endMonth = CalendarUtils.MONTH + 1;
            endDay = CalendarUtils.DAY;
            endHour = CalendarUtils.HOUR + 1;
            endMinute = CalendarUtils.MINUTE;
        } else {


            Calendar c = Calendar.getInstance();

            //Convert start time from millis
            c.setTimeInMillis(startTimeMillis);
            startYear = c.get(Calendar.YEAR);
            startMonth = c.get(Calendar.MONTH) +1;
            startDay = c.get(Calendar.DAY_OF_MONTH);
            startHour = c.get(Calendar.HOUR_OF_DAY);
            startMinute = c.get(Calendar.MINUTE);

            //Convert end time from millis
            c.setTimeInMillis(endTimeMillis);
            endYear = c.get(Calendar.YEAR);
            endMonth = c.get(Calendar.MONTH) + 1;
            endDay = c.get(Calendar.DAY_OF_MONTH);
            endHour = c.get(Calendar.HOUR_OF_DAY);
            endMinute = c.get(Calendar.MINUTE);
        }

        //Set the text of the labels accordingly
        startDate.setText(startDay + "/" + startMonth + "/"
                + startYear);
        endDate.setText(endDay + "/" + endMonth + "/"
                + endYear);
        startTime.setText(startHour + ":" + startMinute);
        endTime.setText(endHour + ":" + endMinute);

        calStart = Calendar.getInstance();
        calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, endHour);
    }


    private void goToTextView(TextView v) {
        int id = v.getId();
        Log.i("goToTextView", id + "");

        switch (id) {
            //Start date ID
            case R.id.start_date_input:
                Log.i("click on text view", "start date");
                openDatePickerDialog(true);
                break;
            // Start time ID
            case R.id.start_time_input:
                Log.i("click on text view", " start time");
                openTimePickerDialog(true);
                break;
            // End date ID
            case R.id.end_date_input:
                Log.i("click on text view", "end date");
                openDatePickerDialog(false);
                break;
            // End time ID
            case R.id.end_time_input:
                Log.i("click on text view", " end time");
                openTimePickerDialog(false);
                break;
            case R.id.calendar_lable_input:
                Log.i("click on text view", " calendar lable");
                Dialog dialog = openCalendarPickerDialog(savedInstanceState);
                dialog.show();

                break;
        }
    }

    public Dialog openCalendarPickerDialog(Bundle savedInstanceState) {

        List<String> cals = calendarFrag.getCalendarModel().getCalendars(getContentResolver());
        final String[] calendars = new String[cals.size()];
        for (int i = 0; i < cals.size(); i++) {
            calendars[i] = cals.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Välj kalender")
                .setItems(calendars, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        calendar = calendars[which];
                        calendarView.setText(calendar);
                    }
                });
        return builder.create();



        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.calendar_picker_dialog, null);
        builder.setView(dialogView);

        ListView calendarList = (ListView) findViewById(R.id.calendar_list);



        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        */
    }

    public void openDatePickerDialog(final boolean isStart) {

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                String year1 = String.valueOf(selectedYear);

                String month1 = String.valueOf(selectedMonth + 1);

                String day1 = String.valueOf(selectedDay);

                if (isStart) {
                    startYear = Integer.parseInt(year1);
                    startMonth = Integer.parseInt(month1);
                    startDay = Integer.parseInt(day1);
                    calStart.set(startYear, startMonth, startDay);
                    startDate.setText(day1 + "/" + month1 + "/" + year1);
                } else {
                    endYear = Integer.parseInt(year1);
                    endMonth = Integer.parseInt(month1);
                    endDay = Integer.parseInt(day1);
                    calEnd.set(endYear, endMonth, endDay);
                    endDate.setText(day1 + "/" + month1 + "/" + year1);
                }
            }
        };

        DatePickerDialog datePickerDialog;
        if (isStart) {
            datePickerDialog = new DatePickerDialog(EventActivity.this, datePickerListener, startYear,
                    startMonth - 1, startDay);

        } else {
            datePickerDialog = new DatePickerDialog(EventActivity.this, datePickerListener, endYear,
                    endMonth - 1, endDay);
        }

        datePickerDialog.show();
        datePickerDialog.setCancelable(true);
    }

    public void openTimePickerDialog(final boolean isStart) {

        TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour1 = String.valueOf(hourOfDay);
                String minute1 = String.valueOf(minute);

                if (isStart) {
                    startHour = Integer.parseInt(hour1);
                    startMinute = Integer.parseInt(minute1);
                    calStart.set(Calendar.HOUR_OF_DAY, startHour);
                    calStart.set(Calendar.MINUTE, startMinute);
                    startTime.setText(hour1 + ":" + minute1);

                } else {
                    endHour = Integer.parseInt(hour1);
                    endMinute = Integer.parseInt(minute1);
                    calEnd.set(Calendar.HOUR_OF_DAY, endHour);
                    calEnd.set(Calendar.MINUTE, endMinute);
                    endTime.setText(hour1 + ":" + minute1);
                }
            }
        };

        TimePickerDialog timePickerDialog;
        if (isStart) {
            timePickerDialog = new TimePickerDialog(EventActivity.this,
                    R.style.Theme_IAPTheme, timePickerListener, startHour,
                    startMinute, true);
        } else {
            timePickerDialog = new TimePickerDialog(EventActivity.this,
                    R.style.Theme_IAPTheme, timePickerListener, endHour,
                    endMinute, true);
        }

        timePickerDialog.show();
        timePickerDialog.setCancelable(true);
    }

    public void onOKButtonClicked(View v) {
        title = ((EditText) findViewById(R.id.title_input)).getText().toString();
        location = ((EditText) findViewById(R.id.location_input)).getText().toString();
        description = ((EditText) findViewById(R.id.description_input)).getText().toString();

        //TODO: Fix with notifications
/*      String notificationString = ((EditText) dialogView.findViewById(R.id.notification_input)).getText().toString();
        notification = -1;
        if (notificationString != null && !notificationString.isEmpty()) {
            notification = Integer.parseInt(notificationString);
        }
        */
        Log.i("ok button clicked", isInAddMode + "");
        if (isInAddMode) {

            calendarFrag.addEvent(title, location, description, calStart.getTimeInMillis(), calEnd.getTimeInMillis(), getContentResolver(), calendarID);


            onBackPressed();

            CharSequence text = "Händelsen " + title + " har skapats";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        } else {

            calendarFrag.editEvent(getContentResolver(), title, calStart.getTimeInMillis(), calEnd.getTimeInMillis(), location, description, curEventID, calendarID);


            //calStart.set(startYear, startMonth, startDay, startHour, startMinute);
            //calEnd.set(endYear, endMonth, endDay, endHour, endMinute);


            onBackPressed();

            CharSequence text = "Händelsen " + title + " har redigerats";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }

    }

    public void onCancelButtonClicked(View v) {
        onBackPressed();
    }


    public void setCalendarFrag(CalendarFrag calendarFrag) {
        this.calendarFrag = calendarFrag;
    }
}