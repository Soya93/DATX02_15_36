package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        this.view = rootView;
        initComponents(view);
        Log.i("onCreateHome", "home");
        return rootView;
    }

    private void initComponents(View view) {
        todayTextView = new TextView(context);
        todayTextView.setText("Today");
        todayTextView.setTextSize(20);
        todayTextView.setTextColor(Color.BLACK);
        events = new ArrayList<String>();
        layout = (LinearLayout) view.findViewById(R.id.linearLayout1);
        Button button = new Button(context);
        button.setText("Sync");
        layout.addView(button);
        layout.addView(todayTextView);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                setCalendarInfo();
            }});
    }


   public void setCalendarFrag(CalendarFrag calendarFrag){
       Log.i("HomeActivity:", "setCalendarActivity");
       this.calendarFrag = calendarFrag;
   }

    /**
     * Sets the info about todays events in the homescreen
     *
     * @return
     */
    public List<String> setCalendarInfo() {
            Log.i("HomeActivity: setCalendarInfo", "view:" + getView());

            if (calendarFrag != null) {
                //get calendarinfo of today from calendar
                List<String> todaysEventsTitles = calendarFrag.getTodaysEvents();

                if (todaysEventsTitles != null) {
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
                        Log.i("setCalendarInfo", str);
                    }
                    return todaysEventsTitles;
                } else {
                    TextView tmp2 = new TextView(context);
                    tmp2.setText("");
                    TextView tmp = new TextView(context);
                    tmp.setText("There are no planned events today");
                    tmp.setTextColor(Color.BLACK);
                    layout.addView(tmp2);
                    layout.addView(tmp);
                }
            }
        return null;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
