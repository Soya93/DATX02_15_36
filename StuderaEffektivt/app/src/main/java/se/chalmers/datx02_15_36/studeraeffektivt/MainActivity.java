package se.chalmers.datx02_15_36.studeraeffektivt;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;


public class MainActivity extends ActionBarActivity {
    private String userName = "user_Name";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /** Go to TimerActivity.
     * Called when the user clicks the Timer button. */
    public void goToTimer(View view) {
        Intent intent = new Intent(this, TimerActivity.class);
        intent.putExtra(userName,"Alex");
        startActivity(intent);
    }

   public void goToCountUp(View view){
       Intent intent = new Intent(this, CounterUpTimer.class);
       startActivity(intent);
   }


}
