package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

public class EventActivity extends ActionBarActivity {
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private String title;
    private String location;
    private String description;
    private int notification;
    private Calendar calStart;
    private Calendar calEnd;
    private CalendarFrag calendarFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        calendarFrag = new CalendarFrag();
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

        startDate = (TextView) findViewById(R.id.start_date_input);
        startDate.setOnClickListener(myTextViewHandler);

        startTime = (TextView) findViewById(R.id.start_time_input);
        startTime.setOnClickListener(myTextViewHandler);

        endDate = (TextView) findViewById(R.id.end_date_input);
        endDate.setOnClickListener(myTextViewHandler);

        endTime = (TextView) findViewById(R.id.end_time_input);
        endTime.setOnClickListener(myTextViewHandler);

        startDate.setText(CalendarUtils.DAY + "/" + CalendarUtils.MONTH + "/"
                + CalendarUtils.YEAR);
        endDate.setText(CalendarUtils.DAY + "/" + CalendarUtils.MONTH + "/"
                + CalendarUtils.YEAR);
        startTime.setText(CalendarUtils.HOUR + ":" + CalendarUtils.MINUTE);
        endTime.setText(CalendarUtils.HOUR +1 + ":" + CalendarUtils.MINUTE);

        calStart = Calendar.getInstance();
        calEnd = Calendar.getInstance();
        calEnd.add(Calendar.HOUR_OF_DAY, CalendarUtils.HOUR +1);
    }


    private void goToTextView(TextView v) {
        int id = v.getId();
        Log.i("goToTextView", id +"");

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
        }
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
                    calStart.set(Integer.parseInt(year1), Integer.parseInt(month1), Integer.parseInt(day1));
                    startDate.setText(day1 + "/" + month1 + "/" + year1);
                } else {
                    calEnd.set(Integer.parseInt(year1), Integer.parseInt(month1), Integer.parseInt(day1));
                    endDate.setText(day1 + "/" + month1 + "/" + year1);
                }
            }
        };

        DatePickerDialog datePickerDialog  = new DatePickerDialog(EventActivity.this, datePickerListener, CalendarUtils.YEAR,
                CalendarUtils.MONTH, CalendarUtils.DAY);
        datePickerDialog.show();
        datePickerDialog.setCancelable(false);
        datePickerDialog.setTitle("Select the time");
    }

    public void openTimePickerDialog(final boolean isStart) {
        TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour1 = String.valueOf(hourOfDay);
                String minute1 = String.valueOf(minute);

                if (isStart) {
                    calStart.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hour1));
                    calStart.add(Calendar.MINUTE, Integer.parseInt(minute1));
                    startTime.setText(hour1 + ":" + minute1);
                } else {
                    calEnd.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hour1));
                    calEnd.add(Calendar.MINUTE, Integer.parseInt(minute1));
                    endTime.setText(hour1 + ":" + minute1);
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this,
                R.style.Theme_IAPTheme, timePickerListener, CalendarUtils.HOUR,
                CalendarUtils.MINUTE, true);

        timePickerDialog.show();
        timePickerDialog.setCancelable(false);
        timePickerDialog.setTitle("Select the time");
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
        calendarFrag.addEvent(title, location, description, calStart.getTimeInMillis(), calEnd.getTimeInMillis(), getContentResolver());

        onBackPressed();

        CharSequence text = "Händelsen " + title + " har skapats";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }
    public void onCancelButtonClicked(View v) {
        onBackPressed();
    }
    public void setCalendarFrag(CalendarFrag calendarFrag){ this.calendarFrag = calendarFrag; }
}

