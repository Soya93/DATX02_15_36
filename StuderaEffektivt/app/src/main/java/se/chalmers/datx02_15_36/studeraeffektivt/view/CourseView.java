package se.chalmers.datx02_15_36.studeraeffektivt.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * Created by emmawestman on 15-05-14.
 */
public class CourseView {

    public AlertDialog.Builder confirmCourseStatusView(String course, boolean isActive, Activity act){
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.confirm_dialog, null);
        String status;
        if(isActive) {
            status = "avslutad";
        }else {
            status = "pågående";
        }

        TextView eventNameLabel = (TextView) dialogView.findViewById(R.id.confirm_text);
        if (eventNameLabel != null) {
            eventNameLabel.setText("Vill du verkligen markera " + course + " som " + status + "?");
        }
        builder.setView(dialogView);

        return builder;
    }
}
