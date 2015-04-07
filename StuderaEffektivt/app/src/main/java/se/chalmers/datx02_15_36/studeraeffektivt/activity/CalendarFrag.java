package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.model.HomeEventItem;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.view.CalendarView;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarFrag extends Fragment implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    public ContentResolver cr;
    boolean hasOnMonthChange;
    private CalendarModel calendarModel = new CalendarModel();
    private CalendarView calendarView = new CalendarView();
    private EventActivity eventActivity;
    private View view;
    private WeekView mWeekView;

    private List<WeekViewEvent> eventList;
    private List<Long> visibleCalendars;
    private SubActionButton button1;
    private SubActionButton button2;
    private SubActionButton button3;
    private SubActionButton button4;
    private Button backButton;
    private Button forwradButton;
    private Button goToTodayButton;
    int numberOfVisibleDays;
    private View.OnClickListener fabHandler;

    public CalendarModel getCalendarModel() {
        return calendarModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarModel = new CalendarModel();


        numberOfVisibleDays = 5;

        visibleCalendars = calendarModel.getCalendarIDsInstances(cr);

        this.initComponents();
        return view;
    }

    private void initComponents() {

        button1 = MainActivity.button1;
        button2 = MainActivity.button2;
        button3 = MainActivity.button3;
        button4 = MainActivity.button4;

        backButton = (Button) view.findViewById(R.id.cal_back_button);
        forwradButton = (Button) view.findViewById(R.id.cal_forward_button);
        goToTodayButton = (Button) view.findViewById(R.id.go_to_today_button);

        View.OnClickListener myButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == backButton.getId()) {
                    onBackClick();
                }else if (v.getId() == forwradButton.getId()) {
                    onForwardCLick();
                } else {
                    onTodayClick();
                }

            }
        };

        backButton.setOnClickListener(myButtonHandler);
        forwradButton.setOnClickListener(myButtonHandler);
        goToTodayButton.setOnClickListener(myButtonHandler);




        fabHandler = new View.OnClickListener() {

            public void onClick(View v) {
                if (v.getTag() == button1.getTag()) {
                    openAddEvent();
                }else if (v.getTag() == button2.getTag()) {
                    addRepetitionSession();
                }else if (v.getTag() == button3.getTag()) {
                    //Instälningar - antal dagar
                    changeNbrOfDaysDialog();
                } else  {
                    //Instälningar - vilka kalendrar
                    changeVisibleCalendars();
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


        // set the time for which hour that are shown
        mWeekView.goToHour(CalendarUtils.HOUR -1);

        //Disable horizontal scroll in calendar view
        mWeekView.setHorizontalScrollBarEnabled(false); // doesn't work... :(


        //actionButton.setOnClickListener(myButtonHandler);
        button1.setOnClickListener(fabHandler);
        button2.setOnClickListener(fabHandler);
        button3.setOnClickListener(fabHandler);
        button4.setOnClickListener(fabHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }

    //TODO remove this??
    //Reads all todays events, used in homescreen
    public ArrayList<HomeEventItem> getTodaysEvents() {
        return calendarModel.readEventsToday(cr);
    }

    @Override
    public void onEventClick(final WeekViewEvent weekViewEvent, RectF rectF) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.event_selected_dialog, null);

        final String title = weekViewEvent.getName();
        final long eventId = weekViewEvent.getId();

        //Get a cursor for the detailed information of the event
        final long startTime = weekViewEvent.getStartTime().getTimeInMillis();
        final long endTime = weekViewEvent.getEndTime().getTimeInMillis();
        Cursor cur = calendarModel.getEventDetailedInfo(cr, startTime, endTime, weekViewEvent.getId());

        //Fetch information from the cursor
        final String location = cur.getString(CalendarUtils.LOCATION);
        final String description = cur.getString(CalendarUtils.DESCRIPTION);
        final String calendar = cur.getString(CalendarUtils.CALENDAR_NAME);
        final long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
        final int allDay = cur.getInt(CalendarUtils.ALL_DAY);
        cur.close();

        final int notification = calendarModel.getNotificationTime(cr, startTime,endTime,eventId);

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
                openEditEvent(eventId, startTime, endTime, title, location, description, calID, calendar, notification, allDay);
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
        intent.putExtra("calID", 1);        // 1 är hem kalender
        String name = calendarModel.getCalendarNamesInstances(cr).get(0);        //hemkalenderns namn finns på position 0
        intent.putExtra("calName", name);
        startActivity(intent);
    }

    //Opens an dialog when pressing the buttom for adding a new event
    private void openEditEvent(long eventID, long startTime, long endTime, String title, String location, String description, Long calID, String calName, int notification, int isAllDay) {
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
        intent.putExtra("calName", calName);
        intent.putExtra("notification", notification);
        intent.putExtra("isAllDay", isAllDay);
        startActivity(intent);
    }

    private void openAddRepetition(int studyWeek) {
        eventActivity = new EventActivity();
        eventActivity.setCalendarFrag(this);
        Intent intent = new Intent(getActivity(), eventActivity.getClass());
        intent.putExtra("isInAddMode", true);
        intent.putExtra("startTime", 0L);
        intent.putExtra("endTime", 0l);
        intent.putExtra("title", "Repititonspass för LV" + studyWeek);
        intent.putExtra("calID", 1);        // 1 är hem kalender
        String name = calendarModel.getCalendarNamesInstances(cr).get(0);        //hemkalenderns namn finns på position 0
        intent.putExtra("calName", name);


        startActivity(intent);
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

        // specifies the intervall for which events that are read
        int year = CalendarUtils.YEAR;
        int month = CalendarUtils.MONTH;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, 1);
        long startDay = cal.getTimeInMillis();
        int daysInMonth =cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(year, month, daysInMonth);
        long endDay = cal.getTimeInMillis();

        Cursor cur = calendarModel.getEventsCursor(cr, startDay, endDay);


        while (cur.moveToNext()) {
            long calID = cur.getLong(CalendarUtils.CALENDAR_ID);


            //Checks so the events belongs to an calendar that should be visible
           if(visibleCalendars.contains(calID)) {

                long id = cur.getLong(CalendarUtils.EVENT_ID);

                String eventName = cur.getString(CalendarUtils.TITLE);
                Calendar startTime = Calendar.getInstance();
                startTime.setTimeInMillis(cur.getLong(CalendarUtils.EVENT_BEGIN));


                Calendar endTime = Calendar.getInstance();
                endTime.setTimeInMillis(cur.getLong(CalendarUtils.EVENT_END));


              if(cur.getInt(CalendarUtils.ALL_DAY) == 1){
                   startTime.set(Calendar.HOUR_OF_DAY, 0);
                   startTime.set(Calendar.MINUTE, 0);
                   endTime.set(Calendar.HOUR_OF_DAY, 23);
                   endTime.set(Calendar.MINUTE, 59);
               }

               int color = cur.getInt(CalendarUtils.EVENT_COLOR);
                if(color == 0) {
                    color = cur.getInt(CalendarUtils.CALENDAR_COLOR);
                }

                WeekViewEvent event = new WeekViewEvent(id, eventName, startTime, endTime);
                event.setColor(color);



                eventList.add(event);
            }

        }
        cur.close();
    }

    public void setContentResolver(ContentResolver cr) {
        this.cr = cr;
    }

    public void addRepetitionSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //the notificationAlternatives
        String[] alternatives = {"LV1", "LV2", "LV3", "LV4", "LV5", "LV6", "LV7", "LV8"};
        builder.setTitle("Välj ett pass att repetera")
                .setItems(alternatives, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO hämta några random uppgifter
                        String tasks = "";
                        int studyWeek = which + 1;
                        openAddRepetition(studyWeek);


                        //calendarModel.addEventAuto(cr, "Repititonspass för LV" + studyWeek, 0L, 0L,null)
                        //startActivity(calendarModel.addEventManually(0L, 0L, true, "Repititonspass för LV" + studyWeek, null, "Repetera " + tasks));

                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void changeVisibleCalendars() {
        final List<String> calNames = getCalendarModel().getCalendarNamesInstances(cr);
        final List<Long> calIDs = getCalendarModel().getCalendarIDsInstances(cr);
        final String[] calendars = calNames.toArray(new String[calNames.size()]);
        final boolean [] checkedArray = new boolean [calIDs.size()];

        int i = 0;
        for(long id: calIDs){
            checkedArray[i] = (visibleCalendars.contains(id));
            i ++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Välj kalender")
                .setMultiChoiceItems(calendars, checkedArray,  new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            if(!visibleCalendars.contains(which)) {
                                visibleCalendars.add(calIDs.get(which));
                                //visibleCalendars.add(which, calIDs.get(which));
                            }
                        } else {
                            visibleCalendars.remove(which);
                            //visibleCalendars.set(which, null);
                        }
                    }
                });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hasOnMonthChange = false;
                mWeekView.notifyDatasetChanged();
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void changeNbrOfDaysDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String [] choices = {"1", "3", "5"};
        numberOfVisibleDays = mWeekView.getNumberOfVisibleDays();
        int index = getIndexOfVisibleDays();

       builder.setTitle("Antalet dagar i vyn");
       builder.setSingleChoiceItems(choices, index, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int selected) {

                   numberOfVisibleDays = Integer.parseInt((choices[selected]));
           }
       });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mWeekView.setNumberOfVisibleDays(numberOfVisibleDays);
                mWeekView.goToHour(CalendarUtils.HOUR > 16? 16: CalendarUtils.HOUR);
                hasOnMonthChange = false;
                mWeekView.notifyDatasetChanged();
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int getIndexOfVisibleDays(){
        int i = numberOfVisibleDays == 3? numberOfVisibleDays-2: numberOfVisibleDays-3;
        return numberOfVisibleDays == 1? numberOfVisibleDays-1: i;
    }



    private void onForwardCLick() {
        Calendar newDate = mWeekView.getFirstVisibleDay();
        if (numberOfVisibleDays == 1) {
            newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) + 1);
        } else {
            newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) + numberOfVisibleDays - 1);
        }

        mWeekView.goToDate(newDate);
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }

    private void onBackClick() {
        Calendar newDate = mWeekView.getFirstVisibleDay();
        if (numberOfVisibleDays == 1) {
            newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) -1);
        } else {
            newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) - numberOfVisibleDays + 1);
        }

        mWeekView.goToDate(newDate);
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }

    private void onTodayClick() {
        mWeekView.goToToday();
        mWeekView.goToHour(CalendarUtils.HOUR-1);
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }

}
