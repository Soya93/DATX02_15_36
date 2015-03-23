package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;

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
        //mWeekView.setOnEventClickListener(mEventClickListener);
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        //mWeekView.setMonthChangeListener(mMonthChangeListener);
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        //mWeekView.setEventLongPressListener(mEventLongPressListener);
        mWeekView.setEventLongPressListener(this);
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

    public void addEventAuto(View view) {
        calendarModel.getCalendars(cr, "sayo.panda.sn@gmail.com", "com.google");
        calendarModel.getCalendars(cr, "eewestman@gmail.com", "com.google");
        Long eventID = this.calendarModel.addEventAuto(cr);;
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

    }

    @Override
    public void onEventLongPress(WeekViewEvent weekViewEvent, RectF rectF) {

    }

   @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
       // Populate the week view with some events.
       List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

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

       return events;
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }


}
