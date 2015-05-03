package se.chalmers.datx02_15_36.studeraeffektivt.util;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Patricia on 2015-05-02.
 */
public class OneDecimalFormatter implements ValueFormatter {

    @Override
    public String getFormattedValue(float value){
        DecimalFormat df = new DecimalFormat("#.#");
        return ""+df.format(value);
    }
}
