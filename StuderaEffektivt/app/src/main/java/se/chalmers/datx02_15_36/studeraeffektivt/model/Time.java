package se.chalmers.datx02_15_36.studeraeffektivt.model;

/**
 * Created by alexandraback on 23/04/15.
 */
public class Time {
    private int hour;
    private int min;
    private int seconds =0;

    public Time (int hour,int min){
        this.hour=hour;
        this.min= min;
    }
    public Time (int hour,int min,int seconds){
        this.hour=hour;
        this.min= min;
        this.seconds = seconds;
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

    public String getTimeWithSecondsString() {

        String minutes =  String.format("%02d", this.min);
        String hours = String.format("%02d",this.hour);
        String seconds = String.format("%02d",this.seconds);

    return hours + ":" + minutes + ":" + seconds;

    }

    public long timeToMillisSeconds () {
      return((long) this.min * 60 * 1000 + hour * 3600*1000);
    }

    public static Time setTimeFromMilliSeconds(long milliSeconds) {
        int seconds =(int) milliSeconds/1000;
        int hour = seconds/3600;
        int remainder = seconds - hour*3600;
        int min = remainder/60;
        remainder -= min*60;

    return new Time (hour,min,remainder);}

}
