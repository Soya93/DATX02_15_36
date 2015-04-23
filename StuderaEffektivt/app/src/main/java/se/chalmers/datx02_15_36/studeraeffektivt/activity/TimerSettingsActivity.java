package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * Created by alexandraback on 21/04/15.
 */
public class TimerSettingsActivity extends ActionBarActivity {
    private ExpandableListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timersettings);
        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        String[] listValues = new String[]{
                "Antal repetitioner", "Pluggfas", "Vilofas"
        };

        /*
        ExpandableListAdapter adapter = new ExpandableListAdapter(this,listValues);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert

            }
        });*/

    }
}