package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class HomeFrag extends Fragment {

    private TextView todayTextView;
    private List<String> events;
    private LinearLayout layout;
    private View view;
    private Context context;
    private CalendarFrag calendarFrag;
    private boolean hasInit = false;
    private FloatingActionButton homeActionButton;
    private View.OnClickListener fabHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        this.view = rootView;
        initComponents(view);
        return rootView;
    }

    private void initComponents(View view) {
        todayTextView = new TextView(context);
        todayTextView.setText("Idag");
        todayTextView.setTextSize(20);
        todayTextView.setTextColor(Color.BLACK);
        events = new ArrayList<String>();
        layout = (LinearLayout) view.findViewById(R.id.linearLayout1);
        layout.addView(todayTextView);
        hasInit = true;
        this.setCalendarInfoToday();
       // homeActionButton = MainActivity.homeActionButton;

        /*
        fabHandler = new View.OnClickListener() {

            public void onClick(View v) {
                if (v.getTag() == homeActionButton.getTag()) {
                    calendarFrag.changeVisibleCalendars();
                }
            }
        };
        */
    }

   public void setCalendarFrag(CalendarFrag calendarFrag){
       this.calendarFrag = calendarFrag;
   }

    /**
     * Sets the info about todays events in the homescreen
     *
     * @return
     */
    public void setCalendarInfoToday() {
        if (calendarFrag != null && hasInit) {
                //get calendarinfo of today from calendar
                List<String> todaysEventsTitles = calendarFrag.getTodaysEvents();

                if (todaysEventsTitles != null || !todaysEventsTitles.isEmpty()) {
                    for (String str : todaysEventsTitles) {
                        TextView tmp = new TextView(context);
                        tmp.setText(str);
                        tmp.setTextColor(Color.BLACK);
                        TextView tmp2 = new TextView(context);
                        tmp2.setText("");
                        if (!events.contains(str)){
                            events.add(str);
                            layout.addView(tmp2);
                            layout.addView(tmp);
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
            }
    }
    public void setContext(Context context) {
        this.context = context;
    }
}
