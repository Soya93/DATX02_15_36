package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import se.chalmers.datx02_15_36.studeraeffektivt.fragment.CalendarFrag;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;

public class RepetitionActivity extends ActionBarActivity {

    private String selectedWeek;
    private String selectedCourse;
    private CalendarFrag calendarFrag;
    private CalendarModel calendarModel;
    private Spinner weekSpinner;
    private Spinner courseSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repetition);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Repetition");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.primaryColor)));
        weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);

        calendarModel = new CalendarModel();
        calendarModel.getCalendarInfo(getContentResolver());

        final String[] alternatives = new String[7];
        int currentWeek = Utils.getCurrWeekNumber();


        DBAdapter dbAdapter = new DBAdapter(this);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter2);
        Cursor cursor = dbAdapter.getCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");
        while (cursor.moveToNext()) {
            String ccode = cursor.getString(ccodeColumn);
            String cname = cursor.getString(cnameColumn);
            adapter2.add(ccode + " " + cname);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Log.i("calendarFrag", weekSpinner.toString());
        Log.i("calendarFrag", adapter.toString());
        weekSpinner.setAdapter(adapter);
        for (int i = 7, j = 0; i >= 2 && j < alternatives.length; i--, j++) {
            int newWeek = currentWeek - i;
            Log.i("newWeek", newWeek + "");
            adapter.add("Vecka " + newWeek);
            alternatives[j] = "Vecka " + newWeek;
        }
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
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }else{
            selectedCourse = courseSpinner.getSelectedItem().toString();
            selectedWeek = weekSpinner.getSelectedItem().toString().toLowerCase();

            EventActivity eventActivity = new EventActivity();
           // eventActivity.setCalendarFrag(this);
            Intent intent = new Intent(this, eventActivity.getClass());
            intent.putExtra("isInAddMode", true);
            intent.putExtra("startTime", 0L);
            intent.putExtra("endTime", 0L);
            intent.putExtra("title", "Repetitonspass för " + selectedWeek + " " + selectedCourse);
            SharedPreferences sharedPref = this.getSharedPreferences("calendarPref", Context.MODE_PRIVATE);
            Long homeCalID = sharedPref.getLong("homeCalID", 1L); // 1 is some value if it fails to read??
            intent.putExtra("calID", homeCalID);        // is the home calendar
            intent.putExtra("calName", calendarModel.getCalendarsMap().get(homeCalID));     //get name of the home calendar
            intent.putExtra("color", calendarModel.getCalIdAndColorMap().get(homeCalID));
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

}