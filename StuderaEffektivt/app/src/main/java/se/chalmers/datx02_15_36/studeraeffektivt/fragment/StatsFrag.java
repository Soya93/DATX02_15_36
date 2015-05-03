package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
*/

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.LargeValueFormatter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.IntegerValueFormatter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.OneDecimalFormatter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;

public class StatsFrag extends Fragment {

    private View rootView;

    private Spinner spinner;
    private String currCourse = "";

    private PieChart pieHours;
    private PieChart pieAssignments;
    private LineChart lineChart;

    private DBAdapter dbAdapter;
    private Utils utils;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            dbAdapter = new DBAdapter(getActivity());
        }
        utils = new Utils();

        //insertTestDataToDB("DDD111");
        //insertTestDataToDB2("APA777");

        Log.d("stats", "there is data: "+thereIsData());
        if(thereIsData()) {
            rootView = inflater.inflate(R.layout.activity_stats, container, false);
            instantiateView();
        }else{

            rootView = inflater.inflate(R.layout.activity_stats_empty, container, false);
        }

        return rootView;
    }

    private void instantiateView(){
        spinner = (Spinner) rootView.findViewById(R.id.spinner_stats);
        setCourses();
        spinner.setSelection(0);
        Log.i("DB", "initial selection: "+spinner.getSelectedItem());

        instantiatePieHours();
        instantiatePieAssignments();
        instantiateLineChart();
    }

    private void instantiateLineChart(){
        lineChart = (LineChart) rootView.findViewById(R.id.line_hours);

        //For each week
        Entry hoursInWeek;
        //For each course
        ArrayList<Entry> hoursInCourse;
        int color = Color.parseColor("#B3E5FC");
        //Just enhance it with adding course identifier
        LineDataSet setOfHoursInCourse;

        //All courses
        ArrayList<LineDataSet> setsOfHoursInCourses = new ArrayList<>();
        ArrayList<String> weeks = new ArrayList<>();
        LineData data;

        Cursor courses = dbAdapter.getCourses();
        int c = 0;
        while( courses.moveToNext() ){
            String ccode = courses.getString(courses.getColumnIndex("_ccode"));
            int smallestWeek = dbAdapter.getSmallestWeek(ccode);
            hoursInCourse = new ArrayList<>();

            int i = 0;
            for(int w=smallestWeek; w<Utils.getCurrWeekNumber(); w++){

                if(c == 0){
                    weeks.add(""+w);
                }

                Cursor mins = dbAdapter.getMinutes(w, ccode);
                if(mins.getCount() == 0){
                    hoursInWeek = new Entry(0, i);
                    Log.d("lineChart", "week: "+w+", course: "+ccode+", hours 0 getMinutes().getCount() is 0");
                }else {
                    int hours = 0;
                    while (mins.moveToNext()) {
                        hours += (mins.getInt(0) / 60);
                    }
                    Log.d("lineChart", "week: "+w+", course: "+ccode+", hours in course and week: " + hours);
                    hoursInWeek = new Entry(hours, i);

                }
                hoursInCourse.add(hoursInWeek);
                Log.d("lineChart", "week: " + w + ", course: " + ccode + ", added something to entryarray");

                i++;
            }

            setOfHoursInCourse = new LineDataSet(hoursInCourse, ccode);

            int[] cols = getColors();
            color = cols[c%cols.length];
            setOfHoursInCourse.setColor(color);

            setsOfHoursInCourses.add(setOfHoursInCourse);
            c++;
            Log.d("lineChart", "weeks.length: "+weeks.size()+" dataSet.length: "+setOfHoursInCourse.getEntryCount());
        }

        data = new LineData(weeks, setsOfHoursInCourses);
        data.setValueFormatter(new IntegerValueFormatter());

        lineChart.setData(data);
        lineChart.setDescription("");
        lineChart.setTouchEnabled(false);

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setValueFormatter(new OneDecimalFormatter());
        leftYAxis.setStartAtZero(true);

        Legend legend = lineChart.getLegend();
        legend.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        legend.setForm(Legend.LegendForm.CIRCLE);

        lineChart.invalidate();

    }

    private int[] getColors(){
        int[] cols = {Color.parseColor("#B3E5FC"), Color.parseColor("#56c8fc"),
                Color.parseColor("#d9f1fc"), Color.parseColor("#00a2ed"), Color.parseColor("#0083bf"),
                Color.parseColor("#32a6db"), Color.parseColor("#1a719a")};

        return cols;
    }

    private void instantiatePieHours(){
        pieHours = (PieChart) rootView.findViewById(R.id.pie_hours);
        pieHours.setNoDataTextDescription("TIMMAR DU LAGT");

        //Set up pie chart data
        Log.d("stats", "pieHours spent: "+getHoursSpent());
        Log.d("stats", "pieHours left: "+getHoursLeft());

        ArrayList<Entry> pieEntries = new ArrayList<Entry>();
        Entry hoursDone = new Entry(getHoursSpent(),0);
        Entry hoursLeft = new Entry(getHoursLeft(),1);
        pieEntries.add(hoursDone);
        pieEntries.add(hoursLeft);

        int[] colors = {Color.parseColor("#e5e5e5"), Color.parseColor("#B3E5FC")};
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Timmar");
        pieDataSet.setColors(colors);
        pieDataSet.setValueFormatter(new IntegerValueFormatter());

        ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
        dataSets.add(pieDataSet);
        ArrayList<String> pieLabels = new ArrayList<String>();
        pieLabels.add("Kvar");
        pieLabels.add("Klara");

        PieData pieData = new PieData(pieLabels,pieDataSet);
        pieHours.setData(pieData);

        //Style pie chart data
        pieHours.setDescription("");
        pieHours.setCenterText("Timmar");
        pieHours.setDrawHoleEnabled(true);
        pieHours.setHoleColorTransparent(true);
        pieHours.getLegend().setEnabled(false);
        pieHours.setTouchEnabled(false);

        //Show pie chart data
        pieHours.invalidate();
    }

    private void instantiatePieAssignments(){
        pieAssignments = (PieChart) rootView.findViewById(R.id.pie_assignments);
        pieAssignments.setNoDataTextDescription("UPPGIFTER DU GJORT");

        //Set up pie chart data
        Log.d("BAJS", "pieAsses done: "+getAssDone());
        Log.d("BAJS", "pieAsses left: "+getAssLeft());

        ArrayList<Entry> pieEntries = new ArrayList<Entry>();
        Entry assesDone = new Entry(getAssDone(),0);
        Entry assesLeft = new Entry(getAssLeft(),1);
        pieEntries.add(assesDone);
        pieEntries.add(assesLeft);

        int[] colors = {Color.parseColor("#e5e5e5"), Color.parseColor("#B3E5FC")};

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Uppgifter");
        pieDataSet.setColors(colors);
        pieDataSet.setValueFormatter(new IntegerValueFormatter());

        ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
        dataSets.add(pieDataSet);
        ArrayList<String> pieLabels = new ArrayList<String>();
        pieLabels.add("Kvar");
        pieLabels.add("Klara");

        PieData pieData = new PieData(pieLabels,pieDataSet);

        pieAssignments.setData(pieData);

        //Style pie chart data
        pieAssignments.setDescription("");
        pieAssignments.setCenterText("Uppgifter");
        pieAssignments.setDrawHoleEnabled(true);
        pieAssignments.setHoleColorTransparent(true);
        pieAssignments.getLegend().setEnabled(false);
        pieAssignments.setTouchEnabled(false);

        //Show pie chart data
        pieAssignments.invalidate();
    }

    private void setCourses() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Cursor cursor = dbAdapter.getCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");
        while (cursor.moveToNext()) {
            String ccode = cursor.getString(ccodeColumn);
            String cname = cursor.getString(cnameColumn);
            adapter.add(ccode + " " + cname);
        }
    }

    public void setSelectedCourse(){
        Log.i("DB", "spinner's selected item: " + spinner.getSelectedItem());
        if(spinner.getSelectedItem() != null){
            String temp = spinner.getSelectedItem().toString();
            String[] parts = temp.split(" ");
            this.currCourse = parts[0];
        }
    }

    private int getHoursSpent(){
        setSelectedCourse();
        return (dbAdapter.getSpentTime(currCourse)/60);
    }

    private int getHoursLeft(){
        return ((dbAdapter.getTimeOnCourse(currCourse)/60)-(dbAdapter.getSpentTime(currCourse)/60));
    }

    private int getHoursTotal(){
        return (dbAdapter.getTimeOnCourse(currCourse)/60);
    }

    private int getAssDone(){
        return dbAdapter.getDoneAssignments(currCourse).getCount();
    }

    private int getAssLeft(){
        int assignments = dbAdapter.getAssignments(currCourse).getCount();
        int doneAssignments = dbAdapter.getDoneAssignments(currCourse).getCount();
        return (assignments-doneAssignments);
    }

    private boolean thereIsData(){
        Cursor courses = dbAdapter.getCourses();
        if( courses.getCount() == 0 ){
            return false;
        }
        return true;
    }

    public void onStart(){
        super.onStart();
        if (thereIsData()) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setSelectedCourse();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    private void insertTestDataToDB(String course) {
        //Insert course
        long idCourse = dbAdapter.insertCourse(course, "Default Course");
        if (idCourse > 0) {
            Toast.makeText(getActivity(), course+" created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to create course in Stats.", Toast.LENGTH_SHORT ).show();
        }

        //Insert sessions
        long idS1 = dbAdapter.insertSession(course, utils.getCurrWeekNumber(), 60);
        long idS2 = dbAdapter.insertSession(course, utils.getCurrWeekNumber(), 120);
        long idS3 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber()-1),300);
        long idS4 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber()-1),30);
        long idS5 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber()-2),60);
        long idS6 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber()-2),60);
        if (idS1 > 0 && idS2 > 0 && idS3 > 0 && idS4 > 0 && idS5 > 0 && idS6 > 0) {
            Toast.makeText(getActivity(), "Added six sessions to "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Sessions in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert TimeOnCourse.
        long idTOC = dbAdapter.insertTimeOnCourse(course, 1200);
        if (idTOC>0) {
            Toast.makeText(getActivity(), "Added TimeOnCourse 1200 for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add TimeOnCourse in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert Assignments
        long idA1 = dbAdapter.insertAssignment(course, 0, Utils.getCurrWeekNumber(), "2B", 15, 30, AssignmentType.READ, AssignmentStatus.DONE);
        if (idA1>0) {
            Toast.makeText(getActivity(), "Added DONE ASSIGNMENT for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Assignment in Stats", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertTestDataToDB2(String course) {
        //Insert course
        long idCourse = dbAdapter.insertCourse(course, "Apkursen");
        if (idCourse > 0) {
            Toast.makeText(getActivity(), course+" created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to create course in Stats.", Toast.LENGTH_SHORT).show();
        }

        //Insert sessions
        long idS1 = dbAdapter.insertSession(course, utils.getCurrWeekNumber(),60);
        long idS2 = dbAdapter.insertSession(course, utils.getCurrWeekNumber(),120);
        long idS3 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber()-1),300);
        long idS4 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber()-2),30);
        if (idS1 > 0 && idS2 > 0 && idS3 > 0 && idS4 > 0) {
            Toast.makeText(getActivity(), "Added six sessions to "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Sessions in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert TimeOnCourse.
        long idTOC = dbAdapter.insertTimeOnCourse(course, 1200);
        if (idTOC>0) {
            Toast.makeText(getActivity(), "Added TimeOnCourse 1200 for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add TimeOnCourse in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert Assignments
        long idA1 = dbAdapter.insertAssignment(course, 0, Utils.getCurrWeekNumber(), "2B", 15, 30, AssignmentType.READ, AssignmentStatus.DONE);
        if (idA1>0) {
            Toast.makeText(getActivity(), "Added DONE ASSIGNMENT for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Assignment in Stats", Toast.LENGTH_SHORT).show();
        }
    }
}
