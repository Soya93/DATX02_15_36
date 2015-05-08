package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.adapter.CalendarChoiceAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CalendarFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarChoiceItem;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarsFilterItem;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

public class EventActivity extends ActionBarActivity {
    private CalendarModel calendarModel;
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
    private int color;
    private int notification;
    private Calendar calStart;
    private Calendar calEnd;
    private Long calendarID;
    private Bundle savedInstanceState;
    private boolean isAllDayEvent;
    private Switch allDaySwitch;
    private AlertDialog alertDialog;
    private MenuItem item;
    private long itemID;
    private Activity repetitionActivity;


    Map<Integer, String> notificationAlternativesMap = new LinkedHashMap<>();

    //String[] notificationAlternatives = {"Ingen", "Vid start", "1 minut", "5 minuter", "10 minuter", "15 minuter", "30 minuter", "1 timme", "2 timmar"};


    private long startTimeMillis;
    private long endTimeMillis;
    private CalendarFrag calendarFrag;
    private long eventID;
    private boolean isInAddMode;
    private boolean isInRepMode;

    //Setting variables for the time/datepicker
    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;

    private String startWeekday;
    private String endWeekday;
    private String startMonthS;
    private String endMonthS;


    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        repetitionActivity = (Activity) RepetitionActivity.cntxofParent;

        calendarFrag = new CalendarFrag();
        calendarModel = new CalendarModel();
        isInAddMode = getIntent().getBooleanExtra("isInAddMode", true);
        isInRepMode = getIntent().getBooleanExtra("isInRepMode", false);
        eventID = getIntent().getLongExtra("eventID", 0L);
        startTimeMillis = getIntent().getLongExtra("startTime", 0L);
        endTimeMillis = getIntent().getLongExtra("endTime", 0L);
        title = getIntent().getStringExtra("title");
        location = getIntent().getStringExtra("location");
        description = getIntent().getStringExtra("description");
        calendarID = getIntent().getLongExtra("calID", 1);
        calendarName = getIntent().getStringExtra("calName");
        notification = getIntent().getIntExtra("notification", -1);
        isAllDayEvent = getIntent().getIntExtra("isAllDay", 0) == 1;
        color = getIntent().getIntExtra("color", 0);



        initComponents();

        String title;
        if (isInAddMode) {
            title = "Ny händelse";
        } else if(isInRepMode){
            title = "Nytt Repetitonspass";
        }else {
            title = "Redigera händelse";
        }

        this.setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarColor(color);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if(isInAddMode || isInRepMode) {
            getMenuInflater().inflate(R.menu.menu_event_add, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_event, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_event) {
            onOKButtonClicked();
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.delete_event) {
            deleteEvent(eventID, title, getContentResolver());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initComponents() {

        View.OnClickListener myTextViewHandler = new View.OnClickListener() {
            public void onClick(View v) {
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

        allDaySwitch = (Switch) findViewById(R.id.all_day_switch);
        allDaySwitch.setChecked(isAllDayEvent);
        allDaySwitch.setOnClickListener(myTextViewHandler);

        //add color filter on icons
        ImageView cal = (ImageView) findViewById(R.id.image_calendar);
        cal.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);

        ImageView time = (ImageView) findViewById(R.id.image_time);
        time.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);

        ImageView location = (ImageView) findViewById(R.id.image_location);
        location.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);

        ImageView description = (ImageView) findViewById(R.id.image_description);
        description.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);

        ImageView notification = (ImageView) findViewById(R.id.image_notification);
        notification.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);


        if (isInAddMode || isInRepMode) {
            // get the current time and date for start and stop
            setCurrentTime();

        } else {
            Calendar c = Calendar.getInstance();

            //Convert start time from millis
            c.setTimeInMillis(startTimeMillis);
            startYear = c.get(Calendar.YEAR);
            startMonth = c.get(Calendar.MONTH) + 1;
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


        SimpleDateFormat startDateFormat = new SimpleDateFormat("E d MMM yyyy");
        SimpleDateFormat endDateFormat = new SimpleDateFormat("E d MMM yyyy");
        startDate.setText(startDateFormat.format((new Date(startTimeMillis))));
        endDate.setText(endDateFormat.format((new Date(endTimeMillis))));

        // Set format on the time
        SimpleDateFormat startFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm");
        startTime.setText(startFormat.format(new Date(startTimeMillis)));
        endTime.setText(endFormat.format(new Date(endTimeMillis)));

        calStart = Calendar.getInstance();
        calStart.setTimeInMillis(startTimeMillis);
        calEnd = Calendar.getInstance();
        calEnd.setTimeInMillis(endTimeMillis);
    }

