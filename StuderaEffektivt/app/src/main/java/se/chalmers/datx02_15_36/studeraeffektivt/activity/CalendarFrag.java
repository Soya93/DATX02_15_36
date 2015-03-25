package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;


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
    private AlertDialog.Builder builder;
    private WeekView mWeekView;
    private Map <Long, WeekViewEvent> eventMap;
    boolean hasOnMonthChange;
    private Button studySession;
    private Button repetition;

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
        mWeekView.goToHour(8.0);
        return view;
    }

    private void initComponents() {
        View.OnClickListener myOnlyhandler = new View.OnClickListener() {
            public void onClick(View v) {

                goToButtonView((Button) v);

            }
        };
        studySession = (Button) view.findViewById(R.id.button_add_study_session);
        studySession.setOnClickListener(myOnlyhandler);
       /* repetition = (Button) view.findViewById(R.id.button_add_repetition_session);
        repetition.setOnClickListener(myOnlyhandler);*/
    }

    private void goToButtonView(Button b) {

        String buttonText = b.getText().toString();
        switch (buttonText) {
            case "Lägg till studiepass":
                this.openAddEventDialog();
                break;
            /*case "Lägg till repetitionspass":
                this.addRepetitionSession();
                break;*/
        }
    }

    private void addRepetitionSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //the alternatives
        String[] alternatives = {"LV1", "LV2", "LV3", "LV4", "LV5", "LV6", "LV7", "LV8"};
        builder.setTitle("Välj ett pass att repetera")
                .setItems(alternatives, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO hämta några random uppgifter
                        String tasks = "";
                        int studyWeek = which + 1;
                        startActivity(calendarModel.addEventManually(0L, 0L, true, "Repititonspass för LV" + studyWeek, null, "Repetera " + tasks));

                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public List<String> getTodaysEvents() {
        return calendarModel.readEventsToday(cr);
    }

    public List<String> getSundaysEvents() {
        return calendarModel.readEventsSunday(cr);
    }

    public void setContentResolver(ContentResolver cr){
        this.cr = cr;
    }

    // Calendar view stuff
    @Override
    public void onEventClick(WeekViewEvent weekViewEvent, RectF rectF) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.event_selected_dialog, null);
        final long eventID = weekViewEvent.getId();

       if(this.eventMap.containsValue(weekViewEvent) && weekViewEvent!= null) {
            //Fetching the information about the event from its object
            long startTime = weekViewEvent.getStartTime().getTimeInMillis();
            long endTime = weekViewEvent.getEndTime().getTimeInMillis();
            CharSequence name = weekViewEvent.getName();

            //Get a cursor for the detailed information of the event
            Cursor cur = calendarModel.getEventDetailedInfo(cr, startTime, endTime, eventID);

            //Fetch information from the cursor
            String location = cur.getString(CalendarUtils.EVENT_INFO_LOCATION);
            String description = cur.getString(CalendarUtils.EVENT_INFO_DESCRIPTION);
            String calendar = cur.getString(CalendarUtils.EVENT_INFO_CALENDAR);
            cur.close();

           this.updateEventInfoView(dialogView, name, startTime, endTime, location, description, calendar);
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
                openEditEventDialog(eventID);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void updateEventInfoView(View dialogView, CharSequence name, long startTime, long endTime, String location, String description, String calendar){
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
    }

    public void openEditEventDialog(final long eventID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_event_dialog, null);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                long calID = 1;

                //long statTime =;
                // long endTime = ;
                String title = ((TextView) dialogView.findViewById(R.id.title_input)).getText().toString();
                String location = ((TextView) dialogView.findViewById(R.id.location_input)).getText().toString();
                String description = ((TextView) dialogView.findViewById(R.id.description_input)).getText().toString();
                String notification = ((EditText) dialogView.findViewById(R.id.notification_input)).getText().toString();
                int minutes = -1;
                if(notification!=null) {
                   minutes = Integer.parseInt(notification);
                }
                calendarModel.editTitle(cr, eventID, title);
                calendarModel.editLocation(cr, eventID, location);
                calendarModel.editDescription(cr, eventID, description);
                calendarModel.addNotification(cr, eventID,minutes);
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void openAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_event_dialog, null);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                long calID = 1;

                //long statTime =;
               // long endTime = ;

                String title = ((TextView) dialogView.findViewById(R.id.title_input)).getText().toString();
                String location = ((TextView) dialogView.findViewById(R.id.location_input)).getText().toString();
                String description = ((TextView) dialogView.findViewById(R.id.description_input)).getText().toString();



                calendarModel.addEventAuto(cr, title, 0L, 0L, location, description, calID);
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onEventLongPress(final WeekViewEvent weekViewEvent, RectF rectF) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.event_selected_dialog, null);

        TextView eventNameLabel = (TextView) dialogView.findViewById(R.id.event_name_label);
        if(eventNameLabel!= null){
            eventNameLabel.setText("Remove " + weekViewEvent.getName() + "?");
        }

        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //TODO: Create a remove method
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }


   @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
       if(!hasOnMonthChange){
           hasOnMonthChange = true;
           readEvents();
           return new ArrayList<WeekViewEvent>(eventMap.values());
       }

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

        Cursor cur = calendarModel.getEventsCursor(cr, 0L, 0L);

        while (cur.moveToNext()) {
            long id = cur.getLong(CalendarUtils.PROJECTION_ID_INDEX);


            if(!eventMap.containsKey(id)) {
                String eventName = cur.getString(CalendarUtils.PROJECTION_TITLE_INDEX);

                Calendar startTime = Calendar.getInstance();
                startTime.setTimeInMillis(cur.getLong(CalendarUtils.PROJECTION_BEGIN_INDEX));

                Calendar endTime = Calendar.getInstance();
                endTime.setTimeInMillis(cur.getLong(CalendarUtils.PROJECTION_END_INDEX));

                WeekViewEvent event = new WeekViewEvent(id, eventName, startTime, endTime);
                event.setColor(getResources().getColor(R.color.pink));

                eventMap.put(id, event);

            }
        }
        cur.close();

    }

    public void setHasOnMonthChange(boolean b) {
        hasOnMonthChange = b;
    }




}
