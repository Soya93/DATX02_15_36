package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.model.HomeAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.HomeEventItem;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;


public class HomeFrag extends Fragment {


    private List<String> events;
    private View rootView;
    private Context context;
    private CalendarFrag calendarFrag;
    private boolean hasInit = false;
    private FloatingActionButton homeFAB;
    private List<String> todaysEventsTitles;
    private CalendarModel calModel;
    private ContentResolver cr;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_home, container, false);

        initComponents(rootView);
        calModel = new CalendarModel();
        return rootView;
    }

    private void initComponents(View view) {
        events = new ArrayList<String>();
        hasInit = true;


        View.OnClickListener myButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getTag() == homeFAB.getTag()) {
                    calendarFrag.changeVisibleCalendars();
                }
            }
        };

        homeFAB = (FloatingActionButton) view.findViewById(R.id.home_fab);
        homeFAB.setTag(1);
        homeFAB.setOnClickListener(myButtonHandler);
        homeFAB.setSize(FloatingActionButton.SIZE_NORMAL);

    }

    public void setContentResolver(ContentResolver cr) {
        this.cr = cr;
    }

    public void setCalendarFrag(CalendarFrag calendarFrag) {
        this.calendarFrag = calendarFrag;
    }

    private ArrayList<HomeEventItem> getEvents() {
        return calModel.readEventsToday(cr);
    }

    public void setTodaysEvents() {
        ArrayList<HomeEventItem> eventsList;

        eventsList = getEvents();

        HomeAdapter adapter = new HomeAdapter(context, eventsList);

        listView = (ListView) rootView.findViewById(R.id.home_list);
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
        Cursor cur = calModel.getEventDetailedInfo(cr, startTime, endTime, eventId);

        //Fetch information from the cursor
        String title = cur.getString(CalendarUtils.TITLE);
        String location = cur.getString(CalendarUtils.LOCATION);
        String description = cur.getString(CalendarUtils.DESCRIPTION);
        String calendar = cur.getString(CalendarUtils.CALENDAR_NAME);
        long calID = cur.getLong(CalendarUtils.CALENDAR_ID);
        int allDay = cur.getInt(CalendarUtils.ALL_DAY);
        cur.close();
        final int notification = calModel.getNotificationTime(cr, startTime,endTime,eventId);

        calendarFrag.openViewEventInfo(eventId, title, startTime, endTime, location, description, calendar, calID, notification, allDay);
    }

    public void setContext(Context c){
        this.context = c;
    }
}