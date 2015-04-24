package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.app.TimePickerDialog;
import android.content.Context;
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
public class ListAdapter extends BaseAdapter {
    private String [] values;
    private Context context;
    private HashMap<Integer,Time> mapping ;
    private static LayoutInflater inflater=null;


    public ListAdapter(TimerSettingsActivity mainActivity, String[] stringValues,HashMap<Integer,Time> inputHashMap) {
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
        Time b = mapping.get(position);
        holder.forStringValues=(TextView) rowView.findViewById(R.id.settings_name);
        holder.forIntegerValues=(TextView) rowView.findViewById(R.id.time);
        holder.forStringValues.setText(values[position]);
        if(position != 0){
        holder.forIntegerValues.setText(b.getString());}
        else{
            holder.forIntegerValues.setText(String.valueOf(b.getMin()));
        }

        return rowView;
    }



}
