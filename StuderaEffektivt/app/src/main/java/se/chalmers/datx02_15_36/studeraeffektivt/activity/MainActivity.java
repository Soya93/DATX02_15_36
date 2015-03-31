package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.TabAdapter;


public class MainActivity extends ActionBarActivity {

    private HomeFrag homeFrag;
    private CalendarFrag calendarFrag;
    private TimerFrag timerFrag;
    private StatsFrag statsFrag;

    private String userName = "user_Name";
    private Drawable tabResetIcon;
    public static FloatingActionButton actionButton;
    public static SubActionButton button1;
    public static SubActionButton button2;
    public static SubActionButton button3;
    public static FloatingActionMenu actionMenu;
    private View.OnClickListener fabHandler;

    /**
     * Called when the activity is first created.
     */

    private ViewPager viewPager;
    private TabAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;
    private View view;

    private MyCountDownTimer serviceMCDT;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMCDT = ((MyCountDownTimer.MCDTBinder) service).getService();
            long timeFromService = serviceMCDT.returnTimePassed();
            timerFrag.handleTimeFromService(timeFromService * 1000);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMCDT = null;
        }


    };

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


        // listener for FAB menu
        FloatingActionMenu.MenuStateChangeListener myFABHandler = new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {

            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {

            }

        };

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_medal));



        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_medal));
        button1 = itemBuilder.setContentView(itemIcon1).build();
        button1.setTag(1);
        button1.setOnClickListener(fabHandler);

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_medal));
        button2 = itemBuilder.setContentView(itemIcon2).build();
        button2.setTag(2);
        button2.setOnClickListener(fabHandler);

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_medal));
        button3 = itemBuilder.setContentView(itemIcon3).build();
        button3.setTag(3);
        button3.setOnClickListener(fabHandler);

        Log.i("main: ", " button1 id:  " + button1.getId() + " button2 id : " + button2.getId()
                + " button3 id: " + button3.getId());

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button3)
                .addSubActionView(button2)
                .addSubActionView(button1)
                .attachTo(actionButton)
                .build();

        actionMenu.setStateChangeListener(myFABHandler);










        homeFrag = (HomeFrag) mAdapter.getItem(0);
        homeFrag.setCalendarFrag(calendarFrag);
        homeFrag.setContext(this.getApplicationContext());


        timerFrag = (TimerFrag) mAdapter.getItem(2);
        statsFrag = (StatsFrag) mAdapter.getItem(3);

        final int[] ICONS = new int[]{
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


                if (tab.getPosition() == 1) {
                    actionButton.setVisibility(View.VISIBLE);
                    button1.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                } else {
                    actionButton.setVisibility(View.GONE);
                    button1.setVisibility(View.GONE);
                    button2.setVisibility(View.GONE);
                    button3.setVisibility(View.GONE);
                }


                tabResetIcon = tab.getIcon();
                int position = tab.getPosition();

                switch(position){
                    case 0:
                        tab.setIcon(R.drawable.ic_home1);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_cal2);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_timer);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.ic_pilegraph);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.ic_action_overflow);
                        break;
                    default:
                        break;
                }
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

    public void startTimer(View view) {
        timerFrag.startTimer();
    }

    public void resetTimer(View view) {
        timerFrag.resetTimer();
    }

    public void settingsTimer(View view) {
        timerFrag.settingsTimer();
    }

    protected void onResume() {
        super.onResume();

        if (isMyServiceRunning(MyCountDownTimer.class)) {
            Intent i = new Intent(getBaseContext(), MyCountDownTimer.class);
            bindService(i, sc, Context.BIND_AUTO_CREATE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent i = new Intent(getBaseContext(), MyCountDownTimer.class);
                    stopService(i);
                    unbindService(sc);

                }
            }, 2000);

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    protected void onStop() {
        super.onStop();
        if (timerFrag.pauseTimerIsRunning || timerFrag.studyTimerIsRunning) {
            long timePassedToService = timerFrag.timePassed;
            long totalTime = timerFrag.default_TotalTime;

            timerFrag.cancelOneOfTimers();

            Intent i = new Intent(this, MyCountDownTimer.class);
            i.putExtra("TIME_PASSED", timePassedToService / 1000);
            i.putExtra("TOTAL_TIME", totalTime / 1000);
            startService(i);
        }
    }


}


