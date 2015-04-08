package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * Created by emmawestman on 15-04-04.
 */
public class HomeEventItem{

    private View view;


    private String titleS;
    private String timeS;
    private String locationS;
    private String timeToStartS;



    public HomeEventItem() {

    }


    public String getTitleS() {
        return titleS;
    }

    public void setTitleS(String title) {

        if(title.length() > 20) {
            this.titleS = title.substring(0,20) + "...";
        } else {
            this.titleS = title;
        }
    }

    public String getTimeS() {
        return timeS;
    }

    public void setTimeS(String timeS) {
        this.timeS = timeS;
    }

    public String getLocationS() {
        return locationS;
    }

    public void setLocationS(String locationS) {
        this.locationS = locationS;
    }

    public String getTimeToStartS() {
        return timeToStartS;
    }

    public void setTimeToStartS(String timeToStartS) {
        this.timeToStartS = timeToStartS;
    }
}
