package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CalendarFrag;
//import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CourseDetailedInfoFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.HomeFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.StatsFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.TimerFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.model.TabAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;


public class MainActivity extends ActionBarActivity {

    private HomeFrag homeFrag;
    private CalendarFrag calendarFrag;
    private TimerFrag timerFrag;
    private Drawable tabResetIcon;
    public static FloatingActionButton actionButton;
    public static SubActionButton button1;
    public static SubActionButton button2;
    public static SubActionButton button3;
    public static SubActionButton button4;
    private View.OnClickListener fabHandler;
    private Drawable primaryColorDrawable;

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

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
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
        Drawable statsUns = getResources().getDrawable( R.drawable.ic_pilegraph).mutate();
        statsUns.setColorFilter(Color.parseColor(Constants.secondaryColor), PorterDuff.Mode.SRC_ATOP);
        Drawable myProfileUns = getResources().getDrawable(R.drawable.ic_action).mutate();
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
                        tab.setIcon( getResources().getDrawable( R.drawable.ic_pilegraph).mutate());
                        break;
                    case 4:
                        tab.setIcon(getResources().getDrawable( R.drawable.ic_action).mutate());
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


}


