package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.IO.LogInHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarFrag extends Fragment {

    CalendarModel calendarModel = new CalendarModel();
    ContentResolver cr;
    View rootView;
    int week = -1;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       rootView = inflater.inflate(R.layout.activity_calendar, container, false);
       calendarModel = new CalendarModel();
       return rootView;
    }

    public void setContentResolver(ContentResolver cr){
        this.cr = cr;
    }

    public Intent addStudySession(){
        return calendarModel.addEventManually(0L, 0L, false, "Studiepass", null, null);
    }

    public Intent openCalendar(){
        return calendarModel.openCalendar();
    }

    public List<String> getTodaysEvents() {
        return calendarModel.readEventsToday(cr);
    }

    public List<String> getSundaysEvents() {
        return calendarModel.readEventsSunday(cr);
    }


    public CalendarModel getCalendarModel(){
        return this.calendarModel;
    }

    //Används inte men kommer vara användbart
    public void readCalendar() {
        calendarModel.getCalendars(cr, "sayo.panda.sn@gmail.com", "com.google");

        calendarModel.readEvents(cr, 0L, 0L);
    }

    public void addEventAuto(View view) {
        calendarModel.getCalendars(cr, "sayo.panda.sn@gmail.com", "com.google");
        calendarModel.getCalendars(cr, "eewestman@gmail.com", "com.google");
        Long eventID = this.calendarModel.addEventAuto(cr);;
    }

    /*  Whatever is below this line does not work and is not invoked */
    public void createBuilder(){
        builder.create().show();
    }

    public void openDialog(){
       builder = new AlertDialog.Builder(getActivity());

       //the alternatives
       String [] alternatives = {"LV1", "LV2", "LV3", "LV4", "LV5", "LV6", "LV7", "LV8"};
       String tasks = "";

       builder.setTitle("Välj ett pass att repetera")
               .setItems(alternatives, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       int studyWeek = which+1;
                       setWeek(studyWeek);
                      Log.i("weekInOpen", week + "");
                       // The 'which' argument contains the index position
                       // of the selected item
                   }
               });
        //builder.create().show();
   }

    public Intent addRepetition() {
        return calendarModel.addEventManually(0L, 0L, true, "Repititonspass för LV" + week, null, "Repetera " +  "");
    }

    private void setWeek(int week){
        this.week = week;
    }

    public int getWeek(){
        return this.week;
    }

    public AlertDialog.Builder getBuilder(){
        return builder;
    }
}
