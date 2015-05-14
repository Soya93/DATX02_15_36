package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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

        if(hasSynced && ! eventsList.isEmpty()) {
            syncText.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }



    private void initComponents(View view) {
        hasInit = true;

        View.OnClickListener myButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getTag() == homeFAB.getTag()) {
                    calendarFrag.changeVisibleCalendars();
                    onRefresh();
                }
            }
        };

        homeFAB = (FloatingActionButton) view.findViewById(R.id.home_fab);
        homeFAB.setTag(1);
        homeFAB.setOnClickListener(myButtonHandler);
        homeFAB.setType(FloatingActionButton.TYPE_NORMAL);
        homeFAB.setBackgroundColor(Color.parseColor(Constants.primaryColor));

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
                syncText.setVisibility(View.INVISIBLE);
                hasSynced = true;
                swipeLayout.setRefreshing(false);
                setTodaysEvents();
            }
        }, 5000);
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

        if(eventsList.isEmpty()) {
            syncText.setText("Du har inga planerade händesler idag!");
        }else {
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