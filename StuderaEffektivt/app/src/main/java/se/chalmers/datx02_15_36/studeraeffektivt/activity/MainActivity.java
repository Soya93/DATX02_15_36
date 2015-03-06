package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.TabAdapter;


public class MainActivity extends ActionBarActivity {

    private HomeFrag homeFrag;
    private CalendarFrag calendarFrag;
    private TimerFrag timerFrag;
    private StatsFrag statsFrag;

    private String userName = "user_Name";
    private Drawable tabResetIcon;

    /**
     * Called when the activity is first created.
     */

    private ViewPager viewPager;
    private TabAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;
    private View view;

    // Tab titles
    private String[] tabs = {"Hem", "Kalender", "Timer", "Statistik", "Tips"};



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        Log.i("Main", viewPager.toString());

        actionBar = getSupportActionBar();


        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);


        mAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        calendarFrag = (CalendarFrag) mAdapter.getItem(1);
        calendarFrag.setContentResolver(this.getContentResolver());
        
        homeFrag = (HomeFrag) mAdapter.getItem(0);
        homeFrag.setCalendarFrag(calendarFrag);
        homeFrag.setContext(this.getApplicationContext());


        timerFrag = (TimerFrag) mAdapter.getItem(2);
        statsFrag = (StatsFrag) mAdapter.getItem(3);

        final int[] ICONS = new int[] {
            R.drawable.ic_home1_uns,
               R.drawable.ic_cal2_uns,
                R.drawable.ic_timer_uns,
                R.drawable.ic_pilegraph_uns,
                R.drawable.ic_action_overflow_uns
        };

        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());
                tabResetIcon = tab.getIcon();

                Drawable homeIcon = getResources().getDrawable(R.drawable.ic_home1_uns);
                Drawable calIcon = getResources().getDrawable(R.drawable.ic_cal2_uns);
                Drawable timerIcon = getResources().getDrawable(R.drawable.ic_timer_uns);
                Drawable graphIcon = getResources().getDrawable(R.drawable.ic_pilegraph_uns);
                Drawable menuIcon = getResources().getDrawable(R.drawable.ic_action_overflow_uns);

                //For some reason they are always false.
                if(tabResetIcon.equals(homeIcon)){
                    tab.setIcon(R.drawable.ic_home1);
                }else if(tabResetIcon.equals(calIcon)){
                    tab.setIcon(R.drawable.ic_cal2);
                }else if(tabResetIcon.equals(timerIcon)){
                    tab.setIcon(R.drawable.ic_timer);
                }else if(tabResetIcon.equals(graphIcon)){
                    tab.setIcon(R.drawable.ic_pilegraph);
                }else if (tabResetIcon.equals(menuIcon)){
                    tab.setIcon(R.drawable.ic_action_overflow);
                }else{
                    tab.setIcon(R.drawable.ic_home1);
                }

                /*switch(tabResetIcon){
                    case R.drawable.ic_home1_uns:
                        tab.setIcon(R.drawable.ic_home1);
                        break;
                    case R.drawable.ic_cal2_uns:
                        tab.setIcon(R.drawable.ic_cal2);
                        break;
                    case R.drawable.ic_timer_uns:
                        tab.setIcon(R.drawable.ic_timer);
                        break;
                    case R.drawable.ic_pilegraph_uns:
                        tab.setIcon(R.drawable.ic_pilegraph);
                        break;
                    case R.drawable.ic_action_overflow_uns:
                        tab.setIcon(R.drawable.ic_action_overflow);
                        break;
                    default:
                        break;
                }*/


            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                tab.setIcon(tabResetIcon);
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

        // Adding Tabs with the icons
        for (int tabIcon : ICONS) {
            actionBar.addTab(actionBar.newTab().setIcon(this.getResources().getDrawable(tabIcon))
                    .setTabListener(tabListener));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    /**
     * Go to TipActivity.
     * Called when the user clicks the Tip button.
     */
    /*public void goToTip(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        Intent intent = new Intent(this, TipActivity.class);
        intent.putExtra("studyType", buttonText);
        startActivity(intent);
    }*/

    public void goToCourses(View view) {
        //Tillfällig kod för testning
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
        //Slut på tillfällig kod för testning
    }

    //Calendar buttons redirections
    public void addStudySession(View view) {
        startActivity(calendarFrag.addStudySession());
    }

    public void readCalendar(View view) {
        calendarFrag.readCalendar();
    }

    public void openCalendar(View view){
        startActivity(calendarFrag.openCalendar());
    }

    public void openDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(calendarFrag.getActivity());

        //the alternatives
        String [] alternatives = {"LV1", "LV2", "LV3", "LV4", "LV5", "LV6", "LV7", "LV8"};

        builder.setTitle("Välj ett pass att repetera")
                .setItems(alternatives, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO hämta några random uppgifter
                        String tasks = "";
                        int studyWeek = which+1;
                        startActivity(calendarFrag.getCalendarModel().addEventManually(0L, 0L, true, "Repititonspass för LV" + studyWeek, null, "Repetera " +  tasks));

                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

       //Does not work properly, but looks more beautiful...
    public void openDialog2(View view) {
        calendarFrag.openDialog();

        Log.i("week", calendarFrag.getWeek() + "" );

        if (calendarFrag.getWeek() != -1) {
            startActivity(calendarFrag.addRepetition());
        }
        calendarFrag.createBuilder();
    }

}

