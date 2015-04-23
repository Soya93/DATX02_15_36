package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Time;
import se.chalmers.datx02_15_36.studeraeffektivt.util.ListAdapter;


/**
 * Created by alexandraback on 21/04/15.
 */
public class TimerSettingsActivity extends ActionBarActivity {

    private ListView listView;
    private HashMap<Integer,Time> mapping = new HashMap<Integer,Time> () ;
    Context context;
    private static String[] values = {"Repetitioner", "Studietid", "Vilotid"};
    private int  mSelectedHour, mSelectedMinutes;
    private Time studyTime;
    private Time pauseTime;


    TimePickerDialog.OnTimeSetListener d = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {

         studyTime = new Time(hourOfDay,minute);
           mapping.put(1,studyTime);
            test();
        }
    };

     TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
         public void onTimeSet(TimePicker view, int hourOfDay,
                               int minute) {
             pauseTime = new Time(hourOfDay,minute);
              mapping.put(2,pauseTime);
             }
     };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timersettings);
        context = this;
        listView = (ListView) findViewById(R.id.listView);
       mapping.put(0,new Time(0,2));
        mapping.put(1,new Time(0,25));
        mapping.put(2,new Time(0,5));

        test();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Position", String.valueOf(position));
                switch (position) {
                    case 0:

                        break;
                    case 1: {
                        new TimePickerDialog(TimerSettingsActivity.this,
                                d,
                                mSelectedHour, mSelectedMinutes,
                                true).show();
                    }
                    break;
                    case 2: {
                        new TimePickerDialog(TimerSettingsActivity.this,
                                t,
                                mSelectedHour, mSelectedMinutes,
                                true).show();
                    }
                    break;
                }

            }
        });

    }


    public void test () {
        listView.setAdapter(new ListAdapter(this, values, mapping));
    }


}