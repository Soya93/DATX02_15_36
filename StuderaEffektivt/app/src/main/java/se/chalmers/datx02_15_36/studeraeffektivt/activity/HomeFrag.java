package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.ContentResolver;
import android.content.Context;
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
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.model.HomeAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.HomeEventItem;


public class HomeFrag extends Fragment {

    private TextView todayTextView;
    private List<String> events;
    private LinearLayout layout;
    private View rootView;
    private Context context;
    private CalendarFrag calendarFrag;
    private boolean hasInit = false;
    private FloatingActionButton homeFAB;
    private List<String> todaysEventsTitles;
    private CalendarModel calModel;
    private ContentResolver cr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_home, container, false);

        initComponents(rootView);
        calModel = new CalendarModel();
        return rootView;
    }

    private void initComponents(View view) {
        todayTextView = new TextView(context);
        todayTextView.setText("Idag");
        todayTextView.setTextSize(20);
        todayTextView.setTextColor(Color.BLACK);
        events = new ArrayList<String>();
        //layout = (LinearLayout) rootView.findViewById(R.id.linearLayout1);
       // layout.addView(todayTextView);
        hasInit = true;



        View.OnClickListener myButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getTag() == homeFAB.getTag()) {
                    //calendarFrag.changeVisibleCalendars();
                    ArrayList<HomeEventItem> eventsList = new ArrayList<HomeEventItem>();

                    eventsList = getEvents();

                    HomeAdapter adapter = new HomeAdapter(context, eventsList);


                    ListView listView = (ListView) rootView.findViewById(R.id.home_list);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //startActivity(new Intent(rootView.getContext(), ExampleDetailsActivity.class));
                        }
                    });
                }
            }
        };

        homeFAB = (FloatingActionButton) view.findViewById(R.id.home_fab);
        homeFAB.setTag(1);
        homeFAB.setOnClickListener(myButtonHandler);
/*
        ArrayList<HomeEventItem> eventsList = new ArrayList<HomeEventItem>();

        eventsList = getEvents();

        HomeAdapter adapter = new HomeAdapter(context, eventsList);


        ListView listView = (ListView) rootView.findViewById(R.id.home_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View rootView, int position, long id) {
                //startActivity(new Intent(rootView.getContext(), ExampleDetailsActivity.class));
            }
        });
        */







    }

    public void setContentResolver(ContentResolver cr){
        this.cr = cr;
    }

    public void setCalendarFrag(CalendarFrag calendarFrag) {
        this.calendarFrag = calendarFrag;
    }

    private ArrayList<HomeEventItem> getEvents() {
        return calModel.readEventsToday(cr);
    }

    /**
     * Sets the info about todays events in the homescreen
     *
     * @return
     */
    public void setCalendarInfoToday() {
        if (calendarFrag != null && hasInit) {

            //get calendarinfo of today from calendar
          // todaysEventsTitles = calendarFrag.getTodaysEvents();

            /*


            if (todaysEventsTitles != null || !todaysEventsTitles.isEmpty()) {
                for (String str : todaysEventsTitles) {

                    HomeEventItem eventItem = new HomeEventItem(this.getActivity().getApplicationContext());
                    eventItem.setTitle(str);
                    eventItem.setTime("10-12");
                    eventItem.setLocation("hemma");
                    eventItem.setTimeToStart("10 min");



                    Log.i("Home: ", str);



                        TextView tmp = new TextView(context);
                        tmp.setText(str);
                        tmp.setTextColor(Color.BLACK);

                    TextView tmp2 = new TextView(context);
                    tmp2.setText("hej");


                    // so events will not be added several times
                    if (!events.contains(str)) {
                        events.add(str);
                        layout.addView(eventItem);
                        layout.addView(tmp);
                        layout.addView(tmp2);

                    }

                }
            } else {
                TextView tmp2 = new TextView(context);
                tmp2.setText("");
                TextView tmp = new TextView(context);
                tmp.setText("Det finns inga planerade händelser idag");
                tmp.setTextColor(Color.BLACK);
                layout.addView(tmp2);
                layout.addView(tmp);
            }
            hasInit = false;
            */
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
