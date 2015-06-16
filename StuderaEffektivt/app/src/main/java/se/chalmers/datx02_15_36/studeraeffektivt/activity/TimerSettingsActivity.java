/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.util.HashMap;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Time;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
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
            if (minute > 0) {
                studyTime = new Time(hourOfDay, minute);
                mapping.put(1, studyTime);
                updateView();
            }
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            if (minute > 0) {
                pauseTime = new Time(hourOfDay, minute);
                mapping.put(2, pauseTime);
                updateView();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_event) {
            this.finish();
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timersettings);
        context = this;
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Ställ in tid");
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
                                studyTime.getHour(), studyTime.getMin(),
                                true).show();
                    }
                    break;
                    case 2: {
                        new TimePickerDialog(TimerSettingsActivity.this,
                                t,
                                pauseTime.getHour(), pauseTime.getMin(),
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
        editor.putInt("reps", mapping.get(0).getMin());
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
        int reps = sharedPref.getInt("reps", -1);
        initDefaultValues();


        if (studyMin != -1) {
            studyTime = new Time(studyHour, studyMin);
            mapping.put(1, studyTime);
        }

        if (pauseMin != -1) {
            pauseTime = new Time(pauseHour, pauseMin);
            mapping.put(2, pauseTime);
        }
        if (reps != -1) {
            mapping.put(0, new Time(0, reps));
        }


    }


    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);
        np = (NumberPicker) dialogView.findViewById(R.id.numberPicker1);
        np.setMaxValue(10);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mapping.put(0, new Time(0, newVal));

            }
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                updateView();
            }
        });
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }
        );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
    }


}