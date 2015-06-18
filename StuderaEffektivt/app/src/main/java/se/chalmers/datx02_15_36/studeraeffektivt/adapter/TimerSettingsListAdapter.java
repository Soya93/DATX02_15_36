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

package se.chalmers.datx02_15_36.studeraeffektivt.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.TimerSettingsActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Time;

/**
 * Created by alexandraback on 23/04/15.
 */
public class TimerSettingsListAdapter extends BaseAdapter {
    private String [] values;
    private Context context;
    private HashMap<Integer,Time> mapping ;
    private static LayoutInflater inflater=null;


    public TimerSettingsListAdapter(TimerSettingsActivity mainActivity, String[] stringValues, HashMap<Integer, Time> inputHashMap) {
        values=stringValues;
        context=mainActivity;
        mapping =inputHashMap;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView forStringValues;
        TextView forIntegerValues;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.timer_list, null);
        Time time = mapping.get(position);
        holder.forStringValues=(TextView) rowView.findViewById(R.id.settings_name);
        holder.forIntegerValues=(TextView) rowView.findViewById(R.id.time);
        holder.forStringValues.setText(values[position]);
        String text = "";
        if(position != 0) {
            text = time.getHour() != 0? String.valueOf(time.getHour()) + " tim": "";
            text = time.getMin() != 0 ? text + " " +  String.valueOf(time.getMin()) + " min": text + "";
        }else{
            text = String.valueOf(time.getMin()) + " st";
        }
        holder.forIntegerValues.setText(text);
        return rowView;
    }



}