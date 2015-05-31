/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

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
