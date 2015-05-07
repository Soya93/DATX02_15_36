package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;

public class RepetitionActivity extends ActionBarActivity {

    private int selectedWeek;
    private String selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repetition);
        Spinner weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
        Spinner courseSpinner = (Spinner) findViewById(R.id.courseSpinner);


        final String[] alternatives = new String[7];
        int currentWeek = Utils.getCurrWeekNumber();
        for (int i = 7, j = 0; i >= 2 && j < alternatives.length; i--, j++) {
            int newWeek = currentWeek - i;
            Log.i("newWeek", newWeek + "");
            alternatives[j] = "Vecka " + newWeek;
        }

        DBAdapter dbAdapter = new DBAdapter(this);

        Cursor cursor = dbAdapter.getCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        final int ccodeColumn = cursor.getColumnIndex("_ccode");
        while (cursor.moveToNext()) {
            String ccode = cursor.getString(ccodeColumn);
            String cname = cursor.getString(cnameColumn);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, alternatives);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Log.i("calendarFrag", weekSpinner.toString());
        Log.i("calendarFrag", adapter.toString());
        weekSpinner.setAdapter(adapter);



        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,ccodeColumn);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        courseSpinner.setAdapter(adapter2);

        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){

            }
            public void onNothingSelected(AdapterView<?> parent){

            }
        });
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){

            }
            public void onNothingSelected(AdapterView<?> parent){

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_repetition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}