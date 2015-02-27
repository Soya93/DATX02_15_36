package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarActivity extends Fragment {

    CalendarModel calendarModel;
    ContentResolver cr;
    Button repButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_calendar, container, false);

        cr = getActivity().getContentResolver();

        repButton = (Button)rootView.findViewById(R.id.button_add_repetition_session);
      /*  repButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(v);
            }
        });*/
        return rootView;
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

        TextView textView = (TextView) getView().findViewById(R.id.calendar_text);
        Long eventID = this.calendarModel.addEventAuto(cr);
        textView.setText(eventID +"");
    }


    public void openDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
