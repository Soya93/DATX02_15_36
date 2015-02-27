package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.TabHost;
import android.app.TabActivity;
import android.widget.TabHost.OnTabChangeListener;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class MainActivity extends TabActivity implements OnTabChangeListener{
    private String userName = "user_Name";

    /** Called when the activity is first created. */
    TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Get TabHost Refference
        tabHost = getTabHost();

        // Set TabChangeListener called when tab changed
        tabHost.setOnTabChangedListener(this);

        TabHost.TabSpec spec;
        Intent intent;

        /************* TAB1 ************/
        // Create  Intents to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, HomeActivity.class);
        spec = tabHost.newTabSpec("First").setIndicator("")
                .setContent(intent);

        //Add intent to tab
        tabHost.addTab(spec);

        /************* TAB2 ************/
        intent = new Intent().setClass(this, CalendarActivity.class);
        spec = tabHost.newTabSpec("Second").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        /************* TAB3 ************/
        intent = new Intent().setClass(this, TimerActivity.class);
        intent.putExtra(userName,"Alex");
        spec = tabHost.newTabSpec("Third").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        /************* TAB4 ************/
        intent = new Intent().setClass(this, Statistics.class);
        spec = tabHost.newTabSpec("Forth").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        // Set drawable images to tab
        //tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tab2);
        //tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.tab3);

        // Set Tab1 as Default tab and change image
        tabHost.getTabWidget().setCurrentTab(0);
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.MAGENTA);


    }

    @Override
    public void onTabChanged(String tabId) {

        /************ Called when tab changed *************/

        //********* Check current selected tab and change colours *******/

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            if(i==0)
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.CYAN);
            else if(i==1)
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.CYAN);
            else if(i==2)
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.CYAN);
            else if(i==3)
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.CYAN);
        }


        Log.i("tabs", "CurrentTab: "+tabHost.getCurrentTab());

        if(tabHost.getCurrentTab()==0)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.MAGENTA);
        else if(tabHost.getCurrentTab()==1)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.MAGENTA);
        else if(tabHost.getCurrentTab()==2)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.MAGENTA);
        else if(tabHost.getCurrentTab()==3)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.MAGENTA);
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
