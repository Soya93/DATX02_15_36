package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/*
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
*/

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

public class StatsFrag extends Fragment {

    PieChart pc_hours;
    //PieChart pc_ass;
    //LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View rootView = inflater.inflate(R.layout.activity_stats, container, false);

        //Pie chart showing how many ours studied.
        pc_hours = (PieChart) rootView.findViewById(R.id.piechart_hours);
        pc_hours.setNoDataTextDescription("TIMMAR DU LAGT");

        //Set up pie chart data
        ArrayList<Entry> pieEntries = new ArrayList<Entry>();
        Entry hoursDone = new Entry(50,0);
        Entry hoursLeft = new Entry(50,1);
        pieEntries.add(hoursDone);
        pieEntries.add(hoursLeft);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Timmar");
        ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
        dataSets.add(pieDataSet);
        ArrayList<String> pieLabels = new ArrayList<String>();
        pieLabels.add("Klara");
        pieLabels.add("Kvar");

        PieData pieData = new PieData(pieLabels,pieDataSet);
        pc_hours.setData(pieData);

        //Style pie chart data
        pc_hours.setDescription("");
        pc_hours.setDrawHoleEnabled(true);
        pc_hours.setUsePercentValues(true);

        //Show pie chart data
        pc_hours.invalidate();




        /*pc_ass = (PieChart) getView().findViewById(R.id.piechart_ass);
        lineChart = (LineChart) getView().findViewById(R.id.linechart);*/


        return rootView;
    }
}
