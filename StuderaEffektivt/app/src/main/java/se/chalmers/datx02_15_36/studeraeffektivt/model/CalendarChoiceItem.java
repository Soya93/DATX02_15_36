package se.chalmers.datx02_15_36.studeraeffektivt.model;

/**
 * Created by SoyaPanda on 15-04-09.
 */
public class CalendarChoiceItem {

    private int color;
    private String title;
    private boolean isChecked;

    public CalendarChoiceItem() {
        color = 0;
        title = "";
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


}
