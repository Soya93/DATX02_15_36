package se.chalmers.datx02_15_36.studeraeffektivt.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

public class CalendarView {

    public CalendarView(){ ; }

    public void updateEventInfoView(View dialogView, CharSequence name, long startTime, long endTime, String location, String description, String calendar) {
        TextView eventNameLabel = (TextView) dialogView.findViewById(R.id.event_name_label);
        if (eventNameLabel != null) {
            eventNameLabel.setText(name);
        }

        TextView eventTimeLabel = (TextView) dialogView.findViewById(R.id.event_time_label);
        if (eventTimeLabel != null) {
            eventTimeLabel.setText("Tid: " + CalendarUtils.formatDate(startTime) + " - " + CalendarUtils.formatDate(endTime));
        }

        TextView eventLocationLabel = (TextView) dialogView.findViewById(R.id.event_location_label);
        if (eventLocationLabel != null) {
            eventLocationLabel.setText("Plats: " + location);
        }

        TextView eventDescriptionLabel = (TextView) dialogView.findViewById(R.id.event_description_label);
        if (eventDescriptionLabel != null) {
            eventDescriptionLabel.setText("Beskrivning: " + description);
        }

        TextView eventCalendarLabel = (TextView) dialogView.findViewById(R.id.event_calendar_label);
        if (eventCalendarLabel != null) {
            eventCalendarLabel.setText("Kalender: " + calendar);
        }
    }

    public AlertDialog.Builder updateDeleteInfoView(String event, Activity act){
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.event_selected_dialog, null);

        TextView eventNameLabel = (TextView) dialogView.findViewById(R.id.event_name_label);
        if (eventNameLabel != null) {
            eventNameLabel.setText("Vill du verkligen ta bort " + event + "?");
        }
        builder.setView(dialogView);

        return builder;
    }
}

