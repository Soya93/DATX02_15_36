package se.chalmers.datx02_15_36.studeraeffektivt.model;

/**
 * Created by alexandraback on 23/04/15.
 */
public class Time {
    private int hour;
    private int min;

    public Time (int hour,int min){
        this.hour=hour;
        this.min= min;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMin(int min){
        this.min=min;
    }

    public int getHour() {

    return this.hour;}

    public int getMin() {
        return this.min;
    }

    public String getString () {
        String minutes= String.format("%02d", this.min);
        String hours = String.format("%02d",this.hour);
        return hours + " h : " + minutes + " min";
    }

}
