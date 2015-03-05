package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

public class StatsFrag extends Fragment {

    PieChart pc_hours;
    PieChart pc_ass;
    LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_stats, container, false);

        //Pie chart showing how many ours studied.
        pc_hours = (PieChart) rootView.findViewById(R.id.piechart_hours);
        pc_hours.setNoDataTextDescription("TIMMAR DU LAGT");


        /*pc_ass = (PieChart) getView().findViewById(R.id.piechart_ass);
        lineChart = (LineChart) getView().findViewById(R.id.linechart);*/


        return rootView;
    }
}
