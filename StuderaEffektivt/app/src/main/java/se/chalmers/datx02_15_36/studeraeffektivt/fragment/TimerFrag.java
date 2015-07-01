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

package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;


import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.TimerSettingsActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.CoursesDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Time;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.TimerService;
import se.chalmers.datx02_15_36.studeraeffektivt.util.sharedPreference.CoursePreferenceHelper;
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;


public class TimerFrag extends Fragment {

    private ImageButton startButton;
    private ImageButton stopButton;
    private ImageButton pauseButton;

    private boolean isInitialized;
    private int buttonId = R.drawable.ic_action_play;
    private boolean hasBeenPaused = false;

    private TextView textView;

    private View rootView;
    private AssignmentCheckBoxLayout taskList;
    private Spinner taskSwitch;
    private ImageButton previousWeek;
    private ImageButton nextWeek;

    public static final int TIMER_1 = 0;
    public static final int TIMER_FINISHED = 1;

    private TextView textViewWeek;

    private long serviceInt;

    private Time default_studyTime;
    private Time default_pauseTime;
    private int reps;
    private String ccode;
    private int phaceInt;

    private AssignmentType assignmentType;
    private int week;

    private Spinner spinner;
    private ProgressBar progressBar;

    private CoursesDBAdapter coursesDBAdapter;
    private OldAssignmentsDBAdapter assDBAdapter;
    private Handler serviceHandler;

    private SharedPreferences sharedPref;
    private String buttonPrefName = "ButtonPref";
    private String weekPrefName = "WeekPref";

    private CoursePreferenceHelper cph;

