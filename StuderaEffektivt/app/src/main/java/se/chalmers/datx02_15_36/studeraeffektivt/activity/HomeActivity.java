package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class HomeActivity extends Fragment {

    private TextView todayTextView;
    private List <TextView> textViews;
    private LinearLayout layout;
    private View view;
    private boolean hasReadToday;


    private CalendarActivity calendarActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        this.view = rootView;
        initComponents(view);
        return rootView;
    }

    private void initComponents(View view){
        todayTextView = new TextView(this.getActivity().getApplicationContext());
        todayTextView.setText("Today");
        todayTextView.setTextSize(20);
        todayTextView.setTextColor(Color.BLACK);
        textViews = new ArrayList<TextView>();
        layout = (LinearLayout) view.findViewById(R.id.linearLayout1);
        layout.addView(todayTextView);
        Log.i("HomeActivity:", "InitComponents");
    }

   public void setCalendarActivity(CalendarActivity calendarActivity){
       Log.i("HomeActivity:", "setCalendarActivity");
       this.calendarActivity = calendarActivity;
   }

    /**
     * Sets the info about todays events in the homescreen
     * @return
     */
    public List<String> setCalendarInfo() {
        Log.i("HomeActivity: setCalendarInfo", "view:" + view);

        if (calendarActivity != null) {
            Log.i("HomeActivity: setCalendarInfo", "CalendarActivity_NOT_NULL");

            //get calendarinfo of today from calendar
            List<String> todaysEventsTitles = calendarActivity.getTodaysEvents();

            if(todaysEventsTitles!=null) {
                for (String str : todaysEventsTitles) {
                    TextView tmp = new TextView(this.getActivity().getApplicationContext());
                    tmp.setText(str);
                    tmp.setTextColor(Color.BLACK);
                    TextView tmp2 = new TextView(this.getActivity().getApplicationContext());
                    tmp2.setText("");
                    textViews.add(tmp);
                    layout.addView(tmp2);
                    layout.addView(tmp);
                    Log.i("setCalendarInfo", str);
                }
                this.hasReadToday = true;
                return todaysEventsTitles;
            } else {
                TextView tmp2 = new TextView(this.getActivity().getApplicationContext());
                tmp2.setText("");
                TextView tmp = new TextView(this.getActivity().getApplicationContext());
                tmp.setText("There are no planned events today");
                tmp.setTextColor(Color.BLACK);
                layout.addView(tmp2);
                layout.addView(tmp);
            }
<<<<<<< HEAD
=======

            Log.i("HomeActivity: setCalendarInfo", "view:" + view);

            //Crashes because view is null...
            initComponents(view);

            //studySession1_course1.setText(todaysEventsTitles.get(0));
            //studySession2_course1.setText(todaysEventsTitles.get(1));
            //studySession3_course1.setText(todaysEventsTitles.get(2));

            return todaysEventsTitles;
>>>>>>> origin/develop
        }
        return null;
    }

    public boolean hasReadToday(){
        return this.hasReadToday;
    }
}
