package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.widget.TimePicker;

/**
 * Created by alexandraback on 23/04/15.
 */
public class Child {

    private String Name;
    private TimePicker timePicker;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public TimePicker getTimePicker() {
        return timePicker;
    }

    public void setTimePicker(TimePicker timePicker) {
        this.timePicker = timePicker;
    }
}