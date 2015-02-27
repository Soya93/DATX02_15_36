package se.chalmers.datx02_15_36.studeraeffektivt.activity;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import se.chalmers.datx02_15_36.studeraeffektivt.CounterUpTimer;
import se.chalmers.datx02_15_36.studeraeffektivt.CourseActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.TabAdapter;


public class MainActivity extends ActionBarActivity {

    private String userName = "user_Name";
    /** Called when the activity is first created. */

    private ViewPager viewPager;
    private TabAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Home", "Calendar", "Timer", "Statistics" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        Log.i("Main", viewPager.toString());

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        mAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

            }
        };

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(tabListener));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }



    /** Go to TimerActivity.
     * Called when the user clicks the Timer button. */
    public void goToTimer(View view) {
        Intent intent = new Intent(this, TimerActivity.class);
        intent.putExtra(userName,"Alex");
        startActivity(intent);
    }

    /** Go to CalendarActivity.
     * Called when the user clicks the Calendar button. */
    public void goToCalendar(View view) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }
    public void goToCountUp(View view){
        Intent intent = new Intent(this, CounterUpTimer.class);
        startActivity(intent);
    }

    /** Go to TipActivity.
     * Called when the user clicks the Tip button.  */
    public void goToTip(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        Intent intent = new Intent(this, TipActivity.class);
        intent.putExtra("studyType",buttonText);
        startActivity(intent);
    }

    public void goToCourses(View view){
        //Tillfällig kod för testning
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
        //Slut på tillfällig kod för testning
    }
}
