package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class HomeActivity extends Fragment {

    private TextView todayTextView;
    private TextView course1TextView;
    private TextView studySession1_course1;
    private TextView studySession2_course1;
    private TextView studySession3_course1;
    private TextView course2TextView;
    private TextView studySession1_course2;
    private TextView studySession2_course2;
    private TextView studySession3_course2;
    private View view;

    private CalendarActivity calendarActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        this.view = rootView;
        //initComponents(view);


        Log.i("HomeActivity: onCreate", "View: "+ view );
        return rootView;
    }

    private void initComponents(View view){
        todayTextView = (TextView) view.findViewById(R.id.todayTextView);
        course1TextView = (TextView) view.findViewById(R.id.course1TextView);
        studySession1_course1 = (TextView) view.findViewById(R.id.studySession1_course1);
        studySession2_course1 = (TextView) view.findViewById(R.id.studySession2_course1);
        studySession3_course1 = (TextView) view.findViewById(R.id.studySession3_course1);
        course2TextView = (TextView) view.findViewById(R.id.course2TextView);
        studySession1_course2 = (TextView) view.findViewById(R.id.studySession1_course2);
        studySession2_course2 = (TextView) view.findViewById(R.id.studySession2_course2);
        studySession3_course2 = (TextView) view.findViewById(R.id.studySession3_course2);
        Log.i("HomeActivity:", "InitComponents");
    }


   public void setCalendarActivity(CalendarActivity calendarActivity){
       Log.i("HomeActivity:", "setCalendarActivity");

       this.calendarActivity = calendarActivity;
   }

    public List<String> setCalendarInfo() {
        Log.i("HomeActivity: setCalendarInfo", "view:" + view);

        if (calendarActivity != null) {
            Log.i("HomeActivity: setCalendarInfo", "CalendarActivity_NOT_NULL");

            //get calendarinfo of today from calendar
            List<String> todaysEventsTitles = calendarActivity.getTodaysEvents();


            for(String str: todaysEventsTitles) {
                Log.i("setCalendarInfo", str);
            }

            Log.i("HomeActivity: setCalendarInfo", "view:" + view);

            //Crashes because view is null...
            initComponents(view);
            /*studySession1_course1.setText(todaysEventsTitles.get(0));
            studySession2_course1.setText(todaysEventsTitles.get(1));
            studySession3_course1.setText(todaysEventsTitles.get(2));*/

            return todaysEventsTitles;
        }
        return null;
    }



}
