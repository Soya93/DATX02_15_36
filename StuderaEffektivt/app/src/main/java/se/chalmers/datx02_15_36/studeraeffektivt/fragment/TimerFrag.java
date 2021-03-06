package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import java.util.HashMap;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.TimerSettingsActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Time;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;


public class TimerFrag extends Fragment {

    private ImageButton startButton;
    private ImageButton stopButton;
    private ImageButton pauseButton;

    private boolean isInitialized;
    private int buttonId = R.drawable.ic_action_play;
    private boolean hasBeenPaused = false;

    private TextView textView;

    private View rootView;
    private FlowLayout taskList;
    private Switch taskSwitch;
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


    private DBAdapter dbAdapter;
    private Handler serviceHandler;
    private SharedPreferences sharedPref;
    private String prefName = "ButtonPref";

    private boolean isActivyRunning = false;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER_1:
                    Bundle b = msg.getData();
                    serviceInt = b.getLong("timePassed", -1);
                    phaceInt = b.getInt("Phace", -1);
                    if(isActivyRunning) {
                        if (phaceInt == 0) {
                            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                            progressBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progressbar_study));
                        }

                        if (phaceInt == 1) {
                            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                            progressBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progressbar_pause));
                        }
                    }
                    setTimerView(serviceInt);
                    break;
                case TIMER_FINISHED:
                    resetTimer();
                    break;
            }

        }
    };
    MyCountDownTimer timerService;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerService = ((MyCountDownTimer.MCDTBinder) service).getService();
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
            dbAdapter = new DBAdapter(getActivity());
        }
        instantiate();
        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getTimeFromTimerSettings() {
        sharedPref = getActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);
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

    public void getViewFromSharedPref() {
        sharedPref = getActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);

        phaceInt = sharedPref.getInt("Phace", -1);
        int buttonTemp = sharedPref.getInt("buttonImage", -1);
        if (buttonTemp > 0) {
            buttonId = buttonTemp;
            startButton.setImageResource(buttonId);
        }
        if(!isMyServiceRunning(MyCountDownTimer.class)) {
            startButton.setImageResource(R.drawable.ic_action_play);
        }
        hasBeenPaused = sharedPref.getBoolean("hasPaused", false);
        Log.d("hasbeenPause",String.valueOf(hasBeenPaused));
        if (hasBeenPaused) {
          long timeLeft = sharedPref.getLong("timeLeft",-1);
            Log.d("timeLeft",String.valueOf(timeLeft));
            setTimerView(timeLeft);
        }
        else{
              startSetTimerView();
        }

    }


    public void onStart() {
        super.onStart();
        startButton.setImageResource(buttonId);
        if (isMyServiceRunning(MyCountDownTimer.class)) {
            Intent i = new Intent(getActivity().getBaseContext(), MyCountDownTimer.class);
            getActivity().bindService(i, sc, Context.BIND_AUTO_CREATE);
        }

        isActivyRunning = true;
        getTimeFromTimerSettings();
        getViewFromSharedPref();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedCourse();
                taskList.removeAllViews();
                taskList.addTasksFromDatabase(dbAdapter, ccode, assignmentType, week);
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
        taskList = (FlowLayout) rootView.findViewById(R.id.taskList);
        taskSwitch = (Switch) rootView.findViewById(R.id.taskSwitch);
        previousWeek = (ImageButton) rootView.findViewById(R.id.previousWeek);
        nextWeek = (ImageButton) rootView.findViewById(R.id.nextWeek);
        textViewWeek = (TextView) rootView.findViewById(R.id.textViewWeek);

        taskSwitch.setVisibility(View.VISIBLE);

        setCourses();

        week = Utils.getCurrWeekNumber();
        textViewWeek.setText("Vecka " + String.valueOf(week));

        initButtons();
        assignmentType = AssignmentType.OTHER;
        spinner.setSelection(0);

        if(spinner.getSelectedItem()!=null) {
            setSelectedCourse();
            updateTaskList(assignmentType, week);
        }else {

        }

        isInitialized = true;

    }

    private void setCourses() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Cursor cursor = dbAdapter.getOngoingCourses();
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
        }
    }


    public void startTimer() {
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
        Intent i = new Intent(getActivity().getBaseContext(), MyCountDownTimer.class);
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
        return buttonId == R.drawable.ic_action_pause;
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
            progressBar.setProgress((int) (secondsUntilFinished * 1000 / default_studyTime.timeToMillisSeconds()));

            textView.setText(t.getTimeWithSecondsString());
        }
        if (phaceInt == 1) {
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
        if(isMyServiceRunning(MyCountDownTimer.class)) {
            Intent i = new Intent(getActivity().getBaseContext(), MyCountDownTimer.class);
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
        if (isMyServiceRunning(MyCountDownTimer.class)) {
            serviceHandler.sendEmptyMessage(3);
            removeMessages();
            getActivity().unbindService(sc);
        }

        saveFragmentState();
        isActivyRunning = false;
    }

    private void removeMessages() {
        handler.removeMessages(0);
        handler.removeMessages(1);
        handler.removeMessages(2);
    }

    private void saveFragmentState() {
        sharedPref = getActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("buttonImage", buttonId);
        editor.putBoolean("hasPaused", hasBeenPaused);
        editor.putInt("Phace", phaceInt);


        if (hasBeenPaused) {
            editor.putLong("timeLeft", serviceInt);
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
        taskList.addTasksFromDatabase(dbAdapter, ccode, assignmentType, week);

        if(taskList.isEmpty()){
            TextView textView1 = new TextView(getActivity());
            textView1.setText("Ingen uppgift av den här typen för den valda veckan");
            taskList.addView(textView1);
        }
    }

    public void initButtons() {

        taskSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    assignmentType = AssignmentType.READ;
                    updateTaskList(assignmentType, week);
                }
                else{
                    assignmentType = AssignmentType.OTHER;
                    updateTaskList(assignmentType, week);
                }
            }
        });

        previousWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                week--;
                textViewWeek.setText("Vecka " + String.valueOf(week));
                updateTaskList(assignmentType, week);
            }
        });

        nextWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                week++;
                textViewWeek.setText("Vecka " + String.valueOf(week));
                updateTaskList(assignmentType, week);
            }
        });

        colorSwitch();

        colorNextButtonGrey();
        colorPrevButtonGrey();
    }

    private void colorSwitch(){
        int colorOn = Color.parseColor("#757575");
        int colorOff = Color.parseColor("#757575");
        StateListDrawable thumbStates = new StateListDrawable();
        thumbStates.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(colorOn));
        thumbStates.addState(new int[]{}, new ColorDrawable(colorOff)); // this one has to come last
        taskSwitch.setThumbDrawable(thumbStates);

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
        updateTaskList(assignmentType, week);
    }

}