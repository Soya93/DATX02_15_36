package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import java.util.ArrayList;
import java.util.List;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CalendarFrag;
//import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CourseDetailedInfoFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.HomeFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.StatsFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.TimerFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.adapter.TabAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;
import se.chalmers.datx02_15_36.studeraeffektivt.util.NotificationBroadcastReceiver;
import se.chalmers.datx02_15_36.studeraeffektivt.util.NotificationReceiver;
import se.chalmers.datx02_15_36.studeraeffektivt.util.NotificationService;
import se.chalmers.datx02_15_36.studeraeffektivt.util.RepetitionReminder;


public class MainActivity extends ActionBarActivity {

    private HomeFrag homeFrag;
    private CalendarFrag calendarFrag;
    private StatsFrag statsFrag;
    private TimerFrag timerFrag;
    private Drawable tabResetIcon;
    public static FloatingActionButton actionButton;
    public static SubActionButton button1;
    public static SubActionButton button2;
    public static SubActionButton button3;
    public static SubActionButton button4;
    public static FloatingActionMenu actionMenu;
    private View.OnClickListener fabHandler;
    private Drawable primaryColorDrawable;
    public NotificationBroadcastReceiver testReceiver;


    /**
     * Called when the activity is first created.
     */

    private ViewPager viewPager;
    private TabAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        primaryColorDrawable = new ColorDrawable(Color.parseColor(Constants.primaryColor));
        actionBar.setBackgroundDrawable(primaryColorDrawable);
        actionBar.setSplitBackgroundDrawable(primaryColorDrawable);
        actionBar.setStackedBackgroundDrawable(primaryColorDrawable);

        mAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        calendarFrag = (CalendarFrag) mAdapter.getItem(1);
        calendarFrag.setContentResolver(this.getContentResolver());

        homeFrag = (HomeFrag) mAdapter.getItem(0);
        homeFrag.setContentResolver(this.getContentResolver());
        homeFrag.setCalendarFrag(calendarFrag);

        statsFrag = (StatsFrag) mAdapter.getItem(3);

