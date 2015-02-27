package se.chalmers.datx02_15_36.studeraeffektivt;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;


public class TimerActivity extends ActionBarActivity {

    private TimePicker t1;

    private Button startButton;
    private Button resetButton;

    private long chosenSeconds;
    private long secondsUntilFinished;

    private long timePassed;
    private final long default_StudyTime = (50*60*1000);
    private final long default_PauseTime = (25*60*1000);

    private final long update_Time=1000;
    private TextView textView;
    private boolean studyTimerIsRunning=false;

    private BroadcastReceiver receiver;
    private boolean hasRestarted;

    private SharedPreferences prefs;
    private String prefName = "MyPref";

    private SharedPreferences statusPrefs;
    private String statusPrefName = "MyPref";

    private final int dialog_setPause = 1;
    private final int dialog_setStart = 0;


    private IntentFilter intentFilter;
    private MyCountDownTimer serviceMCDT;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        serviceMCDT = ((MyCountDownTimer.MCDTBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };




    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            secondsUntilFinished = intent.getLongExtra("TIME_LEFT", -1);
            Log.d("receiver", "Got message: " + secondsUntilFinished);
            t1.setCurrentMinute(secondsToMin(secondsUntilFinished));
            t1.setCurrentHour(secondsToHour(secondsUntilFinished));
            textView.setText("seconds remainig" + secondsUntilFinished/1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        instantiate();
        t1.setCurrentHour(0);
        t1.setCurrentMinute(secondsToMin(default_StudyTime));
        intentFilter = new IntentFilter();
        intentFilter.addAction("TIME_LEFT_SERVICE_ACTION");
        this.registerReceiver(serviceReceiver,intentFilter);







    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume () {
       super.onResume();



    }

    protected void onStop() {
        super.onStop();
        unregisterReceiver(serviceReceiver);
    }


    private void instantiate() {
        t1 = (TimePicker) findViewById(R.id.timePicker);
        t1.setIs24HourView(true);
        resetButton = (Button) findViewById(R.id.button_reset);
        startButton = (Button) findViewById(R.id.button_start_timer);
        prefs = getSharedPreferences(prefName,MODE_PRIVATE);
        textView = (TextView) findViewById(R.id.text_timer);
        statusPrefs= getSharedPreferences(statusPrefName,MODE_PRIVATE);


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
     * Start the timer.
     * Called when the user clicks the Start Timer button.
     */
    public void startTimer(View view) {
        if(startButton.getText().equals("Start")) {
            Intent temp = new Intent(getBaseContext(), MyCountDownTimer.class);
            temp.putExtra("STUDY_TIME",default_StudyTime);
            startService(temp);

        }
        else if (startButton.getText().equals("Pause")){

        }
        else if (startButton.getText().equals("Resume")){

        }

    }



    public void resetTimer(View view) {
        Intent temp = new Intent(getBaseContext(), MyCountDownTimer.class);
        temp.putExtra("STUDY_TIME",default_StudyTime);
        stopService(temp);
    }
    /*
    public void settingsTimer (View view){
        showDialog(0);
    }

    protected Dialog onCreateDialog(int choice) {
        switch (choice) {

            case : 0
                return new AlertDialog.Builder(this).
                       setTitle("Timer settings");

        }
    return null;}

    */


}
