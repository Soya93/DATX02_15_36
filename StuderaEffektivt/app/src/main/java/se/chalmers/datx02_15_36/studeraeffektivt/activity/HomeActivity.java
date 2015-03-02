package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private CalendarActivity calendarActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        initComponents(rootView);

        setCalendarInfo();
    setCalendarActivity(null);
        return rootView;
    }

    private void initComponents(View view){
        todayTextView = (TextView) view.findViewById(R.id.todayTextView);
        course1TextView = (TextView) view.findViewById(R.id.course1TextView);
        studySession1_course1 = (TextView) view.findViewById(R.id.studySession1_course1);
        studySession1_course1 = (TextView) view.findViewById(R.id.studySession2_course1);
        studySession1_course1 = (TextView) view.findViewById(R.id.studySession3_course1);
        course2TextView = (TextView) view.findViewById(R.id.course2TextView);
        studySession1_course2 = (TextView) view.findViewById(R.id.studySession1_course2);
        studySession1_course2 = (TextView) view.findViewById(R.id.studySession2_course2);
        studySession1_course2 = (TextView) view.findViewById(R.id.studySession3_course2);
    }

   public void setCalendarActivity(CalendarActivity calendarActivity){
       this.calendarActivity = calendarActivity;
   }

    private void setCalendarInfo(){
        //get calendarinfo of today from calendar

        //set it to the view


    }

}
