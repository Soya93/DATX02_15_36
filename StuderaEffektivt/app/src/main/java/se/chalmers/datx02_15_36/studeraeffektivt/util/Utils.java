package se.chalmers.datx02_15_36.studeraeffektivt.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Patricia on 2015-04-07.
 */
public class Utils {

    public Utils(){}

    public static int getCurrWeekNumber(){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }


}
