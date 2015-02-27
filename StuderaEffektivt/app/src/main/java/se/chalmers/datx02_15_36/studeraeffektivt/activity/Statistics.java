package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

public class Statistics extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD:StuderaEffektivt/app/src/main/java/se/chalmers/datx02_15_36/studeraeffektivt/activity/Statistics.java
        setContentView(R.layout.activity_statistics);
=======
        setContentView(R.layout.activity_main);
        //Tillfällig kod för testning
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
        //Slut på tillfällig kod för testning
>>>>>>> CourseAndClass:StuderaEffektivt/app/src/main/java/se/chalmers/datx02_15_36/studeraeffektivt/MainActivity.java
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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
}
