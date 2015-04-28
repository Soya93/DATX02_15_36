package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
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
    private HashMap<Integer, Time> mapping = new HashMap<Integer, Time>();
    Context context;
    private static String[] values = {"Repetitioner", "Studietid", "Vilotid"};
    private int mSelectedHour, mSelectedMinutes;
    private Time studyTime;
    private Time pauseTime;
    private SharedPreferences sharedPref;
    private String prefName = "ButtonPref";
    private static Dialog dialog;
    private NumberPicker np;

    TimePickerDialog.OnTimeSetListener d = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {

            studyTime = new Time(hourOfDay, minute);
            mapping.put(1, studyTime);
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            pauseTime = new Time(hourOfDay, minute);
            mapping.put(2, pauseTime);
            updateView();
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timersettings);
        context = this;
        listView = (ListView) findViewById(R.id.listView);
        initFromSharedPref();
        updateView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Position", String.valueOf(position));
                switch (position) {
                    case 0:
                        show();
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


    public void updateView() {
        listView.setAdapter(new ListAdapter(this, values, mapping));
    }


    public void onPause() {
        super.onPause();
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("studyHour", studyTime.getHour());
        editor.putInt("studyMin", studyTime.getMin());
        editor.putInt("pauseHour", pauseTime.getHour());
        editor.putInt("pauseMin", pauseTime.getMin());
        editor.putInt("reps",mapping.get(0).getMin());
        editor.apply();

    }

    protected void initDefaultValues() {
        studyTime = new Time(0, 25);
        pauseTime = new Time(0, 5);
        mapping.put(0, new Time(0, 2));
        mapping.put(1, studyTime);
        mapping.put(2, pauseTime);
    }

    protected void initFromSharedPref() {
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        int studyMin = sharedPref.getInt("studyMin", -1);
        int studyHour = sharedPref.getInt("studyHour", 0);
        int pauseMin = sharedPref.getInt("pauseMin", -1);
        int pauseHour = sharedPref.getInt("pauseHour", 0);
        int reps = sharedPref.getInt("reps",-1);
        initDefaultValues();


        if (studyMin != -1  ) {
            studyTime = new Time(studyHour, studyMin);
            mapping.put(1, studyTime);}

        if(pauseMin != -1){
                pauseTime = new Time(pauseHour, pauseMin);
                mapping.put(2, pauseTime);
            }
         if(reps != -1) {
             mapping.put(0, new Time(0, reps));
         }


    }


    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);
        np = (NumberPicker) dialogView.findViewById(R.id.numberPicker1);
        np.setMaxValue(10);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mapping.put(0, new Time(0, newVal));

            }
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                updateView();

            }

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



}