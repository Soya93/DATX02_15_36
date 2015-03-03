package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.TabAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;


public class MainActivity extends ActionBarActivity {

    private HomeActivity homeActivity;
    private CalendarActivity calendarActivity;
    private TimerActivity timerActivity;
    private Statistics statistics;

    private String userName = "user_Name";
    /**
     * Called when the activity is first created.
     */

    private ViewPager viewPager;
    private TabAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;
    // Tab titles
    private String[] tabs = {"Home", "Calendar", "Timer", "Statistics"};

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

        calendarActivity = (CalendarActivity) mAdapter.getItem(1);
        calendarActivity.setContentResolver(this.getContentResolver());

        homeActivity = (HomeActivity) mAdapter.getItem(0);
        homeActivity.setCalendarActivity(calendarActivity);

        timerActivity = (TimerActivity) mAdapter.getItem(2);
        statistics = (Statistics) mAdapter.getItem(3);

        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());
                if(viewPager.getCurrentItem() ==  1 && !homeActivity.hasReadToday()) {
                        homeActivity.setCalendarInfo();
                }
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

            }
        };


        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page§    §
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

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

    /**
     * Go to TipActivity.
     * Called when the user clicks the Tip button.
     */
    public void goToTip(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        Intent intent = new Intent(this, TipActivity.class);
        intent.putExtra("studyType", buttonText);
        startActivity(intent);
    }

    public void goToCourses(View view) {
        //Tillfällig kod för testning
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
        //Slut på tillfällig kod för testning
    }

    //Calendar buttons redirections
    public void addStudySession(View view) {
        startActivity(calendarActivity.addStudySession());
    }

    public void readCalendar(View view) {
        calendarActivity.readCalendar();
    }

    public void openCalendar(View view){
        startActivity(calendarActivity.openCalendar());
    }

    public void openDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(calendarActivity.getActivity());

        //the alternatives
        String [] alternatives = {"LV1", "LV2", "LV3", "LV4", "LV5", "LV6", "LV7", "LV8"};

        builder.setTitle("Välj ett pass att repetera")
                .setItems(alternatives, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO hämta några random uppgifter
                        String tasks = "";
                        int studyWeek = which+1;
                        startActivity(calendarActivity.getCalendarModel().addEventManually(0L, 0L, true, "Repititonspass för LV" + studyWeek, null, "Repetera " +  tasks));

                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

       //Does not work properly, but looks more beautiful...
    public void openDialog2(View view) {
        calendarActivity.openDialog();

        Log.i("week",calendarActivity.getWeek() + "" );

        if (calendarActivity.getWeek() != -1) {
            startActivity(calendarActivity.addRepetition());
        }
        calendarActivity.createBuilder();
    }

}

