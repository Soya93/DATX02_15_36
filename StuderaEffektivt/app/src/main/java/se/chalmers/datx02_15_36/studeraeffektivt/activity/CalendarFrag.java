package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;

/**
 * A class representing the controller of a calendar object
 */
public class CalendarFrag extends Fragment {

    CalendarModel calendarModel = new CalendarModel();
    ContentResolver cr;
    View view;
    int week = -1;
    Button studySession;
    Button repetition;
    Button calendar;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       view = inflater.inflate(R.layout.activity_calendar, container, false);
       calendarModel = new CalendarModel();
       initComponents();
       return view;
    }

    private void initComponents() {
        View.OnClickListener myOnlyhandler = new View.OnClickListener() {
            public void onClick(View v) {

                goToButtonView((Button) v);

            }
        };
        studySession = (Button) view.findViewById(R.id.button_add_study_session);
        studySession.setOnClickListener(myOnlyhandler);
        repetition = (Button) view.findViewById(R.id.button_add_repetition_session);
        repetition.setOnClickListener(myOnlyhandler);
        calendar = (Button) view.findViewById(R.id.button_open_calendar);
        calendar.setOnClickListener(myOnlyhandler);
    }

    private void goToButtonView(Button b) {

        String buttonText = b.getText().toString();
        switch (buttonText) {
            case "Lägg till studiepass":
                this.addStudySession();
                break;
            case "Lägg till repetitionspass":
                this.addRepetitionSession();
                break;
            case "Öppna kalendern":
                this.openCalendar();
                break;

        }
    }

    private void addStudySession(){
        this.getActivity().startActivity(calendarModel.addEventManually(0L, 0L, false, "Studiepass", null, null));
    }

    private void openCalendar(){
        this.getActivity().startActivity(calendarModel.openCalendar());
    }

    private void addRepetitionSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //the alternatives
        String[] alternatives = {"LV1", "LV2", "LV3", "LV4", "LV5", "LV6", "LV7", "LV8"};

        builder.setTitle("Välj ett pass att repetera")
                .setItems(alternatives, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO hämta några random uppgifter
                        String tasks = "";
                        int studyWeek = which + 1;
                        startActivity(calendarModel.addEventManually(0L, 0L, true, "Repititonspass för LV" + studyWeek, null, "Repetera " + tasks));

                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public List<String> getTodaysEvents() {
        return calendarModel.readEventsToday(cr);
    }

    public List<String> getSundaysEvents() {
        return calendarModel.readEventsSunday(cr);
    }

    public void setContentResolver(ContentResolver cr){
        this.cr = cr;
    }
}