    private void setNotificationMapValues() {
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
                //openCalendarPickerDialog();
                openChooseCalendar();
                break;
            case R.id.notification_input:
                Log.i("click on text view", " notification");
                chooseNotification();
                break;
            case R.id.all_day_switch:
                allDaySwitch.setChecked(allDaySwitch.isChecked());
                isAllDayEvent = allDaySwitch.isChecked();
                hideTimeLabels(isAllDayEvent);
                break;
        }
    }

    private void hideTimeLabels(boolean hide) {
        if (hide) {
            startTime.setVisibility(View.INVISIBLE);
            endTime.setVisibility(View.INVISIBLE);
        } else {
            startTime.setVisibility(View.VISIBLE);
            endTime.setVisibility(View.VISIBLE);
        }
    }

    public void openChooseCalendar() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.calendarchoiceslistview, null);

        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayList<CalendarChoiceItem> calendarsList = new ArrayList<CalendarChoiceItem>();
        calendarModel.getCalendarInfo(getContentResolver());
        final ArrayList<CalendarsFilterItem> filterItemsList = calendarModel.getCalendarWritersPermissions();
        for (CalendarsFilterItem item : filterItemsList) {
            CalendarChoiceItem newItem = new CalendarChoiceItem();
            newItem.setTitle(item.getTitle());
            newItem.setColor(item.getColor());
            calendarsList.add(newItem);
        }
        CalendarChoiceAdapter ad = new CalendarChoiceAdapter(getApplicationContext(), R.layout.calendars_filter_item, R.id.calendar_text, calendarsList);
        listView.setAdapter(ad);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDivider(null);

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setView(view);
        builder.setTitle("Välj kalender");
        builder.create();
        alertDialog = builder.create();
        alertDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CalendarsFilterItem cfi = filterItemsList.get(position);
                calendarID = cfi.getCalID();
                calendarName = cfi.getTitle();
                calendarView.setText(calendarName);
                setActionBarColor(cfi.getColor());
                Log.i("EventActivity", title + " id: "+ id);
                alertDialog.dismiss();
            }
        });
    }

    private void setActionBarColor(int color) {
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(color));
    }


    public void openDatePickerDialog(final boolean isStart) {

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {


                if (isStart) {
                    startYear = selectedYear;
                    startMonth = selectedMonth;
                    startDay = selectedDay;
                    calStart.set(startYear, startMonth, startDay);

                    SimpleDateFormat startDateFormat = new SimpleDateFormat("E d MMM yyyy");
                    startDate.setText(startDateFormat.format((new Date(calStart.getTimeInMillis()))));

                    calStart.set(startYear, startMonth+1, startDay);

                    if (endDay <= startDay && endMonth == startMonth && endYear == startYear || startMonth > endMonth || startYear > endYear) {
                        endDay = startDay;
                        endMonth = startMonth;
                        endYear = startYear;
                        calEnd.set(endYear, endMonth, endDay);

                        SimpleDateFormat endDateFormat = new SimpleDateFormat("E d MMM yyyy");
                        endDate.setText(endDateFormat.format((new Date(calEnd.getTimeInMillis()))));
                        calEnd.set(startYear, startMonth+1, startDay);

                    }
                } else {
                    endYear = selectedYear;
                    endMonth = selectedMonth;
                    endDay = selectedDay;
                    if (endDay <= startDay && endMonth == startMonth && endYear == startYear || startMonth > endMonth || startYear > endYear) {
                        endDay = startDay;
                        endMonth = startMonth;
                        endYear = startYear;
                    }
                    calEnd.set(endYear, endMonth, endDay);

                    SimpleDateFormat endDateFormat = new SimpleDateFormat("E d MMM yyyy");
                    endDate.setText(endDateFormat.format((new Date(calEnd.getTimeInMillis()))));
                    calEnd.set(endYear, endMonth+1, endDay);

                }
            }
        };

        DatePickerDialog datePickerDialog;
        if (isStart) {
            datePickerDialog = new DatePickerDialog(EventActivity.this, datePickerListener, startYear,
                    startMonth, startDay);

        } else {
            datePickerDialog = new DatePickerDialog(EventActivity.this, datePickerListener, endYear,
                    endMonth, endDay);
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
                    if (endHour <= startHour || startsWithZero && endHour >= startHour) {
                        endHour = startHour + 1;
                        endMinute = startMinute;
                        calEnd.set(Calendar.HOUR_OF_DAY, endHour);
                        calEnd.set(Calendar.MINUTE, endMinute);
                        endTime.setText(timeFormat.format(new Date(calEnd.getTimeInMillis())));
                    }
                } else {
                    endHour = hourOfDay;
                    if (endHour <= startHour) {
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
                    R.style.Base_Theme_AppCompat_Light_Dialog, timePickerListener, startHour,
                    startMinute, true);
        } else {
            timePickerDialog = new TimePickerDialog(EventActivity.this,
                    R.style.Base_Theme_AppCompat_Light_Dialog, timePickerListener, endHour,
                    endMinute, true);

        }


        timePickerDialog.show();
        timePickerDialog.setCancelable(true);
    }

    public void onOKButtonClicked() {
        title = ((EditText) findViewById(R.id.title_input)).getText().toString();
        location = ((EditText) findViewById(R.id.location_input)).getText().toString();
        description = ((EditText) findViewById(R.id.description_input)).getText().toString();

        if (isInAddMode || isInRepMode) {
            calendarFrag.getCalendarModel().addEventAuto(getContentResolver(), title, calStart.getTimeInMillis(), calEnd.getTimeInMillis(), location, description, calendarID, notification, isAllDayEvent);
            repetitionActivity.finish();
            onBackPressed();
            CharSequence text = "Händelsen " + title + " har skapats";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        } else {
            calendarFrag.getCalendarModel().editEventAuto(getContentResolver(), title, calStart.getTimeInMillis(), calEnd.getTimeInMillis(), location, description, calendarID, eventID, notification, isAllDayEvent);
            onBackPressed();
            CharSequence text = "Händelsen " + title + " har redigerats";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }

    public void chooseNotification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final List<String> alternatives = new ArrayList<>(notificationAlternativesMap.values());
        final List<Integer> times = new ArrayList<>(notificationAlternativesMap.keySet());


        builder.setItems(alternatives.toArray(new String[0]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                notification = times.get(which);
                notificationView.setText(alternatives.get(which));
                Log.i("notificationname ", alternatives.get(which) + "time " + notification);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void setCalendarFrag(CalendarFrag calendarFrag) {
        this.calendarFrag = calendarFrag;
    }

    private void deleteEvent(long id, String title, ContentResolver cr) {
        CalendarModel calendarModel = calendarFrag.getCalendarModel();
        calendarModel.deleteEvent(cr, id);

        this.finish();

        Context context = getApplicationContext();
        CharSequence text = "Händelsen " + title + " har tagits bort";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void setCurrentTime() {
        CalendarUtils.update();

        startTimeMillis = CalendarUtils.TODAY_IN_MILLIS;

        startYear = CalendarUtils.YEAR;
        startMonth = CalendarUtils.MONTH;
        startDay = CalendarUtils.DAY;
        startHour = CalendarUtils.HOUR;
        startMinute = CalendarUtils.MINUTE;

        endYear = CalendarUtils.YEAR;
        endMonth = CalendarUtils.MONTH;
        endDay = CalendarUtils.DAY;
        endHour = CalendarUtils.HOUR + 1;
        endMinute = CalendarUtils.MINUTE;

        CalendarUtils.cal.set(endYear, endMonth, endDay, endHour, endMinute);
        endTimeMillis = CalendarUtils.cal.getTimeInMillis();

    }
}