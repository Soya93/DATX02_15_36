package se.chalmers.datx02_15_36.studeraeffektivt.model;

/**
 * Created by SoyaPanda on 15-04-09.
 */
public class CalendarsFilterItem extends  CalendarChoiceItem {
    private boolean isChecked;
    private long calID;

    public CalendarsFilterItem() {
        super();
        isChecked = true;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void setCalID(long calID) {
        this.calID = calID;
    }

    public long getCalID() {
        return this.calID;
    }

}
