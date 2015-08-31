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
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.SessionsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Time;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;


/**
 * Created by alexandraback on 21/04/15.
 */
public class TimerSettingsActivity extends ActionBarActivity {


    Context context;
    private Time studyTime;
    private Time pauseTime;
    private Time addOldTimeT;
    private Calendar addOldDateCal;
    private Time removeOldTimeT;
    private Calendar removeOldDateCal;
    private int reps;

    private SharedPreferences sharedPref;
    private String prefName = "ButtonPref";
    private NumberPicker np;

    private View rep;
    private View study;
    private View pause;
    private View addOldTime;
    private View addOldDate;
    private View removeOldTime;
    private View removeOldDate;

    private Button addButton;
    private Button removeButton;

    private TextView repetitionsInput;
    private TextView studyTimeInput;
    private TextView pauseTimeInput;
    private TextView addOldTimeInput;
    private TextView addOldDateInput;
    private TextView removeOldTimeInput;
    private TextView removeOldDateInput;

    private String course;




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

        if (id == android.R.id.home) {
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
        actionBar.setTitle("Inställningar");
        course = getIntent().getStringExtra("course");

        //Find view components
        addButton = (Button) findViewById(R.id.addOldStudyButton);
        removeButton = (Button) findViewById(R.id.removeOldStudyButton);


        rep = findViewById(R.id.repInput);
        study = findViewById(R.id.studyTimeInput);
        pause = findViewById(R.id.pauseTimeInput);
        addOldTime = findViewById(R.id.oldTimeInput);
        addOldDate = findViewById(R.id.oldDateInput);

        removeOldTime = findViewById(R.id.removeTimeInput);
        removeOldDate = findViewById(R.id.removeDateInput);

        repetitionsInput = (TextView) rep.findViewById(R.id.right_input);
        studyTimeInput = (TextView) study.findViewById(R.id.right_input);
        pauseTimeInput = (TextView) pause.findViewById(R.id.right_input);
        addOldDateInput = (TextView) addOldDate.findViewById(R.id.right_input);
        addOldTimeInput = (TextView) addOldTime.findViewById(R.id.right_input);

        removeOldDateInput  = (TextView) removeOldDate.findViewById(R.id.right_input);
        removeOldTimeInput = (TextView) removeOldTime.findViewById(R.id.right_input);

        View.OnClickListener myTextViewHandler = new View.OnClickListener() {
            public void onClick(View v) {
                int id = v.getId();

                if (id == rep.getId()) {
                    // set the nuber of repetitions of the study session
                    showRepsDialog();

                } else if (id == study.getId()) {
                    // set the study session length
                    new TimePickerDialog(TimerSettingsActivity.this, d, studyTime.getHour(), studyTime.getMin(), true).show();

                } else if (id == pause.getId()) {
                    // set the pause legnth
                    new TimePickerDialog(TimerSettingsActivity.this, t, pauseTime.getHour(), pauseTime.getMin(), true).show();

                } else if (id == addOldDate.getId()) {
                    // set the date of the old study session
                    openDatePickerDialog();

                }else if(id == addOldTime.getId()){
                    // set the legth of the old study session
                    new TimePickerDialog(TimerSettingsActivity.this, old, addOldTimeT.getHour(), addOldTimeT.getMin(), true).show();
                }else if (id == addButton.getId()){
                    //add time to database
                    SessionsDBAdapter db = new SessionsDBAdapter(context);
                    int min = addOldTimeT.getMin();
                    int totmin = addOldTimeT.getHour()*60 + addOldTimeT.getMin();
                    int week = addOldDateCal.get(Calendar.WEEK_OF_YEAR);
                    db.insertSession(course, week, totmin);
                    String text = (addOldTimeT .getHour() > 1? addOldTimeT .getHour() + " timmar " :  addOldTimeT .getHour() + " timme ") + (min > 0? " & " + min + " minuter ": "") + "har loggats i "+ course;
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();

                } else if (id == removeOldDate.getId()) {
                    // set the date of the old study session
                    openDatePickerDialog();
                }else if(id == removeOldTime.getId()){
                    // set the legth of the old study session
                    new TimePickerDialog(TimerSettingsActivity.this, old, addOldTimeT.getHour(), addOldTimeT.getMin(), true).show();
                }else if (id == removeButton.getId()){
                    //remove time to database
                    SessionsDBAdapter db = new SessionsDBAdapter(context);
                    int min = removeOldTimeT.getMin();
                    int totmin = addOldTimeT.getHour()*60 + addOldTimeT.getMin();
                    int week = removeOldDateCal.get(Calendar.WEEK_OF_YEAR);

                    long result = db.removeSession(course, week, totmin);

                    String textWasRemoved = (removeOldTimeT.getHour() > 1? addOldTimeT .getHour() + " timmar " :  addOldTimeT .getHour() + " timme ") + (min > 0? " & " + min + " minuter ": "") + "har tagits bort från "+ course;
                    String textCouldNotBeRemoved = "Det gick inte att ta bort" + (removeOldTimeT.getHour() > 1? " timmar " :  " timme ") + (min > 0? " & " + min + " minuter ": "") + "från " + course;

                    String toastText = result > 0? textWasRemoved: textCouldNotBeRemoved;

                    Toast toast = Toast.makeText(context, toastText + result, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        };

        // add listeners
        rep.setOnClickListener(myTextViewHandler);
        study.setOnClickListener(myTextViewHandler);
        pause.setOnClickListener(myTextViewHandler);
        addOldDate.setOnClickListener(myTextViewHandler);
        addOldTime.setOnClickListener(myTextViewHandler);
        addButton.setOnClickListener(myTextViewHandler);

        removeOldDate.setOnClickListener(myTextViewHandler);
        removeOldTime.setOnClickListener(myTextViewHandler);
        removeButton.setOnClickListener(myTextViewHandler);

        // Set text to the left side
        ((TextView) rep.findViewById(R.id.left_input)).setText("Antal repetioner");
        ((TextView) study.findViewById(R.id.left_input)).setText("Studietid");
        ((TextView) pause.findViewById(R.id.left_input)).setText("Pausetid");
        ((TextView) addOldTime.findViewById(R.id.left_input)).setText("Studietid");
        ((TextView) addOldDate.findViewById(R.id.left_input)).setText("Datum");

        ((TextView) removeOldTime.findViewById(R.id.left_input)).setText("Studietid");
        ((TextView) removeOldDate.findViewById(R.id.left_input)).setText("Datum");


        //read default values
        initFromSharedPref();
        addOldTimeT = new Time(1, 0);
        addOldDateCal = Calendar.getInstance();

        removeOldTimeT = addOldTimeT;
        removeOldDateCal = addOldDateCal;

        // set the default values to the right side
        repetitionsInput.setText(reps +"");
        studyTimeInput.setText(studyTime.getString());
        pauseTimeInput.setText(pauseTime.getString());
        addOldTimeInput.setText(addOldTimeT.getString());
        removeOldTimeInput.setText(removeOldTimeT.getString());
        SimpleDateFormat startDateFormat = new SimpleDateFormat("E d MMM yyyy");
        addOldDateInput.setText(startDateFormat.format((new Date(addOldDateCal.getTimeInMillis()))));
        removeOldDateInput.setText(startDateFormat.format((new Date(removeOldDateCal.getTimeInMillis()))));

    }




    public void onPause() {
        super.onPause();
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("studyHour", studyTime.getHour());
        editor.putInt("studyMin", studyTime.getMin());
        editor.putInt("pauseHour", pauseTime.getHour());
        editor.putInt("pauseMin", pauseTime.getMin());
        editor.putInt("reps", reps);
        editor.apply();

    }


    protected void initDefaultValues() {
        studyTime = new Time(0, 25);
        pauseTime = new Time(0, 5);
        reps = 2;
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
        }

        if (pauseMin != -1) {
            pauseTime = new Time(pauseHour, pauseMin);
        }
        if (reps != -1) {
            this.reps = reps;
        }


    }


    public void showRepsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);
        np = (NumberPicker) dialogView.findViewById(R.id.numberPicker1);
        np.setMaxValue(10);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setValue(reps);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {


            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                reps = newVal;
            }
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setTextToLable(repetitionsInput, reps+"");
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

    private void setTextToLable(TextView view, String text) {
        view.setText(text);
    }




    private void openDatePickerDialog() {
        Log.i("open date picker", "");

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                Calendar date = Calendar.getInstance();
                date.set(selectedYear, selectedMonth, selectedDay);
                SimpleDateFormat startDateFormat = new SimpleDateFormat("E d MMM yyyy");
                addOldDateInput.setText(startDateFormat.format((new Date(date.getTimeInMillis()))));
                removeOldDateInput.setText(addOldDateInput.getText());
            }
        };

        DatePickerDialog datePickerDialog;
        int year = addOldDateCal.get(Calendar.YEAR);
        int month = addOldDateCal.get(Calendar.MONTH);
        int day = addOldDateCal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(TimerSettingsActivity.this, datePickerListener, year,month, day);

        datePickerDialog.show();
        datePickerDialog.setCancelable(true);
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            if (hourOfDay > 0 || minute > 0) {
                pauseTime = new Time(hourOfDay, minute);
                pauseTimeInput.setText(pauseTime.getString());
            }
        }
    };

    TimePickerDialog.OnTimeSetListener d = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            if (hourOfDay > 0 || minute > 0) {
                studyTime = new Time(hourOfDay, minute);
                studyTimeInput.setText(studyTime.getString());
            }
        }
    };

    TimePickerDialog.OnTimeSetListener old = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            if (hourOfDay > 0 || minute > 0) {
                addOldTimeT = new Time(hourOfDay, minute);
                addOldTimeInput.setText(addOldTimeT.getString());
                removeOldTimeT = addOldTimeT;
                removeOldTimeInput.setText(addOldTimeInput.getText());
            }
        }
    };
}