package se.chalmers.datx02_15_36.studeraeffektivt;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.activity.TipDetailedInfoFrag;


public class TechniquesActivity extends ActionBarActivity {

    private List<Button> buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_techniques);
        initComponentsList();
    }

    private void initComponentsList() {

        buttonList = new ArrayList<>();

    buttonList.add((Button) findViewById(R.id.button11));
    buttonList.add((Button) findViewById(R.id.button12));
    buttonList.add((Button) findViewById(R.id.button13));
    buttonList.add((Button) findViewById(R.id.button14));
    buttonList.add((Button) findViewById(R.id.button15));
    buttonList.add((Button) findViewById(R.id.button16));
    buttonList.add((Button) findViewById(R.id.button17));
    buttonList.add((Button) findViewById(R.id.button18));

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

        Intent it = new Intent(this, DetailedInfoActivity.class);
        it.putExtra("key", (String) b.getText());
        Log.i("tipNameTF", (String) b.getText());
        startActivity(it);
    }
}
