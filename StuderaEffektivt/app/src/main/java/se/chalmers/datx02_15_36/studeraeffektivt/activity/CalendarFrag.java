package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.view.CalendarView;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarFrag extends Fragment implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    public CalendarModel getCalendarModel() {
        return calendarModel;
    }

    private CalendarModel calendarModel = new CalendarModel();
    private CalendarView calendarView = new CalendarView();
    private EventActivity eventActivity;
    public ContentResolver cr;
    private View view;
    private WeekView mWeekView;

    private List<WeekViewEvent> eventList;
    boolean hasOnMonthChange;
    private SubActionButton button1;
    private SubActionButton button2;
    private SubActionButton button3;
    private View.OnClickListener fabHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarModel = new CalendarModel();
        this.initComponents();
        return view;
    }

    private void initComponents() {

        button1 = MainActivity.button1;
        button2 = MainActivity.button2;
        button3 = MainActivity.button3;

        View.OnClickListener myButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                openAddEvent();
            }
        };

        Log.i("calendar: ", " button1 id:  " + button1.getId() + " button2 id : " + button2.getId()
                + " button3 id: " + button3.getId());

        fabHandler = new View.OnClickListener() {

            public void onClick(View v) {
                if (v.getTag() == button1.getTag()) {
                    openAddEvent();
                    Log.i("main:", "1 studiepass");
                }else if (v.getTag() == button2.getTag()) {
                    addRepetitionSession();
                    Log.i("main:", " 2 repetition");
                } else  {
                    //Instälningar
                    Log.i("main:", "inställningar");
                }
            }
        };


        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) view.findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // MONTH every time the MONTH changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set number of visible days in the calendar view
        mWeekView.setNumberOfVisibleDays(5);
        hasOnMonthChange = false;
        mWeekView.goToHour(8.0);


        //actionButton.setOnClickListener(myButtonHandler);
        button1.setOnClickListener(fabHandler);
        button2.setOnClickListener(fabHandler);
        button3.setOnClickListener(fabHandler);

    }

    @Override
    public void onResume() {
        super.onResume();
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }


    //Reads all todays events, used in homescreen
    public List<String> getTodaysEvents() {
        return calendarModel.readEventsToday(cr);
    }

    @Override
    public void onEventClick(final WeekViewEvent weekViewEvent, RectF rectF) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.event_selected_dialog, null);

        final String title = weekViewEvent.getName();

        //Get a cursor for the detailed information of the event
        final long startTime = weekViewEvent.getStartTime().getTimeInMillis();
        final long endTime = weekViewEvent.getEndTime().getTimeInMillis();
        Cursor cur = calendarModel.getEventDetailedInfo(cr, startTime, endTime, weekViewEvent.getId());

        //Fetch information from the cursor
        final String location = cur.getString(CalendarUtils.EVENT_INFO_LOCATION);
        final String description = cur.getString(CalendarUtils.EVENT_INFO_DESCRIPTION);
        String calendar = cur.getString(CalendarUtils.EVENT_INFO_CALENDAR);
        final int calID = cur.getInt(CalendarUtils.CALENDAR_ID);
        cur.close();

        calendarView.updateEventInfoView(dialogView, title, startTime, endTime, location, description, calendar);
        builder.setView(dialogView);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setNegativeButton("Redigera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                openEditEvent(weekViewEvent.getId(), startTime, endTime, title, location, description, calID);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Opens an dialog when pressing the buttom for adding a new event
    public void openAddEvent() {
        eventActivity = new EventActivity();
        eventActivity.setCalendarFrag(this);
        Intent intent = new Intent(getActivity(), eventActivity.getClass());
        intent.putExtra("isInAddMode", true);
        startActivity(intent);
    }

    //Opens an dialog when pressing the buttom for adding a new event

    private void openEditEvent(long eventID, long startTime, long endTime, String title, String location, String description, int calID) {

        //Get all neccesary information about the event

        //send it further to the event activity

        eventActivity = new EventActivity();
        eventActivity.setCalendarFrag(this);
        Intent intent = new Intent(getActivity(), eventActivity.getClass());
        intent.putExtra("isInAddMode", false);
        intent.putExtra("eventID", eventID);
        intent.putExtra("startTime", startTime);
        intent.putExtra("endTime", endTime);
        intent.putExtra("title", title);
        intent.putExtra("location", location);
        intent.putExtra("description", description);
        intent.putExtra("calID", calID);
        startActivity(intent);
    }

    public void editEvent(ContentResolver cr, String title, long startMillis, long endMillis, String location, String description, long eventID, long calID, int notification) {
        calendarModel.editEventAuto(cr, title, startMillis, endMillis, location, description, calID, eventID, notification);
    }

    //Adds an event to the calendar with the specified inputs
    public void addEvent(String title, String location, String description, long startMillis, long endMillis, ContentResolver cr, long calID, int notification) {
        long id = calendarModel.addEventAuto(cr, title, startMillis, endMillis, location, description, calID);
        calendarModel.addNotification(cr, id, notification);
    }


    @Override
    public void onEventLongPress(final WeekViewEvent weekViewEvent, RectF rectF) {
        final String eventName = weekViewEvent.getName();
        AlertDialog.Builder builder = calendarView.updateDeleteInfoView(eventName, getActivity());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteEvent(weekViewEvent.getId(), eventName);
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

    //Removes an event from the calendar
    private void deleteEvent(long id, CharSequence event) {
        calendarModel.deleteEvent(cr, id);

        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();

        Context context = getActivity().getApplicationContext();
        CharSequence text = "Händelsen " + event + " har tagits bort";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    //Updates the view of the calendar by redrawing the events
    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        eventList = new ArrayList<>();
        if (!hasOnMonthChange) {
            hasOnMonthChange = true;
            readEvents();
        }
        return eventList;
    }

    //Reads the events on the calendar from today until tomorrow
    private void readEvents() {
        Cursor cur = calendarModel.getEventsCursor(cr, CalendarUtils.TODAY_IN_MILLIS, 0L);

        while (cur.moveToNext()) {
            long id = cur.getLong(CalendarUtils.PROJECTION_ID_INDEX);

            String eventName = cur.getString(CalendarUtils.PROJECTION_TITLE_INDEX);

            Calendar startTime = Calendar.getInstance();
            startTime.setTimeInMillis(cur.getLong(CalendarUtils.PROJECTION_BEGIN_INDEX));


            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(cur.getLong(CalendarUtils.PROJECTION_END_INDEX));

            int color = cur.getInt(CalendarUtils.PROJECTION_COLOR_INDEX);

            WeekViewEvent event = new WeekViewEvent(id, eventName, startTime, endTime);
            event.setColor(color);

            eventList.add(event);
        }
        cur.close();
    }

    public void setContentResolver(ContentResolver cr) {
        this.cr = cr;
    }

    public void addRepetitionSession() {
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
}
