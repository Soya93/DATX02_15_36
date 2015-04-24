package se.chalmers.datx02_15_36.studeraeffektivt.util;

import com.github.mikephil.charting.utils.ValueFormatter;

/**
 * Created by Patricia on 2015-04-24.
 */
public class IntegerValueFormatter implements ValueFormatter {


    @Override
    public String getFormattedValue(float value) {
        return ""+Math.round(value);
    }
}
