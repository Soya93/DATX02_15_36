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
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.HomeEventItem;

/**
 * Created by emmawestman on 15-04-06.
 */
public class HomeAdapter extends ArrayAdapter<HomeEventItem> {

    private Context context;
    private ArrayList<HomeEventItem> itemsArrayList;


    public HomeAdapter(Context context, ArrayList<HomeEventItem> itemsArrayList) {

        super(context, 0, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public int getCount() {
        return itemsArrayList.size();
    }

    @Override
    public HomeEventItem getItem(int position) {
        try {
            return itemsArrayList.get(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_event_item2, parent, false);
        }


        // 3. Get the two text view from the convertView
        TextView titleView = (TextView) convertView.findViewById(R.id.event_name_item_lable);
        TextView timeView = (TextView) convertView.findViewById(R.id.event_time_item_lable);
        TextView locationView = (TextView) convertView.findViewById(R.id.location_item_lable);
        TextView timeToEvent = (TextView) convertView.findViewById(R.id.time_to_event_item_lable);

        // 4. Set the text for textView
        titleView.setText(itemsArrayList.get(position).getTitleS());
        timeView.setText(itemsArrayList.get(position).getTimeS());
        locationView.setText(itemsArrayList.get(position).getLocationS());
        timeToEvent.setText(itemsArrayList.get(position).getTimeToStartS());

        //5. Change color of the calendar image according to the color of the event
        int color = itemsArrayList.get(position).getColor();
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        Drawable myIcon = context.getResources().getDrawable( R.drawable.ic_cal2).mutate();
        myIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        imageView.setImageDrawable(myIcon);

        // 6. return convertView
        return convertView;
    }
}
