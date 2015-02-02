package se.chalmers.datx02_15_36.studeraeffektivt;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class TimerActivity extends ActionBarActivity {

    private CountDownTimer cdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        cdt = getTimer(10000, 1000);

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
                TextView textView = (TextView) findViewById(R.id.text_timer);
                textView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }
        }

    }

    /**
     * Start the timer.
     * Called when the user clicks the Start Timer button.
     */
    public void startTimer(View view) {
        cdt.start();
    }

    /**
     * Stop the timer.
     */


}
