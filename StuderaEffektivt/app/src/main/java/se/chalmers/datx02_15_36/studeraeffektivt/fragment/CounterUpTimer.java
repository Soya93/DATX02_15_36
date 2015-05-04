package se.chalmers.datx02_15_36.studeraeffektivt.fragment;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class CounterUpTimer extends Activity {

    private TimePicker viewTimePassed;
    private Button startB;
    private Button stopB;
    private int timePassed;
    private Timer timer;
    private TimerTask timerTask;
    private TextView setText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counterup);
        viewTimePassed = (TimePicker) findViewById(R.id.timePicker2);
        setText = (TextView) findViewById(R.id.textView2);
        startB = (Button) findViewById(R.id.button_start2);
        stopB = (Button) findViewById(R.id.button_stop2);


        viewTimePassed.clearFocus();
        viewTimePassed.setIs24HourView(true);
        viewTimePassed.setEnabled(false);
        timePassed = 0;
    }



    private void setTime(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
      setText.setText("HOUR  " + hour + "min " + min);
    }



    private int secondsToMin(int timePassed) {
        return timePassed%3600/60;
    }

    private int secondsToHour(int timePassed){
        return timePassed/3600;
    }

    public void startCountUp(View view){
           setTime();
        startB.setEnabled(false);
        stopB.setEnabled(true);
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timePassed +=1;
                        viewTimePassed.setCurrentMinute(secondsToMin(timePassed));
                        viewTimePassed.setCurrentHour(secondsToHour(timePassed));

                    }
                });
            }

        };
        timer.schedule(timerTask,0,1000);

    }
    public void stopCountUpTimer(View view) {
        if(!startB.isEnabled()) {
            timer.cancel();
            timer.purge();
            setTime();
            startB.setEnabled(true);
            stopB.setEnabled(false);
            timePassed = 0;
        }
    }
}