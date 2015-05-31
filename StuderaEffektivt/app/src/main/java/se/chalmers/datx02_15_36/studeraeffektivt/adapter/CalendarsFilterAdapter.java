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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarsFilterItem;

/**
 * Created by SoyaPanda on 15-04-09.
 */
public class CalendarsFilterAdapter extends ArrayAdapter<CalendarsFilterItem> {

    private Context context;
    private ArrayList<CalendarsFilterItem> itemsArrayList;
    private ImageView colorView;

    public CalendarsFilterAdapter(Context context, int resource, int textViewResourceId, ArrayList<CalendarsFilterItem> itemsArrayList) {

        super(context, resource, textViewResourceId, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public int getCount() {
        return itemsArrayList.size();
    }

    @Override
    public CalendarsFilterItem getItem(int position) {
        return itemsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendars_filter_item, parent, false);
        }

        // 3. Get the two text view from the convertView

        // CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.calendar_checkBox);
        TextView textView = (TextView) convertView.findViewById(R.id.calendar_text);
        colorView = (ImageView) convertView.findViewById(R.id.calendar_color_image);

        // 4. Set the text for textView
        textView.setText(" " + itemsArrayList.get(position).getTitle());
        textView.setTextColor(Color.BLACK);

        if(itemsArrayList.get(position).isChecked()) {
            colorView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_square));
            colorView.setColorFilter(itemsArrayList.get(position).getColor());
        }else {
            colorView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_square_frame));
            colorView.setColorFilter(itemsArrayList.get(position).getColor());
        }


        // 6. retrn convertView
        return convertView;
    }





    public void setColor(int color) {
        this.colorView.setColorFilter(color);
    }

    public ArrayList<CalendarsFilterItem> getItemsArrayList() {
        return this.itemsArrayList;
    }


}
