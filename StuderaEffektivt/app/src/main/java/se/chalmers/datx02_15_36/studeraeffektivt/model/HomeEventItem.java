package se.chalmers.datx02_15_36.studeraeffektivt.model;

/**
 * Created by emmawestman on 15-04-04.
 */
public class HomeEventItem{

    private String titleS;
    private String timeS;
    private String locationS;
    private String timeToStartS;
    private long id;
    private long startTime;
    private long endTime;



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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
