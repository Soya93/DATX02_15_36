package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class TipsActivity extends ActionBarActivity {

    private List<Button> buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_tip);
        initComponentsList();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Studietips");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        getWindow().getDecorView().findViewById(android.R.id.content).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initComponentsList() {

        buttonList = new ArrayList<Button>();

        buttonList.add((Button) findViewById(R.id.button1));
        buttonList.add((Button) findViewById(R.id.button2));
        buttonList.add((Button) findViewById(R.id.button3));
        buttonList.add((Button) findViewById(R.id.button4));
        buttonList.add((Button) findViewById(R.id.button5));
        buttonList.add((Button) findViewById(R.id.button6));
        buttonList.add((Button) findViewById(R.id.button7));
        buttonList.add((Button) findViewById(R.id.button8));
        buttonList.add((Button) findViewById(R.id.button9));
        buttonList.add((Button) findViewById(R.id.button10));

        for (Button b : buttonList) {
            b.setOnClickListener(myOnlyhandler);
        }
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            goToTip((Button) v);
        }
    };

    /**
     * Switches to the screen where the information of the selected tip is shown.
     * Called when the user clicks the on a tip-button.
     */
    public void goToTip(Button b) {

        Fragment fragment = new TipDetailedInfoFrag();

        Bundle bundle = new Bundle();
        bundle.putString("key", (String) b.getText());

        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(getWindow().getDecorView().findViewById(android.R.id.content).getId(), fragment, "detailedtipfragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        getWindow().getDecorView().findViewById(android.R.id.content).setVisibility(View.INVISIBLE);


       /* Intent it = new Intent(this, DetailedInfoActivity.class);
        it.putExtra("key", (String) b.getText());
        Log.i("tipNameTF", (String) b.getText());
        startActivity(it);*/
    }
}
