package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarActivity extends ActionBarActivity {

    CalendarModel calendarModel;
    ContentResolver cr;
    Button repButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarModel = new CalendarModel();
        cr = getContentResolver();

        repButton = (Button) findViewById(R.id.button_add_repetition_session);
        repButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(v);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
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


    public void readCalendar(View view) {
        calendarModel.readEvents(cr, 0L, 0L);
    }

    public void addStudySession(View view){
        startActivity(calendarModel.addEventManually(0L, 0L, false, "Studiepass", null, null));
    }

    public void addRepetitionSession(View view){


        //startActivity(calendarModel.addEventManually(0L, 0L, true, "Repititonspass", null, "Repetera " +  repitition));
    }

    public void addEventAuto(View view) {
        //calendarModel.getCalendars(cr, "sayo.panda.sn@gmail.com", "com.google");
        calendarModel.getCalendars(cr, "eewestman@gmail.com", "com.google");

        TextView textView = (TextView) findViewById(R.id.calendar_text);
        Long eventID = this.calendarModel.addEventAuto(cr);
        textView.setText(eventID +"");
    }


    public void openDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);

        //the alternatives
        String [] alternatives = {"LV1", "LV2", "LV3", "LV4", "LV5", "LV6", "LV7", "LV8"};

        builder.setTitle("Välj ett pass att repetera")
                .setItems(alternatives, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO hämta några random uppgifter
                        String tasks = "";
                        int studyWeek = which+1;
                        startActivity(calendarModel.addEventManually(0L, 0L, true, "Repititonspass för LV" + studyWeek, null, "Repetera " +  tasks));

                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

}
