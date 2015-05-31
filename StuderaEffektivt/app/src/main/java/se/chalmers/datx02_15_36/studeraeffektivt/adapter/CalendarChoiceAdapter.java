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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarChoiceItem;

/**
 * Created by SoyaPanda on 15-04-10.
 */
public class CalendarChoiceAdapter extends ArrayAdapter<CalendarChoiceItem> {

    private Context context;
    private ArrayList<CalendarChoiceItem> itemsArrayList;

    public CalendarChoiceAdapter(Context context, int resource, int textViewResourceId, ArrayList<CalendarChoiceItem> itemsArrayList) {

        super(context, resource, textViewResourceId, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public int getCount() {
        return itemsArrayList.size();
    }

    @Override
    public CalendarChoiceItem getItem(int position) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_choice_item, parent, false);
        }

        int color = itemsArrayList.get(position).getColor();

        // 3. Get the two text view from the convertView
        TextView textView = (TextView) convertView.findViewById(R.id.calendar_choice_text);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.calendar_choice_myRectangleView);

        // 4. Set the text for textView
        textView.setText(" " + itemsArrayList.get(position).getTitle());
        textView.setTextColor(Color.BLACK);
        imageView.setBackgroundColor(color);

        // 6. retrn convertView
        return convertView;
    }

}
