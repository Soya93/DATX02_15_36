package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

import static se.chalmers.datx02_15_36.studeraeffektivt.R.id.weekView;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarFrag extends Fragment implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    CalendarModel calendarModel = new CalendarModel();
    ContentResolver cr;
    View rootView;
    int week = -1;
    AlertDialog.Builder builder;
    private WeekView mWeekView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       rootView = inflater.inflate(R.layout.activity_calendar, container, false);
       calendarModel = new CalendarModel();

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) rootView.findViewById(R.id.weekView);


        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set number of visible days in the calendar view
        mWeekView.setNumberOfVisibleDays(5);
        return rootView;
    }

    public void setContentResolver(ContentResolver cr){
        this.cr = cr;
    }

    public Intent addStudySession(){
        return calendarModel.addEventManually(0L, 0L, false, "Studiepass", null, null);
    }

    public Intent openCalendar(){
        return calendarModel.openCalendar();
    }

    public List<String> getTodaysEvents() {
        return calendarModel.readEventsToday(cr);
    }

    public List<String> getSundaysEvents() {
        return calendarModel.readEventsSunday(cr);
    }


    public CalendarModel getCalendarModel(){
        return this.calendarModel;
    }

    //Används inte men kommer vara användbart
    public void readCalendar() {
        calendarModel.getCalendars(cr, "sayo.panda.sn@gmail.com", "com.google");

        calendarModel.readEvents(cr, 0L, 0L);
    }

    public void addEventAuto() {
        calendarModel.addEventAuto(cr);
    }

    /*  Whatever is below this line does not work and is not invoked */
    public void createBuilder(){
        builder.create().show();
    }

    public void openDialog(){
       builder = new AlertDialog.Builder(getActivity());

       //the alternatives
       String [] alternatives = {"LV1", "LV2", "LV3", "LV4", "LV5", "LV6", "LV7", "LV8"};
       String tasks = "";

       builder.setTitle("Välj ett pass att repetera")
               .setItems(alternatives, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       int studyWeek = which+1;
                       setWeek(studyWeek);
                      Log.i("weekInOpen", week + "");
                       // The 'which' argument contains the index position
                       // of the selected item
                   }
               });
        //builder.create().show();
   }

    public Intent addRepetition() {
        return calendarModel.addEventManually(0L, 0L, true, "Repititonspass för LV" + week, null, "Repetera " +  "");
    }

    private void setWeek(int week){
        this.week = week;
    }

    public int getWeek(){
        return this.week;
    }

    public AlertDialog.Builder getBuilder(){
        return builder;
    }

    // Calendar view stuff
    @Override
    public void onEventClick(WeekViewEvent weekViewEvent, RectF rectF) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.event_selected_dialog, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onEventLongPress(WeekViewEvent weekViewEvent, RectF rectF) {

    }

   @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
       // Populate the week view with some events.
       List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

       Cursor cur = calendarModel.getEvents(cr, 0L, 0L);

       while (cur.moveToNext()) {
           long id = cur.getLong(CalendarUtils.PROJECTION_ID_INDEX);
           String eventName = cur.getString(CalendarUtils.PROJECTION_TITLE_INDEX);

           Calendar startTime = Calendar.getInstance();
           startTime.setTimeInMillis(cur.getLong(CalendarUtils.PROJECTION_BEGIN_INDEX));

           Calendar endTime = Calendar.getInstance();
           endTime.setTimeInMillis(cur.getLong(CalendarUtils.PROJECTION_END_INDEX));

           WeekViewEvent event = new WeekViewEvent(id, eventName, startTime, endTime);
           event.setColor(getResources().getColor(R.color.pink));
           events.add(event);
       }




        /*
       Calendar startTime = Calendar.getInstance();
       startTime.set(Calendar.HOUR_OF_DAY, 3);
       startTime.set(Calendar.MINUTE, 0);
       startTime.set(Calendar.MONTH, newMonth-1);
       startTime.set(Calendar.YEAR, newYear);
       Calendar endTime = (Calendar) startTime.clone();
       endTime.add(Calendar.HOUR, 1);
       endTime.set(Calendar.MONTH, newMonth-1);
       WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
       event.setColor(getResources().getColor(R.color.pink));
       events.add(event);

       startTime = Calendar.getInstance();
       startTime.set(Calendar.HOUR_OF_DAY, 3);
       startTime.set(Calendar.MINUTE, 30);
       startTime.set(Calendar.MONTH, newMonth-1);
       startTime.set(Calendar.YEAR, newYear);
       endTime = (Calendar) startTime.clone();
       endTime.set(Calendar.HOUR_OF_DAY, 4);
       endTime.set(Calendar.MINUTE, 30);
       endTime.set(Calendar.MONTH, newMonth-1);
       event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
       event.setColor(getResources().getColor(R.color.material_blue_grey_800));
       events.add(event);

       startTime = Calendar.getInstance();
       startTime.set(Calendar.HOUR_OF_DAY, 4);
       startTime.set(Calendar.MINUTE, 20);
       startTime.set(Calendar.MONTH, newMonth-1);
       startTime.set(Calendar.YEAR, newYear);
       endTime = (Calendar) startTime.clone();
       endTime.set(Calendar.HOUR_OF_DAY, 5);
       endTime.set(Calendar.MINUTE, 0);
       event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
       event.setColor(getResources().getColor(R.color.green));
       events.add(event);

       startTime = Calendar.getInstance();
       startTime.set(Calendar.HOUR_OF_DAY, 5);
       startTime.set(Calendar.MINUTE, 30);
       startTime.set(Calendar.MONTH, newMonth-1);
       startTime.set(Calendar.YEAR, newYear);
       endTime = (Calendar) startTime.clone();
       endTime.add(Calendar.HOUR_OF_DAY, 2);
       endTime.set(Calendar.MONTH, newMonth-1);
       event = new WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
       event.setColor(getResources().getColor(R.color.material_blue_grey_800));
       events.add(event);

       startTime = Calendar.getInstance();
       startTime.set(Calendar.HOUR_OF_DAY, 5);
       startTime.set(Calendar.MINUTE, 0);
       startTime.set(Calendar.MONTH, newMonth-1);
       startTime.set(Calendar.YEAR, newYear);
       startTime.add(Calendar.DATE, 1);
       endTime = (Calendar) startTime.clone();
       endTime.add(Calendar.HOUR_OF_DAY, 3);
       endTime.set(Calendar.MONTH, newMonth - 1);
       event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
       event.setColor(getResources().getColor(R.color.green));
       events.add(event);

       startTime = Calendar.getInstance();
       startTime.set(Calendar.DAY_OF_MONTH, 15);
       startTime.set(Calendar.HOUR_OF_DAY, 3);
       startTime.set(Calendar.MINUTE, 0);
       startTime.set(Calendar.MONTH, newMonth-1);
       startTime.set(Calendar.YEAR, newYear);
       endTime = (Calendar) startTime.clone();
       endTime.add(Calendar.HOUR_OF_DAY, 3);
       event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
       event.setColor(getResources().getColor(R.color.yellow));
       events.add(event);

       startTime = Calendar.getInstance();
       startTime.set(Calendar.DAY_OF_MONTH, 1);
       startTime.set(Calendar.HOUR_OF_DAY, 3);
       startTime.set(Calendar.MINUTE, 0);
       startTime.set(Calendar.MONTH, newMonth-1);
       startTime.set(Calendar.YEAR, newYear);
       endTime = (Calendar) startTime.clone();
       endTime.add(Calendar.HOUR_OF_DAY, 3);
       event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
       event.setColor(getResources().getColor(R.color.pink));
       events.add(event);

       startTime = Calendar.getInstance();
       startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
       startTime.set(Calendar.HOUR_OF_DAY, 15);
       startTime.set(Calendar.MINUTE, 0);
       startTime.set(Calendar.MONTH, newMonth-1);
       startTime.set(Calendar.YEAR, newYear);
       endTime = (Calendar) startTime.clone();
       endTime.add(Calendar.HOUR_OF_DAY, 3);
       event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
       event.setColor(getResources().getColor(R.color.material_blue_grey_800));
       events.add(event);
       */


       return events;
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }




}