    private boolean isActivityRunning = false;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER_1:
                    Bundle b = msg.getData();
                    serviceInt = b.getLong("timePassed", -1);
                    phaceInt = b.getInt("Phace", -1);
                    if(isActivityRunning){
                    setTimerView(serviceInt);}
                    break;
                case TIMER_FINISHED:
                    resetTimer();
                    break;
            }

        }
    };
    TimerService timerService;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerService = ((TimerService.MCDTBinder) service).getService();
            timerService.setHandler(handler);
            timerService.setActivityIsRunning();
            serviceHandler = timerService.getServiceHandler();
        }
         @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
        }


    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_timer, container, false);
        if (getActivity() != null) {
            coursesDBAdapter = new CoursesDBAdapter(getActivity());
            assDBAdapter = new OldAssignmentsDBAdapter(getActivity());
        }
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        cph = CoursePreferenceHelper.getInstance(getActivity());

        instantiate();
        return rootView;
    }


    private void getTimeFromTimerSettings() {
        sharedPref = getActivity().getSharedPreferences(buttonPrefName, Context.MODE_PRIVATE);
        int studyMin = sharedPref.getInt("studyMin", -1);
        int studyHour = sharedPref.getInt("studyHour", -1);
        int pauseMin = sharedPref.getInt("pauseMin", -1);
        int pauseHour = sharedPref.getInt("pauseHour", -1);
        reps = sharedPref.getInt("REPS",1);
        default_studyTime = new Time(0, 25);
        default_pauseTime = new Time(0, 5);
        if (studyMin != -1 ) {
            default_studyTime = new Time(studyHour, studyMin);
        }
        if(pauseMin != -1) {
            default_pauseTime = new Time(pauseHour, pauseMin);
        }


    }

    public void onStart() {
        super.onStart();
        getTimeFromTimerSettings();
        sharedPref = getActivity().getSharedPreferences(buttonPrefName, Context.MODE_PRIVATE);
        isActivityRunning = true;
        if (isMyServiceRunning(TimerService.class)) {
            Intent i = new Intent(getActivity().getBaseContext(), TimerService.class);
            getActivity().bindService(i, sc, Context.BIND_AUTO_CREATE);
            long timeLeft = sharedPref.getLong("timeLeft", -1);

            hasBeenPaused = sharedPref.getBoolean("hasPaused", false);
            buttonId = sharedPref.getInt("buttonId",1);
            Log.d("onstart", String.valueOf(hasBeenPaused));
            startButton.setImageResource(R.drawable.ic_action_pause);
            if (hasBeenPaused && (buttonId == R.drawable.ic_action_play )) {
                startButton.setImageResource(R.drawable.ic_action_play);
                phaceInt = sharedPref.getInt("phaceInt",-1);
                Log.d("phaceIntStart",String.valueOf(phaceInt));
                Log.d("timeLeft", String.valueOf(timeLeft));

                setTimerView(timeLeft);

            }
            if (buttonId == R.drawable.ic_action_pause){
                startButton.setImageResource(R.drawable.ic_action_pause);


            }

        }
          else{
                startButton.setImageResource(R.drawable.ic_action_play);
                 getTimeFromTimerSettings();
                startSetTimerView();

            }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedCourse();
                taskList.removeAllViews();
                taskList.addTasksFromDatabase(assDBAdapter, ccode, assignmentType, week);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
            Log.d("Text", textView.getText().toString());

    }

    public void onResume() {
        super.onResume();
        getTimeFromTimerSettings();
    }

    private void instantiateButtons() {
        startButton = (ImageButton) rootView.findViewById(R.id.button_start_timer);
        stopButton = (ImageButton) rootView.findViewById(R.id.button_reset);
        startButton.setBackgroundColor(Color.TRANSPARENT);
        previousWeek = (ImageButton) rootView.findViewById(R.id.previousWeek);
        nextWeek = (ImageButton) rootView.findViewById(R.id.nextWeek);
        ImageButton settingsButton = (ImageButton) rootView.findViewById(R.id.button);
        settingsButton.setBackgroundColor(Color.TRANSPARENT);
        ImageButton stopButton = (ImageButton) rootView.findViewById(R.id.button_reset);
        stopButton.setBackgroundColor(Color.TRANSPARENT);

    }

    private void instantiate() {
        instantiateButtons();
        textView = (TextView) rootView.findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),TimerSettingsActivity.class);
                startActivity(i);
            }
        });
        spinner = (Spinner) rootView.findViewById(R.id.spinner_timer);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        taskList = (AssignmentCheckBoxLayout) rootView.findViewById(R.id.taskList);
        taskSwitch = (Spinner) rootView.findViewById(R.id.taskSwitch);
        previousWeek = (ImageButton) rootView.findViewById(R.id.previousWeek);
        nextWeek = (ImageButton) rootView.findViewById(R.id.nextWeek);
        textViewWeek = (TextView) rootView.findViewById(R.id.textViewWeek);

        taskSwitch.setVisibility(View.VISIBLE);

        setCourses();

        week = getChosenWeek();

        textViewWeek.setText("Vecka " + String.valueOf(week));

        initButtons();
        assignmentType = AssignmentType.PROBLEM;

        cph.setSpinnerCourseSelection(spinner);

        if(spinner.getSelectedItem()!=null) {
            setSelectedCourse();
            updateTaskList(assignmentType, week);
        }

        isInitialized = true;
        setTaskTypeSpinner();
    }

    private void setCourses() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Cursor cursor = coursesDBAdapter.getOngoingCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String ccode = cursor.getString(ccodeColumn);
                String cname = cursor.getString(cnameColumn);
                adapter.add(ccode + " " + cname);
            }
        }
        else{
            adapter.add("Inga tillagda kurser");
        }

    }


    public void setSelectedCourse() {
        Log.i("timer", "spinner's selected item: " + spinner.getSelectedItem());
        if(spinner.getSelectedItem() != null){
            String temp = spinner.getSelectedItem().toString();
            String[] parts = temp.split(" ");
            this.ccode = parts[0];

            cph.setSharedCoursePos(spinner.getSelectedItemPosition());

            //Tests if the update of the sharedpref worked:
            Log.d("sharedcourse", "Timer. set select: "+spinner.getSelectedItemPosition()
                    + " get select: "+ cph.getSharedCoursePos());
        }
    }


    public void startTimer() {
        Log.d("buttonId",String.valueOf(buttonId == R.drawable.ic_action_play));
        Log.d("hasbeenPause",String.valueOf(hasBeenPaused));
        if (hasBeenStarted()) {
            spinner.setEnabled(false);
            sendDataToService();
            buttonId = R.drawable.ic_action_pause;
            startButton.setImageResource(buttonId);
        } else if (hasBeenPaused()) {
            hasBeenPaused = true;
            serviceHandler.sendEmptyMessage(0);
            buttonId = R.drawable.ic_action_play;
            startButton.setImageResource(buttonId);

        } else if (hasBeenRestarted()) {
            serviceHandler.sendEmptyMessage(1);
            buttonId = R.drawable.ic_action_pause;
            startButton.setImageResource(buttonId);
        }

    }


    private void sendDataToService() {
        Intent i = new Intent(getActivity().getBaseContext(), TimerService.class);
        i.putExtra("CCODE", ccode);
        i.putExtra("TIME_STUDY", default_studyTime.timeToMillisSeconds());
        i.putExtra("TIME_PAUSE", default_pauseTime.timeToMillisSeconds());
        i.putExtra("REPS",reps);
        getActivity().bindService(i, sc, Context.BIND_AUTO_CREATE);
        getActivity().startService(i);

    }

    private boolean hasBeenStarted() {
        return buttonId == R.drawable.ic_action_play && !hasBeenPaused;
    }

    private boolean hasBeenPaused() {
        return buttonId == R.drawable.ic_action_pause ;
    }

    private boolean hasBeenRestarted() {
        return buttonId == R.drawable.ic_action_play && hasBeenPaused;
    }

    public void startSetTimerView() {
        textView.setText(default_studyTime.getString());
    }


    public void setTimerView(long secondsUntilFinished) {
        Time t = Time.setTimeFromMilliSeconds(secondsUntilFinished);

        if (phaceInt == 0) {
            progressBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progressbar_study));
            progressBar.setProgress((int) (secondsUntilFinished * 1000 / default_studyTime.timeToMillisSeconds()));
            textView.setText(t.getTimeWithSecondsString());
        }
        if (phaceInt == 1) {
            progressBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progressbar_pause));
            progressBar.setProgress((int) ((secondsUntilFinished * 1000 / default_pauseTime.timeToMillisSeconds())));
            textView.setText(t.getTimeWithSecondsString());
        }
    }

    public void resetTimer() {
        buttonId = R.drawable.ic_action_play;
        hasBeenPaused = false;
        progressBar.setProgress(1000);
        startButton.setImageResource(buttonId);
        progressBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progressbar_study));
        startSetTimerView();
        if(isMyServiceRunning(TimerService.class)) {
            Intent i = new Intent(getActivity().getBaseContext(), TimerService.class);
            getActivity().stopService(i);
            getActivity().unbindService(sc);
        }
    }

    public void settingsTimer() {
        resetTimer();
        Intent i = new Intent(getActivity(), TimerSettingsActivity.class);
        startActivity(i);
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (isMyServiceRunning(TimerService.class)) {
            serviceHandler.sendEmptyMessage(3);
            removeMessages();
            getActivity().unbindService(sc);
        }

        saveFragmentState();
        isActivityRunning = false;
    }

    private void removeMessages() {
        handler.removeMessages(0);
        handler.removeMessages(1);
        handler.removeMessages(2);
    }

    private void saveFragmentState() {
        sharedPref = getActivity().getSharedPreferences(buttonPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("hasPaused", hasBeenPaused);
        editor.putInt("buttonId",buttonId);



        if (hasBeenPaused) {
            editor.putLong("timeLeft", serviceInt);
            Log.d("phaceIntDestroy",String.valueOf(phaceInt));
            editor.putInt("phaceInt", phaceInt);
        }
        editor.apply();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void updateTaskList(AssignmentType assignmentType, int week) {

        taskList.removeAllViews();
        taskList.addTasksFromDatabase(assDBAdapter, ccode, assignmentType, week);

        if(taskList.isEmpty()){
            TextView textView1 = new TextView(getActivity());
            textView1.setText("Ingen uppgift av den här typen för den valda veckan");
            taskList.addView(textView1);
        }
    }

    public void initButtons() {

            taskSwitch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (taskSwitch.getSelectedItem().toString().equals(AssignmentType.PROBLEM.toString())) {
                        assignmentType = AssignmentType.PROBLEM;
                        updateTaskList(assignmentType, week);
                    } else if (taskSwitch.getSelectedItem().toString().equals(AssignmentType.READ.toString())) {
                        assignmentType = AssignmentType.READ;
                        updateTaskList(assignmentType, week);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        previousWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                week--;
                textViewWeek.setText("Vecka " + String.valueOf(week));
                updateTaskList(assignmentType, week);
                setSharedPreferences();
                Log.i("TimerFrag", "backwardclick" + week);

            }
        });

        nextWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                week++;
                textViewWeek.setText("Vecka " + String.valueOf(week));
                updateTaskList(assignmentType, week);
                setSharedPreferences();
                Log.i("TimerFrag", "forwardclick" + week);
            }
        });

        colorSwitch();

        colorNextButtonGrey();
        colorPrevButtonGrey();
    }

    private void setTaskTypeSpinner(){
        String[] assignmentTypes = new String[]{AssignmentType.HANDIN.toString(), AssignmentType.LAB.toString(), AssignmentType.PROBLEM.toString(), AssignmentType.READ.toString(), AssignmentType.OBLIGATORY.toString(), AssignmentType.OTHER.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, assignmentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskSwitch.setAdapter(adapter);
        taskSwitch.setSelection(2);
    }


    private void setSharedPreferences(){
        sharedPref = getActivity().getSharedPreferences(weekPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("weekInt", week);
        editor.apply();
        Log.i("TimerFrag", "added" + getChosenWeek());

    }

    private int getChosenWeek(){
        sharedPref = getActivity().getSharedPreferences(weekPrefName, Context.MODE_PRIVATE);
        return sharedPref.getInt("weekInt", CalendarUtils.getCurrWeekNumber());
    }

    private void colorSwitch(){
        int colorOn = Color.parseColor("#757575");
        int colorOff = Color.parseColor("#757575");
        StateListDrawable thumbStates = new StateListDrawable();
        thumbStates.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(colorOn));
        thumbStates.addState(new int[]{}, new ColorDrawable(colorOff)); // this one has to come last
       // taskSwitch.setThumbDrawable(thumbStates);

        /*/The color of the taskSwitches background/track
        int color1 = Color.parseColor("#B3E5FC");
        int color2 = Color.parseColor("#B3E5FC");
        StateListDrawable trackStates = new StateListDrawable();
        trackStates.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(color1));
        trackStates.addState(new int[]{}, new ColorDrawable(color2)); // this one has to come last
        taskSwitch.setTrackDrawable(trackStates);*/
    }

    private void colorNextButtonGrey(){
        Drawable forwardDrawable = getResources().getDrawable( R.drawable.ic_navigation_chevron_right).mutate();
        forwardDrawable.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        nextWeek.setBackground(forwardDrawable);
    }

    private void colorPrevButtonGrey(){
        Drawable backDrawable = getResources().getDrawable( R.drawable.ic_navigation_chevron_left).mutate();
        backDrawable.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        previousWeek.setBackground(backDrawable);
    }
    public boolean hasInit(){
        return isInitialized;
    }

    public void updateView(){
        Log.d("sharedcourse", "timer updateview, sharedId: "+ cph.getSharedCoursePos());
        updateTaskList(assignmentType, getChosenWeek());
        cph.setSpinnerCourseSelection(spinner);
    }

}