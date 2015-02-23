package se.chalmers.datx02_15_36.studeraeffektivt.activity;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class TimerActivity extends ActionBarActivity {
    private String userName;
    private CountDownTimer cdt;
    private TimePicker t1;
    private Button startButton;
    private Button resetButton;
    private long chosenSeconds;
    private long seconds;
    private long timePassed;
   private DbAccess dbAccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = getIntent().getStringExtra("user_Name");
        setContentView(R.layout.activity_timer);
        instantiate();
        t1.setIs24HourView(true);
        t1.clearFocus();
        setTime();
        getTimer(5000, 100);
        DbAccess dbAccess = new DbAccess(this);




    }

    private void setTime() {
        t1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                                        @Override
                                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                                            chosenSeconds = minute * 60 + hourOfDay * 3600;
                                            getTimer(chosenSeconds * 1000, 100);

                                        }
                                    }
        );

    }

    private void instantiate() {
        t1 = (TimePicker) findViewById(R.id.timePicker);
        resetButton = (Button) findViewById(R.id.button_reset);
        startButton = (Button) findViewById(R.id.button_start_timer);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Set the timer.
     */
    public CountDownTimer getTimer(long millisInFuture, long countDownInterval) {

        cdt = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {

                TextView textView = (TextView) findViewById(R.id.text_timer);
                textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                setTimePicker(millisUntilFinished);

                seconds = millisUntilFinished;
                timePassed += 100;
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

            @Override
            public void onFinish() {
                t1.setEnabled(true);
                setTime();
                startButton.setText("Start");
                startButton.setEnabled(true);
                //dbAccess.insertValue("Alex",Long.toString(timePassed));

            }
        };
        return cdt;

    }

    /**
     * Start the timer.
     * Called when the user clicks the Start Timer button.
     */
    public void startTimer(View view) {
        t1.setEnabled(false);
        if (startButton.getText().equals("Pause")) {
            startButton.setText("Start");
            cdt.cancel();
            getTimer(seconds, 100);
        } else if (startButton.getText().equals("Start")) {
            cdt.start();
            startButton.setText("Pause");
            resetButton.setEnabled(true);

        }
    }
    /*
    If you hit pause and then reset button and then not choose another time. Timer will count down
    the seconds that are left.
    */

    public void resetTimer(View view) {
        cdt.cancel();
        startButton.setText("Start");
        startButton.setEnabled(true);
        resetButton.setEnabled(false);
        t1.setEnabled(true);
        setTime();
    }




}
