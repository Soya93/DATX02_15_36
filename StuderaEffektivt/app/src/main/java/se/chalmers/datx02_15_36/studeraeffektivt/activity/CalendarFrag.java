package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarFrag extends Fragment implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    private CalendarModel calendarModel = new CalendarModel();
     ContentResolver cr;
    private View view;
    private int week = -1;
    private AlertDialog.Builder builder;
    private WeekView mWeekView;
    private Map <Long, WeekViewEvent> eventMap;
    boolean hasOnMonthChange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      this.view = inflater.inflate(R.layout.activity_calendar, container, false);
      calendarModel = new CalendarModel();

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) view.findViewById(R.id.weekView);


        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set number of visible days in the calendar view
        mWeekView.setNumberOfVisibleDays(5);
        hasOnMonthChange = false;
        return view;
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

       if(this.eventMap.containsValue(weekViewEvent) && weekViewEvent!= null) {
           //Fetching the information about the event from its object
           long id = weekViewEvent.getId();

           long startTime = weekViewEvent.getStartTime().getTimeInMillis();
           long endTime = weekViewEvent.getEndTime().getTimeInMillis();
           CharSequence name = weekViewEvent.getName();

           //Get a cursor for the detailed information of the event
           Cursor cur = calendarModel.getEventDetailedInfo(cr, startTime, endTime, id);

          //Fetch information from the cursor
           String location = cur.getString(CalendarUtils.EVENT_INFO_LOCATION);
           String description = cur.getString(CalendarUtils.EVENT_INFO_DESCRIPTION);
           String calendar = cur.getString(CalendarUtils.EVENT_INFO_CALENDAR);

           //Set the fetched informaton from the calendar on the textfields
           TextView eventNameLabel = (TextView) dialogView.findViewById(R.id.event_name_label);
           if(eventNameLabel!= null){
               eventNameLabel.setText(name);
           }

           TextView eventTimeLabel = (TextView) dialogView.findViewById(R.id.event_time_label);
           if(eventTimeLabel!= null){
               eventTimeLabel.setText("Tid: " +startTime + " - " + endTime);
           }

           TextView eventLocationLabel = (TextView) dialogView.findViewById(R.id.event_location_label);
           if(eventLocationLabel!= null){
               eventLocationLabel.setText("Plats: " + location);
           }

           TextView eventDescriptionLabel = (TextView) dialogView.findViewById(R.id.event_description_label);
           if(eventDescriptionLabel!= null){
               eventDescriptionLabel.setText("Beskrivning: " + description);
           }

           TextView eventCalendarLabel = (TextView) dialogView.findViewById(R.id.event_calendar_label);
           if(eventCalendarLabel!= null){
               eventCalendarLabel.setText("Kalender: " + calendar);
           }
           cur.close();
       }
        builder.setView(dialogView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setNegativeButton("Redigera", new DialogInterface.OnClickListener() {
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
       if(!hasOnMonthChange){
           hasOnMonthChange = true;
           readEvents();
           return new ArrayList<WeekViewEvent>(eventMap.values());
       }
    Log.i("","onMonthChange");
       List<WeekViewEvent> events = new ArrayList<>();
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
       events.add(event);*/
       return events;
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    private void readEvents() {
       eventMap = new HashMap<>();

        Log.i("hej", "hejdå");


        Cursor cur = calendarModel.getEventsCursor(cr, 0L, 0L);

        while (cur.moveToNext()) {
            long id = cur.getLong(CalendarUtils.PROJECTION_ID_INDEX);


            if(!eventMap.containsKey(id)) {
                String eventName = cur.getString(CalendarUtils.PROJECTION_TITLE_INDEX);
                Log.i("Event namn + id: ", eventName + " " + id);


                Calendar startTime = Calendar.getInstance();
                startTime.setTimeInMillis(cur.getLong(CalendarUtils.PROJECTION_BEGIN_INDEX));

                Calendar endTime = Calendar.getInstance();
                endTime.setTimeInMillis(cur.getLong(CalendarUtils.PROJECTION_END_INDEX));

                WeekViewEvent event = new WeekViewEvent(id, eventName, startTime, endTime);
                event.setColor(getResources().getColor(R.color.pink));

                Log.i("weekevent IDt", event.getId() + "");

                eventMap.put(id, event);

            }
        }
        cur.close();

    }



}
