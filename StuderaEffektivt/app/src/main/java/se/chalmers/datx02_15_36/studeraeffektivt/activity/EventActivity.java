package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private TextView notificationView;
    private String title;
    private String location;
    private String description;
    private String calendarName;
    private int notification;
    private Calendar calStart;
    private Calendar calEnd;
    private long calendarID;
    private Bundle savedInstanceState;

    ArrayMap<Integer, String> notificationAlternativesMap = new ArrayMap<>();

    //String[] notificationAlternatives = {"Ingen", "Vid start", "1 minut", "5 minuter", "10 minuter", "15 minuter", "30 minuter", "1 timme", "2 timmar"};


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
        calendarName = getIntent().getStringExtra("calName");
        notification = getIntent().getIntExtra("notification", -1);
        /*
        int index = calendarFrag.getCalendarModel().getCalendarIDs(getContentResolver()).indexOf(calendarID);
        Log.i("oncreate event avtivity: ", calendarID +" " + index);
        calendarName = calendarFrag.getCalendarModel().getCalendarNames(getContentResolver()).get(index);
        Log.i("oncreate event avtivity: ", calendarName);
        */

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
        calendarView.setText(calendarName);
        calendarView.setOnClickListener(myTextViewHandler);

        startDate = (TextView) findViewById(R.id.start_date_input);
        startDate.setOnClickListener(myTextViewHandler);

        startTime = (TextView) findViewById(R.id.start_time_input);
        startTime.setOnClickListener(myTextViewHandler);

        endDate = (TextView) findViewById(R.id.end_date_input);
        endDate.setOnClickListener(myTextViewHandler);

        endTime = (TextView) findViewById(R.id.end_time_input);
        endTime.setOnClickListener(myTextViewHandler);

        notificationView = (TextView) findViewById(R.id.notification_input);
        //notificationView.setText(notificationAlternatives[notification] + "");
        notificationView.setOnClickListener(myTextViewHandler);
        setNotificationMapValues();

        notificationView.setText(notificationAlternativesMap.get(notification));

        if (isInAddMode) {
            startTimeMillis = CalendarUtils.TODAY_IN_MILLIS;

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

            CalendarUtils.cal.set(endYear, endMonth, endDay, endHour, endMinute);
            endTimeMillis = CalendarUtils.cal.getTimeInMillis();

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

      //  startTime.setText(startHour + ":" + startMinute);
       // endTime.setText(String.format("%02d:%02d", endHour, endMinute));

        SimpleDateFormat startFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm");
        startTime.setText(startFormat.format(new Date(startTimeMillis)));
        endTime.setText(endFormat.format(new Date(endTimeMillis)));

        calStart = Calendar.getInstance();
        calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, endHour);
    }

    private void setNotificationMapValues(){
        notificationAlternativesMap.put(-1, "Ingen");
        notificationAlternativesMap.put(0, "Vid start");
        notificationAlternativesMap.put(1, "1 minut");
        notificationAlternativesMap.put(5, "5 minuter");
        notificationAlternativesMap.put(10, "10 minuter");
        notificationAlternativesMap.put(15, "15 minuter");
        notificationAlternativesMap.put(30, "30 minuter");
        notificationAlternativesMap.put(60, "1 timme");
        notificationAlternativesMap.put(120, "2 timmar");
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
            case R.id.notification_input:
                Log.i("click on text view", " notification");
                chooseNotification();
                break;
        }
    }

    public Dialog openCalendarPickerDialog(Bundle savedInstanceState) {

        final List<String> calNames = calendarFrag.getCalendarModel().getCalendarNames(getContentResolver());
        final List<Long> calIDs = calendarFrag.getCalendarModel().getCalendarIDs(getContentResolver());
        final String[] calendars = new String[calNames.size()];
        for (int i = 0; i < calNames.size(); i++) {
            calendars[i] = calNames.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Välj kalender")
                .setItems(calendars, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        calendarID = calIDs.get(which);
                        calendarName = calendars[which];
                        calendarView.setText(calendarName);
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

                if (isStart) {
                    startYear = selectedYear;
                    startMonth = selectedMonth + 1;
                    startDay = selectedDay;
                    calStart.set(startYear, startMonth, startDay);
                    startDate.setText(startDay + "/" + startMonth + "/" + startYear);
                    if(endDay <= startDay && endMonth == startMonth && endYear == startYear || startMonth > endMonth || startYear > endYear){
                        endDay = startDay;
                        endMonth = startMonth;
                        endYear = startYear;
                        calEnd.set(endYear, endMonth, endDay);
                        endDate.setText(endDay + "/" + endMonth + "/" + endYear);
                    }
                } else {
                    endYear = selectedYear;
                    endMonth = selectedMonth + 1;
                    endDay = selectedDay;
                    if(endDay <= startDay && endMonth == startMonth && endYear == startYear || startMonth > endMonth || startYear > endYear){
                        endDay = startDay;
                        endMonth = startMonth;
                        endYear = startYear;
                    }
                    calEnd.set(endYear, endMonth, endDay);
                    endDate.setText(endDay + "/" + endMonth + "/" + endYear);
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
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                if (isStart) {
                    startHour = hourOfDay;
                    startMinute = minute;
                    calStart.set(Calendar.HOUR_OF_DAY, startHour);
                    calStart.set(Calendar.MINUTE, startMinute);
                    startTime.setText(timeFormat.format(new Date(calStart.getTimeInMillis())));
                    String start = startHour + "";

                    boolean startsWithZero = start.length() == 1;
                    if(endHour <= startHour ||  startsWithZero && endHour >= startHour ){
                        endHour = startHour + 1;
                        calEnd.set(Calendar.HOUR_OF_DAY, endHour);
                        calEnd.set(Calendar.MINUTE, endMinute);
                        endTime.setText(timeFormat.format(new Date(calEnd.getTimeInMillis())));
                    }
                } else {
                    endHour = hourOfDay;
                    if(endHour <= startHour){
                        endHour = startHour + 1;
                    }
                    endMinute = minute;
                    calEnd.set(Calendar.HOUR_OF_DAY, endHour);
                    calEnd.set(Calendar.MINUTE, endMinute);
                    endTime.setText(timeFormat.format(new Date(calEnd.getTimeInMillis())));
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

        Log.i("ok button clicked", isInAddMode + "");
        if (isInAddMode) {

            calendarFrag.addEvent(title, location, description, calStart.getTimeInMillis(), calEnd.getTimeInMillis(), getContentResolver(), calendarID, notification);

            onBackPressed();

            CharSequence text = "Händelsen " + title + " har skapats";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        } else {

            calendarFrag.editEvent(getContentResolver(), title, calStart.getTimeInMillis(), calEnd.getTimeInMillis(), location, description, curEventID, calendarID, notification);

            onBackPressed();

            CharSequence text = "Händelsen " + title + " har redigerats";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }

    public void chooseNotification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] alternatives = notificationAlternativesMap.values().toArray(new String[0]);

        builder.setItems(alternatives, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                notification = notificationAlternativesMap.keyAt(which);
                notificationView.setText(notificationAlternativesMap.valueAt(which));
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onCancelButtonClicked(View v) {
        onBackPressed();
    }

    public void setCalendarFrag(CalendarFrag calendarFrag) {
        this.calendarFrag = calendarFrag;
    }
}