        // listener for FAB menu
        FloatingActionMenu.MenuStateChangeListener myFABHandler = new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
            }
        };

        //Setting the buttons for the calendar menu
        ImageView icon = new ImageView(this); // Create an icon
        Drawable moreIcon = getResources().getDrawable( R.drawable.ic_navigation_more_vert).mutate();
        moreIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP);
        icon.setImageDrawable(moreIcon);


        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView itemIcon1 = new ImageView(this);
        Drawable plusIcon = getResources().getDrawable( R.drawable.ic_content_add).mutate();
        plusIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        itemIcon1.setImageDrawable(plusIcon);
        button1 = itemBuilder.setContentView(itemIcon1).build();
        button1.setTag(1);
        button1.setOnClickListener(fabHandler);

        ImageView itemIcon2 = new ImageView(this);
        Drawable repeatIcon = getResources().getDrawable( R.drawable.ic_av_loop).mutate();
        repeatIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        itemIcon2.setImageDrawable(repeatIcon);
        button2 = itemBuilder.setContentView(itemIcon2).build();
        button2.setTag(2);
        button2.setOnClickListener(fabHandler);

        ImageView itemIcon3 = new ImageView(this);
        Drawable nbrOfVisibleDaysIcon = getResources().getDrawable( R.drawable.ic_image_filter).mutate();
        nbrOfVisibleDaysIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        itemIcon3.setImageDrawable(nbrOfVisibleDaysIcon);
        button3 = itemBuilder.setContentView(itemIcon3).build();
        button3.setTag(3);
        button3.setOnClickListener(fabHandler);

        ImageView itemIcon4 = new ImageView(this);
        Drawable calendarsIcon = getResources().getDrawable( R.drawable.ic_cal2).mutate();
        calendarsIcon.setColorFilter(Color.parseColor(Constants.primaryColor), PorterDuff.Mode.SRC_ATOP); //Set color to a drawable from hexcode!
        itemIcon4.setImageDrawable(calendarsIcon);
        button4 = itemBuilder.setContentView(itemIcon4).build();
        button4.setTag(4);
        button4.setOnClickListener(fabHandler);

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button4)
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

        //Sets the unselected tab icons to a lighter blue color
        Drawable homeUns = getResources().getDrawable( R.drawable.ic_home1).mutate();
        homeUns.setColorFilter(Color.parseColor(Constants.secondaryColor), PorterDuff.Mode.SRC_ATOP);
        Drawable calUns = getResources().getDrawable( R.drawable.ic_cal2).mutate();
        calUns.setColorFilter(Color.parseColor(Constants.secondaryColor), PorterDuff.Mode.SRC_ATOP);
        Drawable timerUns = getResources().getDrawable( R.drawable.ic_timer).mutate();
        timerUns.setColorFilter(Color.parseColor(Constants.secondaryColor), PorterDuff.Mode.SRC_ATOP);
        Drawable statsUns = getResources().getDrawable( R.drawable.ic_action_trending_up).mutate();
        statsUns.setColorFilter(Color.parseColor(Constants.secondaryColor), PorterDuff.Mode.SRC_ATOP);
        Drawable myProfileUns = getResources().getDrawable(R.drawable.ic_examhat).mutate();
        myProfileUns.setColorFilter(Color.parseColor(Constants.secondaryColor), PorterDuff.Mode.SRC_ATOP);


        final Drawable[] ICONS = new Drawable[]{
                homeUns,
                calUns,
                timerUns,
                statsUns,
                myProfileUns

        };


        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());


                if(tab.getPosition() == 0) {
                    actionButton.setVisibility(View.GONE);
                    button1.setVisibility(View.GONE);
                    button2.setVisibility(View.GONE);
                    button3.setVisibility(View.GONE);
                    button4.setVisibility(View.GONE);
                    //homeActionButton.setVisibility(View.VISIBLE);
                }else if (tab.getPosition() == 1) {
                    actionButton.setVisibility(View.VISIBLE);
                    button1.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                    button4.setVisibility(View.VISIBLE);
                    //homeActionButton.setVisibility(View.GONE);
                } else {
                    actionButton.setVisibility(View.GONE);
                    button1.setVisibility(View.GONE);
                    button2.setVisibility(View.GONE);
                    button3.setVisibility(View.GONE);
                    button4.setVisibility(View.GONE);
                }


                tabResetIcon = tab.getIcon();

                int position = tab.getPosition();


                switch(position){
                    case 0:
                        tab.setIcon(getResources().getDrawable( R.drawable.ic_home1).mutate());
                        break;
                    case 1:
                        tab.setIcon(getResources().getDrawable( R.drawable.ic_cal2).mutate());
                        break;
                    case 2:
                        tab.setIcon(getResources().getDrawable( R.drawable.ic_timer).mutate());
                        break;
                    case 3:
                        tab.setIcon(getResources().getDrawable( R.drawable.ic_action_trending_up).mutate());
                        break;
                    case 4:
                        tab.setIcon(getResources().getDrawable( R.drawable.ic_examhat).mutate());
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

                //When home page is selected
                if(position == 0) {
                    homeFrag.setTodaysEvents();
                }
                if((position == 3) && statsFrag.hasInit()) {
                    statsFrag.updateView();
                }
                if((position == 1) && calendarFrag.hasInit()) {
                    calendarFrag.updateView();
                }
                if((position == 2) && timerFrag.hasInit()) {
                    timerFrag.updateView();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        // Adding Tabs with the icons
        for (Drawable tabIcon : ICONS) {
            actionBar.addTab(actionBar.newTab().setIcon(tabIcon)
                    .setTabListener(tabListener));
        }

        //Didnt work T_T
        if(savedInstanceState!=null) {
            savedInstanceState.getBoolean("goToTimer", false);
            if (savedInstanceState.getBoolean("goToTimer", false)) {
                actionBar.setSelectedNavigationItem(2);
            }
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


    /*Notification related stuff begins here */

    public void onlyWhenInAppReminder() {
        // if(Utils.getCurrWeekNumber() == Calendar.MONDAY && Utils.getHourNow() >= 8){
        RepetitionReminder repetitionReminder = new RepetitionReminder();
        repetitionReminder.setDBAdapter(new DBAdapter(this));

        List<String> message = repetitionReminder.reminderMessage();

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplication());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplication())
                        .setSmallIcon(R.drawable.ic_study_coach)
                        .setContentTitle("StudieCoach")
                        .setContentText("Ny vecka")
                        .setSubText(message.get(0));

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    //}

    }


    public void TimeToRemind(){
       // if(Utils.getCurrWeekNumber() == Calendar.MONDAY && Utils.getHourNow() >= 8){
            RepetitionReminder repetitionReminder = new RepetitionReminder();
            repetitionReminder.setDBAdapter(new DBAdapter(this));

            List<String> message = repetitionReminder.reminderMessage();

            //Notificationstuff
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplication());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(R.drawable.ic_study_coach)
                .setContentTitle("StudieCoach")
                .setContentText("Ny studievecka");
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        if(!message.isEmpty()){
            for(String text: message)
            inboxStyle.addLine(text);
        }

        if(repetitionReminder.hasCourses()) {
            /*String[] courses = new String[repetitionReminder.getCoursesToRepeat().size()];
            int i = 0;
            for (String course : repetitionReminder.getCoursesToRepeat()) {
                courses[i] = course;
                i++;
            }
              for (int j=0; i < courses.length; j++) {
                inboxStyle.addLine(courses[j]);
            }
            */

            mBuilder.addAction(R.drawable.ic_av_loop, "Ja", resultPendingIntent);
            Bundle bundle = new Bundle();
            bundle.putBoolean("Go_To_Cal", true);
            resultIntent.putExtras(bundle);
            //resultIntent.setAction("Go_To_Cal");

            inboxStyle.addLine("Vill du skapa ett repetitionspass?");
            Cursor courses = repetitionReminder.getCourses();

            while (courses.moveToNext()) {
                Log.i("courses", courses.getString(courses.getColumnIndex("_ccode")));
                inboxStyle.addLine(courses.getString(courses.getColumnIndex("_ccode")));
            }


        } else {
            //resultIntent.setAction("Go_To_Main");
            mBuilder.setStyle(inboxStyle);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    // Launching the service
    public void onStartService() {
        testReceiver = new NotificationBroadcastReceiver();
        // if(Utils.getCurrWeekNumber() == Calendar.MONDAY && Utils.getHourNow() >= 8){
        RepetitionReminder repetitionReminder = new RepetitionReminder();
        repetitionReminder.setDBAdapter(new DBAdapter(this));

        ArrayList<String> message = repetitionReminder.reminderMessage();
        Intent i = new Intent(this, NotificationService.class);

        if(repetitionReminder.hasCourses()){
            i.putExtra("numOfCourses", repetitionReminder.getCoursesToRepeat().size());

            ArrayList<String> courses = repetitionReminder.getCoursesToRepeat();
            i.putStringArrayListExtra("courses", courses);
        }

        //i.putExtra("foo", "bar");
        i.putStringArrayListExtra("messages", message);
        i.putExtra("hascourses", repetitionReminder.hasCourses());
        startService(i);
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(NotificationService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }

    private void setNotificationAlarm()
    {
        Intent intent = new Intent(getApplicationContext() , NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 , pendingIntent);
        Log.d("ME", "Alarm started");
    }

    public void startAlert() {
        Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (2 * 1000), pendingIntent);
        Toast.makeText(this, "Alarm set in " + 2 + " seconds",
                Toast.LENGTH_LONG).show();
    }
}



