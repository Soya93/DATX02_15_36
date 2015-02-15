package se.chalmers.datx02_15_36.studeraeffektivt;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;




public class TimerActivity extends ActionBarActivity {

    private CountDownTimer cdt;
    private TimePicker t1;
    private Button startButton;
    private Button pauseButton;
    private long seconds;
    private boolean isRunning=false;
    private long totalSeconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        t1 = (TimePicker) findViewById(R.id.timePicker);
        pauseButton=(Button) findViewById(R.id.button_pause);
        startButton = (Button) findViewById(R.id.button_start_timer);
        t1.setIs24HourView(true);
        t1.clearFocus();
           setTime();

        getTimer(5000, 100);


    }
    private void setTime(){
        t1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
                                        @Override
                                        public void onTimeChanged(    TimePicker view,    int hourOfDay,    int minute){
                                            seconds=minute*60+hourOfDay*3600;
                                            getTimer(seconds* 1000, 100);

                                        }
                                    }
        );

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
    public CountDownTimer getTimer(long millisInFuture, long countDownInterval){

        cdt = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                if(isRunning) {
                    TextView textView = (TextView) findViewById(R.id.text_timer);
                    textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                    int hour = (int) (millisUntilFinished / 1000 / 3600);
                    int minute = ((int) ((millisUntilFinished / 1000 % 3600)) + 60) / 60;
                    t1.setCurrentHour(hour);
                    t1.setCurrentMinute(minute);
                    seconds = millisUntilFinished;
                    totalSeconds+=1;
                }
            }

            @Override
            public void onFinish() {
               setTime();
               startButton.setEnabled(true);
               isRunning=false;
            }
        };
        return cdt;

    }

    /**
     * Start the timer.
     * Called when the user clicks the Start Timer button.
     */
    public void startTimer(View view) {
        if(!pauseButton.isEnabled()&& startButton.isEnabled()){
            getTimer(seconds,100);
            cdt.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }
        else if (startButton.isEnabled()&& pauseButton.isEnabled()){
            isRunning=true;
            cdt.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }
    }

    public void pauseTimer(View view) {
        cdt.cancel();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    /**
     * Stop the timer.
     */


}
