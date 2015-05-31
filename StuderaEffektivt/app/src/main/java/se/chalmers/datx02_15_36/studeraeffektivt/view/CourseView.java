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
