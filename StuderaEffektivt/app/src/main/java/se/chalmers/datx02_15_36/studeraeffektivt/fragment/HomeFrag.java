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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.adapter.HomeAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.HomeEventItem;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;

public class HomeFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private Context context;
    private CalendarFrag calendarFrag;
    private boolean hasInit = false;
    private FloatingActionButton homeFAB;
    private ContentResolver cr;
    private ListView listView;
    private ArrayList<HomeEventItem> eventsList;
    private SwipeRefreshLayout swipeLayout;
    private TextView syncText;
    private boolean hasSynced;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_home, container, false);

        initComponents(rootView);
        eventsList = getEvents();

        if (hasSynced && !eventsList.isEmpty()) {
            syncText.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }


    private void initComponents(View view) {
        hasInit = true;

        View.OnClickListener myButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getTag() == homeFAB.getTag()) {
                    Log.i("homefrag", "button");
                    calendarFrag.changeVisibleCalendars();
                    updateView();
                }
            }
        };

        homeFAB = (FloatingActionButton) view.findViewById(R.id.home_fab);
        homeFAB.setTag(1);
        homeFAB.setOnClickListener(myButtonHandler);
        homeFAB.setType(FloatingActionButton.TYPE_NORMAL);
        homeFAB.setBackgroundColor(Color.parseColor("#ffffff"));

        Drawable calendarIcon = getResources().getDrawable(R.drawable.ic_cal2).mutate();
        calendarIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP);
        homeFAB.setImageDrawable(calendarIcon);
        //homeFAB.setBackgroundColor(Color.parseColor(Constants.primaryColor));

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light);

        syncText = (TextView) view.findViewById(R.id.synchronize_lable);

    }


    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateView();
                swipeLayout.setRefreshing(false);

            }
        }, 5000);
    }

    public void updateView() {
        if (!eventsList.isEmpty()) {
            syncText.setVisibility(View.INVISIBLE);
        }
        setTodaysEvents();
        Log.i("homefrag", "updateview");

    }

    public void setContentResolver(ContentResolver cr) {
        this.cr = cr;
    }

    public void setCalendarFrag(CalendarFrag calendarFrag) {
        this.calendarFrag = calendarFrag;
    }

    private ArrayList<HomeEventItem> getEvents() {
        return calendarFrag.getCalendarModel().readEventsToday(cr);
    }

    public void setTodaysEvents() {
        hasSynced = true;

        ArrayList<HomeEventItem> visibleEventList = new ArrayList<>();
        eventsList = getEvents();
        List<Long> visibleCalendars = calendarFrag.getVisibleCalendars();

        if (eventsList.isEmpty()) {
            syncText.setText("Inga planerade händelser idag!");
        } else {
            syncText.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < eventsList.size(); i++) {
            if (visibleCalendars.contains(eventsList.get(i).getCalId())) {
                visibleEventList.add(eventsList.get(i));
            }
        }

        HomeAdapter adapter = new HomeAdapter(context, visibleEventList);

        listView = (ListView) rootView.findViewById(R.id.home_list);
        homeFAB.attachToListView(listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeEventItem hei = (HomeEventItem) parent.getAdapter().getItem(position);
                openViewEventInfo(hei.getId(), hei.getStartTime(), hei.getEndTime());
            }
        });
    }

    public void openViewEventInfo(long eventId, long startTime, long endTime) {

        //Get a cursor for the detailed information of the event
        Cursor cur = calendarFrag.getCalendarModel().getEventDetailedInfo(cr, startTime, endTime, eventId);

        //Fetch information from the cursor
        String title = cur.getString(CalendarUtils.TITLE);
        String location = cur.getString(CalendarUtils.LOCATION);
        String description = cur.getString(CalendarUtils.DESCRIPTION);
        String calendar = cur.getString(CalendarUtils.CALENDAR_NAME);
        long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
        int allDay = cur.getInt(CalendarUtils.ALL_DAY);
        final int color = cur.getInt(CalendarUtils.EVENT_COLOR) == 0 ? cur.getInt(CalendarUtils.CALENDAR_COLOR) : cur.getInt(CalendarUtils.EVENT_COLOR);
        cur.close();
        final int notification = calendarFrag.getCalendarModel().getNotificationTime(cr, startTime, endTime, eventId);

        calendarFrag.openViewEventInfo(eventId, title, startTime, endTime, location, description, calendar, calID, notification, allDay, color);
    }

    public void setContext(Context c) {
        this.context = c;
    }
}