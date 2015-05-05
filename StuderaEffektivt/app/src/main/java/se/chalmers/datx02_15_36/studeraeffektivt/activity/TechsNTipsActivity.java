package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.TechniquesFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.TipFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;

public class TechsNTipsActivity extends ActionBarActivity {

    android.support.v7.app.ActionBar actionBar;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.techsntipsactivity);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        title = getIntent().getStringExtra("ActivityTitle");
        actionBar.setTitle(title);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.primaryColor)));
        initFrag(getIntent().getStringExtra("ActivityTitle"));
    }

    private void initFrag(String type){
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(type.equals("Studietips")){
            fragment = new TipFrag();
        } else {
            fragment = new TechniquesFrag();
        }
        fragmentTransaction.add(getWindow().getDecorView().findViewById(android.R.id.content).getId(), fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("In resume", "prevactionbartitle " +  getSupportActionBar().getTitle());
        getSupportActionBar().setTitle(title);
        Log.i("In resume", "title " + title);
        Log.i("In resume", "nextactionbartitle " +  getSupportActionBar().getTitle());


    }
}
