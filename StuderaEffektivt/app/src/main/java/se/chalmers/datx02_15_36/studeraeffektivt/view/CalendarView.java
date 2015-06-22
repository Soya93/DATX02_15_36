/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

package se.chalmers.datx02_15_36.studeraeffektivt.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;

public class CalendarView {

    public CalendarView(){ ; }

    public void updateEventInfoView(View dialogView, CharSequence name, long startTime, long endTime, String location, String description, String calendar, int isAllDay, int notificationTime) {
        TextView eventNameLabel = (TextView) dialogView.findViewById(R.id.event_name_label);
        if (eventNameLabel != null && !name.toString().isEmpty()) {
            eventNameLabel.setVisibility(View.VISIBLE);
            eventNameLabel.setText(name);
        } else if (name.toString().isEmpty()){
            eventNameLabel.setText("Namnlös händelse");
        }

        TextView eventTimeLabel = (TextView) dialogView.findViewById(R.id.event_time_label);
        if (eventTimeLabel != null && isAllDay == 0) {
            eventTimeLabel.setText("Tid: " + formatDate(startTime, endTime));
        } else if (location.isEmpty()){
            eventTimeLabel.setText("Heldagsevent");
        }

        TextView eventLocationLabel = (TextView) dialogView.findViewById(R.id.event_location_label);
        if (eventLocationLabel != null && !location.isEmpty()) {
            eventLocationLabel.setVisibility(View.VISIBLE);
            eventLocationLabel.setText("Plats: " + location);
        } else if (location.isEmpty()){
            eventLocationLabel.setVisibility(View.GONE);
        }

        TextView eventDescriptionLabel = (TextView) dialogView.findViewById(R.id.event_description_label);
        if (eventDescriptionLabel != null && !description.isEmpty() ) {
            eventDescriptionLabel.setVisibility(View.VISIBLE);
            eventDescriptionLabel.setText("Beskrivning: " + description);
        } else if (description.isEmpty()){
            eventDescriptionLabel.setVisibility(View.GONE);
        }

        TextView eventCalendarLabel = (TextView) dialogView.findViewById(R.id.event_calendar_label);
        if (eventCalendarLabel != null && !calendar.isEmpty()) {
            eventCalendarLabel.setVisibility(View.VISIBLE);
            eventCalendarLabel.setText("Kalender: " + calendar);
        } else if (calendar.isEmpty()){
            eventCalendarLabel.setVisibility(View.GONE);
        }

        TextView notificationLabel = (TextView) dialogView.findViewById(R.id.event_notification_label);
        if (notificationLabel != null) {
            notificationLabel.setVisibility(View.VISIBLE);
            notificationLabel.setText("Påminnelse: " + notificationTimeToString(notificationTime));
        }
    }

    public AlertDialog.Builder updateDeleteInfoView(String event, Activity act){
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.confirm_dialog, null);

        TextView eventNameLabel = (TextView) dialogView.findViewById(R.id.confirm_text);
        if (eventNameLabel != null) {
            eventNameLabel.setText("Vill du verkligen ta bort " + event + "?");
        }
        builder.setView(dialogView);

        return builder;
    }

    public static String formatDate(long startDateInMillis, long endDateInMillis){
        SimpleDateFormat startFormat = new SimpleDateFormat("EEEE d MMMM, HH:mm");
        SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm");
        return startFormat.format(new Date(startDateInMillis)) + "-" + endFormat.format(new Date(endDateInMillis));
    }

    public static String formatTime(long startDateInMillis, long endDateInMillis){
        SimpleDateFormat startFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm");
        return startFormat.format(new Date(startDateInMillis)) + "-" + endFormat.format(new Date(endDateInMillis));
    }

    public static String formatTimeToEvent(long diff){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(diff);

        if(cal.get(Calendar.HOUR) >= 1) {
            SimpleDateFormat hours = new SimpleDateFormat("H");
            return hours.format(new Date(diff)) + "h";
        }else {
            SimpleDateFormat mins = new SimpleDateFormat("m");
            return mins.format(new Date(diff)) + "min";
        }

    }

    private String notificationTimeToString(int notificationTime) {

        switch (notificationTime) {
            case -1:
                return "Ingen";
            case 0:
                return "Vid start";
            case 1:
                return "2 minut";
            case 60:
                return "1 timme";
            case 120:
                return "2 timmar";
            default:
                return notificationTime + " minuter";

        }
    }

}

