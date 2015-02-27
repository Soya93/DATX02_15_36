package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarActivity extends Activity {

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
                OpenRepetitionDialog(v);
            }
        });
    }

    public void readCalendar(View view) {
        calendarModel.readEvents(cr, 0L, 0L);
    }

    public void addStudySession(View view){
        startActivity(calendarModel.addEventManually(0L, 0L, false, "Studiepass", null, null));
    }

    public void addEventAuto(View view) {
        //calendarModel.getCalendars(cr, "sayo.panda.sn@gmail.com", "com.google");
        calendarModel.getCalendars(cr, "eewestman@gmail.com", "com.google");

        TextView textView = (TextView) findViewById(R.id.calendar_text);
        Long eventID = this.calendarModel.addEventAuto(cr);
        textView.setText(eventID +"");
    }

    public void OpenRepetitionDialog(View view) {
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
    public void openCalendar(View view){
        startActivity(calendarModel.openCalendar());
    }


}
