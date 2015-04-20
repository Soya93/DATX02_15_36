package se.chalmers.datx02_15_36.studeraeffektivt.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.calendar_checkBox);
        TextView textView = (TextView) convertView.findViewById(R.id.calendar_text);

        // 4. Set the text for textView
        textView.setText(" " + itemsArrayList.get(position).getTitle());
        textView.setTextColor(Color.BLACK);
        checkBox.setBackgroundColor(itemsArrayList.get(position).getColor());
        addClickHandlerToCheckBox(checkBox, position);
        checkBox.setChecked(itemsArrayList.get(position).isChecked());

        // 6. retrn convertView
        return convertView;
    }

    private void addClickHandlerToCheckBox(CheckBox checkbox, final int position) {
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                                                    CheckBox checkbox = (CheckBox) arg0;
                                                    boolean isChecked = checkbox.isChecked();
                                                    itemsArrayList.get(position).setChecked(isChecked);
                                                }
                                            }
        );
    }
}
