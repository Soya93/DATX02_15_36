package se.chalmers.datx02_15_36.studeraeffektivt;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;


public class TimerActivity extends ActionBarActivity {
    private CountDownTimer studyTimer;
    private CountDownTimer pauseTimer;

    private TimePicker t1;

    private Button startButton;
    private Button resetButton;

    private long chosenSeconds;
    private long secondsUntilFinished;

    private long timePassed;
    private final long default_TotalTime =(60*60*1000);
    private final long default_StudyTime = (35*60*1000);
    private final long default_PauseTime = (25*60*1000);

    private final long update_Time=1000;
    private TextView textView;


    private final int dialog_setPause = 1;
    private final int dialog_setStart = 0;

    private MyCountDownTimer serviceMCDT;
    private boolean studyTimerIsRunning = false;
    private long timeFromService;
    private SharedPreferences prefs;
    private String prefName = "WhichTimerIsRunning";



    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMCDT = ((MyCountDownTimer.MCDTBinder) service).getService();
            timeFromService = serviceMCDT.returnStudyTime();
          // boolean isStudyTimerRunning = prefs.getBoolean("IsStudyTimerRunning",false);
            if (timeFromService <= 0){
                textView.setText("Alarm");
            }
            else {

                t1.setCurrentHour(secondsToHour(timeFromService * 1000));
                t1.setCurrentMinute(secondsToMin(timeFromService * 1000));



                    studyTimerFunction(timeFromService * 1000, update_Time);
                    studyTimer.start();
              /*
                else {
                    pauseTimerFunction(timeFromService*1000,update_Time);
                    pauseTimer.start();
                } */
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMCDT=null;
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);


        instantiate();






    }

    protected void onStop() {
        super.onStop();

       // prefs = getSharedPreferences(prefName,MODE_PRIVATE);
       // prefs.edit().putBoolean("IsStudyTimerRunning",studyTimerIsRunning).commit();
        if(studyTimerIsRunning){
            studyTimer.cancel();
        }
        else{
            pauseTimer.cancel();
        }
        if(startButton.getText().equals("Start")) {
            Intent i = new Intent(this, MyCountDownTimer.class);
            i.putExtra("STUDY_TIME", secondsUntilFinished / 1000);
            //

            startService(i);
            Log.d("SÅMÅNGA SEKUNDER ÄR", "kvar" + secondsUntilFinished);
        }


    }



    protected void onResume () {
        super.onResume();
        if(isMyServiceRunning (MyCountDownTimer.class)) {
            Intent i = new Intent(getBaseContext(), MyCountDownTimer.class);
            bindService(i, sc, Context.BIND_AUTO_CREATE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable (){
                public void run(){
                    Intent i = new Intent(getBaseContext(), MyCountDownTimer.class);
                    stopService(i);
                    unbindService(sc);

                }
            },2000);



        }
        else {
            setTimePicker(default_StudyTime-60000);
        }

    }


    private void instantiate() {
        t1 = (TimePicker) findViewById(R.id.timePicker);
        t1.setIs24HourView(true);
        t1.setEnabled(false);
        resetButton = (Button) findViewById(R.id.button_reset);
        startButton = (Button) findViewById(R.id.button_start_timer);
        textView = (TextView) findViewById(R.id.text_timer);


    }

    private int secondsToHour(long millisUntilFinished) {
        return (int) (millisUntilFinished / 1000 / 3600);
    }

    private int secondsToMin(long millisUntilFinished) {
        return ((int) ((millisUntilFinished / 1000 % 3600)) + 60) / 60;
    }


    private void setTimePicker(long millisUntilFinished) {
        int hour = secondsToHour(millisUntilFinished);
        int minute = secondsToMin(millisUntilFinished);
        t1.setCurrentHour(hour);
        t1.setCurrentMinute(minute);
    }

    /**
     * Set the timer.
     */
    public CountDownTimer studyTimerFunction(long millisInFuture, long countDownInterval) {

        studyTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                studyTimerIsRunning=true;

                textView.setText("study seconds remaining: " + millisUntilFinished / 1000);
                setTimePicker(millisUntilFinished);

                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
                pauseTimerFunction(default_PauseTime,update_Time);
                pauseTimer.start();

            }
        };
        return studyTimer;

    }
    public CountDownTimer pauseTimerFunction(long millisInFuture, long countDownInterval) {

        pauseTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                studyTimerIsRunning=false;

                textView.setText("pause seconds remaining: " + millisUntilFinished / 1000);
                setTimePicker(millisUntilFinished);

                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
                studyTimerFunction(default_StudyTime,update_Time);

            }
        };
        return studyTimer;

    }


    /**
     * Start the timer.
     * Called when the user clicks the Start Timer button.
     */
    public void startTimer(View view) {
        if(startButton.getText().equals("Start")) {
            studyTimerFunction(default_StudyTime, 100);
            studyTimer.start();
            startButton.setText("Pause");
        }
        else if (startButton.getText().equals("Pause")){
            studyTimer.cancel();
            startButton.setText("Resume");
        }
        else if (startButton.getText().equals("Resume")){
            studyTimerFunction(secondsUntilFinished, 100);
            studyTimer.start();
            startButton.setText("Pause");
        }

    }



    public void resetTimer(View view) {
        studyTimer.cancel();
        setTimePicker(default_StudyTime - 60000);
    }

    public void settingsTimer (View view){
        showDialog(0);
    }

    protected Dialog onCreateDialog(int choice) {
        switch (choice) {

            case 0: {
                return new AlertDialog.Builder(this).
                        setTitle(getResources()
                                .getString(R.string.settings_label))
                        .setPositiveButton("Change study time", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int arg1) {
                            }
                        }).show();
            }

        }
        return null;
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}

