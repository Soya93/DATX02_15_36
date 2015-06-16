/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.EventActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.MainActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.RepetitionActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.adapter.CalendarsFilterAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarsFilterItem;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;
import se.chalmers.datx02_15_36.studeraeffektivt.view.CalendarView;

;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarFrag extends Fragment implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener, WeekView.EmptyViewClickListener {

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
    private Button forwardButton;
    private TextView goToTodayButton;
    int numberOfVisibleDays;
    private View.OnClickListener fabHandler;
    private AlertDialog alertDialog;
    private ArrayList<CalendarsFilterItem> calendarsList;
    private CalendarsFilterAdapter ad;
    private SharedPreferences sharedPref;
    private boolean hasInit;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarModel = new CalendarModel();


        numberOfVisibleDays = 5;

        Map<Long, String> cals = calendarModel.getCalendarInfo(cr);
        visibleCalendars = new LinkedList<>(cals.keySet());

        //Shared preferences stuff
        //sharedPref = getActivity().getSharedPreferences("calendarFilter", Context.MODE_PRIVATE);
        //Set<String> visibleCalSet = sharedPref.getStringSet("visibleCalendars", null);

        calendarsList = calendarModel.getCalendarFilters();

       /* if(visibleCalSet!=null) {
            //Log.i("CalendarFrag", "gettingSharedPrefAdding");
            for (String id : visibleCalSet) {
                visibleCalendars.add(Long.parseLong(id));
                //Log.i("CalendarFrag", "List_adding" + " string:" + id + " long:" + Long.parseLong(id));
            }
        }*/

        //Log.i("CalendarFrag","gettingSharedPrefAdding");

        hasInit = true;

        this.initComponents();
        return view;
    }

    private void initComponents() {

        button1 = MainActivity.button1;
        button2 = MainActivity.button2;
        button3 = MainActivity.button3;
        button4 = MainActivity.button4;

        backButton = (Button) view.findViewById(R.id.cal_back_button);
        Drawable backDrawable = getResources().getDrawable(R.drawable.ic_navigation_chevron_left).mutate();
        backDrawable.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        backButton.setBackground(backDrawable);

        forwardButton = (Button) view.findViewById(R.id.cal_forward_button);
        Drawable forwardDrawable = getResources().getDrawable(R.drawable.ic_navigation_chevron_right).mutate();
        forwardDrawable.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        forwardButton.setBackground(forwardDrawable);

        goToTodayButton = (TextView) view.findViewById(R.id.go_to_today_button);
        SimpleDateFormat formatter = new SimpleDateFormat(" d MMMM");
        goToTodayButton.setText(formatter.format(new Date()));

        //Drawable todayDrawable = getResources().getDrawable(R.drawable.ic_idag).mutate();
        //todayDrawable.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        //goToTodayButton.setBackground(todayDrawable);

        View.OnClickListener myButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == backButton.getId()) {
                    onBackClick();
                } else if (v.getId() == forwardButton.getId()) {
                    onForwardCLick();
                } else {
                    onTodayClick();
                }

            }
        };

        backButton.setOnClickListener(myButtonHandler);
        forwardButton.setOnClickListener(myButtonHandler);
        goToTodayButton.setOnClickListener(myButtonHandler);


        fabHandler = new View.OnClickListener() {

            public void onClick(View v) {
                updateView();

                if (v.getTag() == button1.getTag()) {
                    openAddEvent();
                } else if (v.getTag() == button2.getTag()) {
                    addRepetitionSession();
                } else if (v.getTag() == button3.getTag()) {
                    //Instälningar - antal dagar
                    changeNbrOfDaysDialog();
                } else {
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


        mWeekView.setEmptyViewClickListener(this);

        // Set number of visible days in the calendar view

        mWeekView.setNumberOfVisibleDays(5);
        hasOnMonthChange = false;


        // set the time for which hour that are shown
        mWeekView.goToHour(CalendarUtils.HOUR == 0 ? CalendarUtils.HOUR : CalendarUtils.HOUR - 1);

        //Disable horizontal scroll in calendar view
        mWeekView.setHorizontalScrollBarEnabled(false); // doesn't work... :(


        // Lets change some dimensions to best fit the view.
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventPadding(20);
        mWeekView.setHourSeparatorColor(20);
        mWeekView.setEventMarginVertical(20);
        mWeekView.setBackgroundColor(Color.WHITE);
        mWeekView.setTodayHeaderTextColor(Color.MAGENTA);
        mWeekView.setTodayBackgroundColor(Color.parseColor(Constants.secondaryColor));


        //actionButton.setOnClickListener(myButtonHandler);
        button1.setOnClickListener(fabHandler);
        button2.setOnClickListener(fabHandler);
        button3.setOnClickListener(fabHandler);
        button4.setOnClickListener(fabHandler);

        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onEventClick(final WeekViewEvent weekViewEvent, RectF rectF) {
        final String title = weekViewEvent.getName();
        final long eventId = weekViewEvent.getId();

        //Get a cursor for the detailed information of the event
        final long startTime = weekViewEvent.getStartTime().getTimeInMillis();
        long eendTime = weekViewEvent.getEndTime().getTimeInMillis();


        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(eendTime);

        if(endCal.get(Calendar.HOUR_OF_DAY) == 0 && endCal.get(Calendar.MINUTE) == 59){
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            eendTime = endCal.getTimeInMillis();
        }
        final long endTime = eendTime;

        Cursor cur = calendarModel.getEventDetailedInfo(cr, startTime, endTime, weekViewEvent.getId());

        //Fetch information from the cursor
        final String location = cur.getString(CalendarUtils.LOCATION);
        final String description = cur.getString(CalendarUtils.DESCRIPTION);
        final String calendar = cur.getString(CalendarUtils.CALENDAR_NAME);
        final long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
        final int allDay = cur.getInt(CalendarUtils.ALL_DAY);
        final int color = cur.getInt(CalendarUtils.EVENT_COLOR) == 0 ? cur.getInt(CalendarUtils.CALENDAR_COLOR) : cur.getInt(CalendarUtils.EVENT_COLOR);
        cur.close();

        final int notification = calendarModel.getNotificationTime(cr, startTime, endTime, eventId);

        openViewEventInfo(eventId, title, startTime, endTime, location, description, calendar, calID, notification, allDay, color);
    }

    public void openViewEventInfo(final long eventId, final String eventTitle, final long startTime, final long endTime, final String location, final String description, final String calendar, final long calID, final int notification, final int allDay, final int color) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.event_selected_dialog, null);

        calendarView.updateEventInfoView(dialogView, eventTitle, startTime, endTime, location, description, calendar, allDay, notification);
        builder.setView(dialogView);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setNegativeButton("Redigera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                openEditEvent(eventId, startTime, endTime, eventTitle, location, description, calID, calendar, notification, allDay, color);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Constants.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Constants.primaryDarkColor));
    }

    //Opens an dialog when pressing the buttom for adding a new event
    public void openAddEvent() {
        eventActivity = new EventActivity();
        eventActivity.setCalendarFrag(this);
        Intent intent = new Intent(getActivity(), eventActivity.getClass());
        intent.putExtra("isInAddMode", true);
        sharedPref = getActivity().getSharedPreferences("calendarPref", Context.MODE_PRIVATE);
        Long homeCalID = sharedPref.getLong("homeCalID", 1L); // 1 is some value if it fails to read??
        intent.putExtra("calID", homeCalID);        // is the home calendar
        intent.putExtra("calName", calendarModel.getCalendarsMap().get(homeCalID)); //get name of the home calendar
        intent.putExtra("color", calendarModel.getCalIdAndColorMap().get(homeCalID));  //get the color of the home calendar
        startActivity(intent);
    }

    //Opens an dialog when pressing the buttom for adding a new event
    private void openEditEvent(long eventID, long startTime, long endTime, String title, String location, String description, Long calID, String calName, int notification, int isAllDay, int color) {
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
        intent.putExtra("color", color);
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

        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Constants.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Constants.primaryDarkColor));
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
        cal.set(year - 1, month, 1);
        long startDay = cal.getTimeInMillis();
        cal.set(year + 1, month +1, 1);
        long endDay = cal.getTimeInMillis();

        Cursor cur = calendarModel.getEventsCursor(cr, startDay, endDay);


        while (cur.moveToNext()) {
            long calID = cur.getLong(CalendarUtils.CALENDAR_ID);


            //Checks so the events belongs to an calendar that should be visible
            if (visibleCalendars.contains(calID)) {

                long id = cur.getLong(CalendarUtils.EVENT_ID);

                String eventName = cur.getString(CalendarUtils.TITLE);
                Calendar startTime = Calendar.getInstance();
                startTime.setTimeInMillis(cur.getLong(CalendarUtils.EVENT_BEGIN));


                Calendar endTime = Calendar.getInstance();
                endTime.setTimeInMillis(cur.getLong(CalendarUtils.EVENT_END));


                int color = cur.getInt(CalendarUtils.EVENT_COLOR);
                if (color == 0) {
                    color = cur.getInt(CalendarUtils.CALENDAR_COLOR);
                }

                WeekViewEvent event = new WeekViewEvent(id, eventName, startTime, endTime);
                event.setColor(color);

                if (cur.getInt(CalendarUtils.ALL_DAY) == 1) {
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    endTime.setTimeInMillis(cur.getLong(CalendarUtils.EVENT_BEGIN));
                    endTime.set(Calendar.HOUR_OF_DAY,0);
                    endTime.set(Calendar.MINUTE, 59);
                }
                eventList.add(event);
            }

        }
        cur.close();
    }

    public void setContentResolver(ContentResolver cr) {
        this.cr = cr;
    }

    public void addRepetitionSession() {
        Intent i = new Intent(getActivity(), RepetitionActivity.class);
        startActivity(i);
    }

    public void changeVisibleCalendars() {
        final List<Long> calIDs = new LinkedList<>(calendarModel.getCalendarInfo(cr).keySet());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = LayoutInflater.from(getActivity());
        View view = li.inflate(R.layout.calendarsfilterlistview, null);

        final ListView listView = (ListView) view.findViewById(R.id.calendar_listview);
        ad = new CalendarsFilterAdapter(getActivity().getApplicationContext(), R.layout.calendars_filter_item, R.id.calendar_text, calendarsList);
        listView.setAdapter(ad);
        listView.setDivider(null);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for (int i = 0; i < calendarsList.size(); i++) {
                    if (calendarsList.get(i).isChecked()) {
                        if (!visibleCalendars.contains(calIDs.get(i))) {
                            visibleCalendars.add(calIDs.get(i));
                        }
                        
                    } else {
                        visibleCalendars.remove(calIDs.get(i));
                        //Log.i("CalendarFrag", "removed " + calIDs.get(i));
                    }
                }
                //Log.i("CalendarFrag", "updatedfilter");
                //updateFilterSharedPreferences();
                hasOnMonthChange = false;
                mWeekView.notifyDatasetChanged();
                alertDialog.dismiss();

            }
        });

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

        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Constants.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Constants.primaryDarkColor));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ad.getItemsArrayList().get(position).setChecked(!ad.getItemsArrayList().get(position).isChecked());
                ad.notifyDataSetChanged();
            }
        });

    }

    public void changeNbrOfDaysDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] choices = {"1", "3", "5"};
        final int previousNumberOfVisibleDays = numberOfVisibleDays;
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
                Calendar oldDate = mWeekView.getFirstVisibleDay();

                Calendar todayDate = Calendar.getInstance();
                todayDate.setTimeInMillis(CalendarUtils.TODAY_IN_MILLIS);


                mWeekView.setNumberOfVisibleDays(numberOfVisibleDays);
                Calendar newDate = mWeekView.getFirstVisibleDay();

                //Log.i("prevNumOfVisDays", previousNumberOfVisibleDays + "");
                //Log.i("NumOfVisDays", numberOfVisibleDays + "");


              /*  if(previousNumberOfVisibleDays == 1 && numberOfVisibleDays == 3) {
                    Log.i("NewDate", newDate.get(Calendar.DAY_OF_MONTH) + "");

                    newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) - (2*(oldDate.get(Calendar.DAY_OF_MONTH) - todayDate.get(Calendar.DAY_OF_MONTH))));

                    Log.i("OldDate", oldDate.get(Calendar.DAY_OF_MONTH) + "");

                    Log.i("todaydate", todayDate.get(Calendar.DAY_OF_MONTH) + "");


                    Log.i("NewNewDate", newDate.get(Calendar.DAY_OF_MONTH) + "");


                } else if(previousNumberOfVisibleDays == 1 && numberOfVisibleDays == 5) {
                    newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) - (2*2*(oldDate.get(Calendar.DAY_OF_MONTH) - todayDate.get(Calendar.DAY_OF_MONTH))));
                }

                if(previousNumberOfVisibleDays == 3 && numberOfVisibleDays == 1){

                }*/

                //Log.i("NewDate", newDate.get(Calendar.DAY_OF_MONTH) + "");

                mWeekView.goToDate(newDate);


                //Log.i("NewDate mWeekView", mWeekView.getFirstVisibleDay().get(Calendar.DAY_OF_MONTH) + "" );


                mWeekView.goToHour(CalendarUtils.HOUR > 16 ? 16 : CalendarUtils.HOUR - 1);
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

        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Constants.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Constants.primaryDarkColor));
    }

    private int getIndexOfVisibleDays() {
        int i = numberOfVisibleDays == 3 ? numberOfVisibleDays - 2 : numberOfVisibleDays - 3;
        return numberOfVisibleDays == 1 ? numberOfVisibleDays - 1 : i;
    }


    private void onForwardCLick() {
        Calendar newDate = mWeekView.getFirstVisibleDay();
        if (numberOfVisibleDays == 1) {
            newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) + 1);
        } else {
            newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) + numberOfVisibleDays);
        }

        mWeekView.goToDate(newDate);
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }

    private void onBackClick() {
        Calendar newDate = mWeekView.getFirstVisibleDay();
        if (numberOfVisibleDays == 1) {
            newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) - 1);
        } else {
            newDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH) - numberOfVisibleDays);
        }

        mWeekView.goToDate(newDate);
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }

    private void onTodayClick() {
        mWeekView.goToToday();
        if(CalendarUtils.HOUR != 0) {
            mWeekView.goToHour(CalendarUtils.HOUR > 16 ? 16 : CalendarUtils.HOUR - 1);
        }
        hasOnMonthChange = false;
        mWeekView.notifyDatasetChanged();
    }

    public List<Long> getVisibleCalendars() {
        return visibleCalendars;
    }

    public CalendarModel getCalendarModel() {
        return calendarModel;
    }

    public void updateView() {
        if(MainActivity.actionMenu.isOpen()) {
            MainActivity.actionMenu.toggle(false);
        }
    }

    public boolean hasInit(){
        return hasInit;
    }


    //Dunno why it does not work..it saves it correctly in the model but the objects are still drawn in the calendar :(
    private void updateFilterSharedPreferences(){
        //Log.i("CalendarFrag", "updateSP");
        sharedPref = getActivity().getSharedPreferences("calendarFilter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Set<String> visibleCalSet = new HashSet<>();

        //Log.i("CalendarFrag", "visiblecalendars= " + visibleCalendars.equals(null));

        for(Long id: visibleCalendars){
            visibleCalSet.add(id.toString());
            //Log.i("CalendarFrag", "SP_adding" + " long:" + id + " string:"+ id.toString());
        }
        editor.putStringSet("visibleCalendars",visibleCalSet);
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.i("CalendarFrag", "pause");
        //updateFilterSharedPreferences();
    }

    @Override
    public void onEmptyViewClicked(Calendar calendar) {
        Log.i("Empty time", "time: " + calendar.getTime());
    }

}
