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

package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.IntegerValueFormatter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.OneDecimalFormatter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;

/*
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
*/

public class StatsFrag extends Fragment {

    private View rootView;

    private Spinner spinner;
    private String currCourse = "";

    private PieChart pieHours;
    private PieChart pieAssignments;
    private LineChart lineChart;

    private TextView noDataView;

    private DBAdapter dbAdapter;
    private Utils utils;

    private boolean hasInit;

    private SharedPreferences sharedPref;
    private String ccodePrefName = "CoursePref";
    private String ccodeExtraName = "course";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) {
            dbAdapter = new DBAdapter(getActivity());
        }
        utils = new Utils();

        rootView = inflater.inflate(R.layout.activity_stats, container, false);
        instantiateView(getHoursSpent(), getHoursLeft(), getAssDone(), getAssLeft());
        if (!isCourses()) {
            hideCharts();
        }
        hasInit = true;

        return rootView;
    }

    private void insertCourseDatorteknik() {
        insertTestDataToDB("EDA432");
    }

    private void insertFakeData2() {
        insertTestDataToDB2("APA777");
    }

    private void instantiateView(int hoursDone, int hoursLeft, int assesDone, int assesLeft) {
        spinner = (Spinner) rootView.findViewById(R.id.spinner_stats);
        noDataView = (TextView) rootView.findViewById(R.id.stats_empty);
        setCourses();
        spinner.setSelection(0);
        drawCharts();
    }

    private void hideCharts() {
        lineChart.setVisibility(View.INVISIBLE);
        pieHours.setVisibility(View.INVISIBLE);
        pieAssignments.setVisibility(View.INVISIBLE);
    }

    private void showCharts() {
        lineChart.setVisibility(View.VISIBLE);
        pieHours.setVisibility(View.VISIBLE);
        pieAssignments.setVisibility(View.VISIBLE);
    }

    private void hideNoDataView() {
        noDataView.setVisibility(View.INVISIBLE);
    }

    private void showNoCourseView() {
        Log.d("stats", "in showNoCourseView");
        noDataView.setText("Lägg till kurser och uppgifter \n lös sedan uppgifter och logga tid med timern \n så får du se din utveckling här!");
        noDataView.setVisibility(View.VISIBLE);
    }

    private void showNoDataView() {
        Log.d("stats", "in showNoDataView");
        noDataView.setText("Lös uppgifter och logga tid med timern \n så får du se din utveckling här!");
        noDataView.setVisibility(View.VISIBLE);
    }

    private void instantiateLineChart() {
        lineChart = (LineChart) rootView.findViewById(R.id.line_hours);
        lineChart.setVisibility(View.VISIBLE);

        //For each week
        Entry hoursInWeek;
        //For each course
        ArrayList<Entry> hoursInCourse;
        //Just enhance it with adding course identifier
        LineDataSet setOfHoursInCourse;

        //All courses
        ArrayList<LineDataSet> setsOfHoursInCourses = new ArrayList<>();
        ArrayList<String> weeks = new ArrayList<>();
        LineData data;

        Cursor courses = dbAdapter.getOngoingCourses();
        int c = 0;
        while (courses.moveToNext()) {
            String ccode = courses.getString(courses.getColumnIndex("_ccode"));
            int smallestWeek = dbAdapter.getSmallestWeek(ccode);
            Log.d("lineChart", "smallestWeek: " + smallestWeek);
            hoursInCourse = new ArrayList<>();

            int i = 0;
            for (int w = smallestWeek; w <= Utils.getCurrWeekNumber(); w++) {

                if (c == 0) {
                    weeks.add("v. " + w);
                }

                Cursor mins = dbAdapter.getMinutes(w, ccode);
                if (mins.getCount() == 0) {
                    hoursInWeek = new Entry(0, i);
                    Log.d("lineChart", "getMinutes().getCount() is 0, week: " + w + ", course: " + ccode);
                } else {
                    int hours = 0;
                    while (mins.moveToNext()) {
                        hours += (mins.getInt(0) / 60);
                    }
                    Log.d("lineChart", "week: " + w + ", course: " + ccode + ", hours in course and week: " + hours);
                    hoursInWeek = new Entry(hours, i);

                }
                hoursInCourse.add(hoursInWeek);
                Log.d("lineChart", "week: " + w + ", course: " + ccode + ", added something to entryarray");

                i++;
            }

            setOfHoursInCourse = new LineDataSet(hoursInCourse, ccode);

            int[] cols = getColors();
            int color = cols[c % cols.length];
            setOfHoursInCourse.setColor(color);

            setsOfHoursInCourses.add(setOfHoursInCourse);
            c++;
            Log.d("lineChart", "weeks.length: " + weeks.size() + " dataSet.length: " + setOfHoursInCourse.getEntryCount());
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

    private int[] getColors() {
        int[] cols = {Color.parseColor("#B3E5FC"), Color.parseColor("#56c8fc"),
                Color.parseColor("#d9f1fc"), Color.parseColor("#00a2ed"), Color.parseColor("#0083bf"),
                Color.parseColor("#32a6db"), Color.parseColor("#1a719a")};

        return cols;
    }

    private void instantiatePieHours(int hoursDone, int hoursLeft) {
        pieHours = (PieChart) rootView.findViewById(R.id.pie_hours);
        pieHours.setNoDataTextDescription("TIMMAR DU LAGT");
        pieHours.setVisibility(View.VISIBLE);

        //Set up pie chart data
        Log.d("stats", "pieHours spent: " + getHoursSpent());
        Log.d("stats", "pieHours left: " + getHoursLeft());

        ArrayList<Entry> pieEntries = new ArrayList<Entry>();
        Entry hoursLeftEntry = new Entry(hoursLeft, 1);
        Entry hoursDoneEntry = new Entry(hoursDone, 0);
        pieEntries.add(hoursLeftEntry);
        pieEntries.add(hoursDoneEntry);

        int[] colors = {Color.parseColor("#e5e5e5"), Color.parseColor("#B3E5FC")};
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Timmar");
        pieDataSet.setColors(colors);
        pieDataSet.setValueFormatter(new IntegerValueFormatter());

        ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
        dataSets.add(pieDataSet);
        ArrayList<String> pieLabels = new ArrayList<String>();
        pieLabels.add("Kvar");
        pieLabels.add("Klara");

        PieData pieData = new PieData(pieLabels, pieDataSet);
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

    private void instantiatePieAssignments(int assesDone, int assesLeft) {
        pieAssignments = (PieChart) rootView.findViewById(R.id.pie_assignments);
        pieAssignments.setNoDataTextDescription("UPPGIFTER DU GJORT");
        pieAssignments.setVisibility(View.VISIBLE);

        //Set up pie chart data
        Log.d("BAJS", "pieAsses done: " + getAssDone());
        Log.d("BAJS", "pieAsses left: " + getAssLeft());

        ArrayList<Entry> pieEntries = new ArrayList<Entry>();
        Entry assesDoneEntry = new Entry(assesDone, 0);
        Entry assesLeftEntry = new Entry(assesLeft, 1);
        pieEntries.add(assesLeftEntry);
        pieEntries.add(assesDoneEntry);

        int[] colors = {Color.parseColor("#e5e5e5"), Color.parseColor("#B3E5FC")};

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Uppgifter");
        pieDataSet.setColors(colors);
        pieDataSet.setValueFormatter(new IntegerValueFormatter());

        ArrayList<PieDataSet> dataSets = new ArrayList<PieDataSet>();
        dataSets.add(pieDataSet);
        ArrayList<String> pieLabels = new ArrayList<String>();
        pieLabels.add("Kvar");
        pieLabels.add("Klara");

        PieData pieData = new PieData(pieLabels, pieDataSet);

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

        Cursor cursor = dbAdapter.getOngoingCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String ccode = cursor.getString(ccodeColumn);
                String cname = cursor.getString(cnameColumn);
                adapter.add(ccode + " " + cname);
            }
        } else {
            adapter.add("Inga tillagda kurser");
        }
    }

    public void setSelectedCourse() {
        Log.i("DB", "spinner's selected item: " + spinner.getSelectedItem());
        if (spinner.getSelectedItem() != null) {
            String temp = spinner.getSelectedItem().toString();
            String[] parts = temp.split(" ");
            this.currCourse = parts[0];

            setSharedCoursePos(spinner.getSelectedItemPosition());

            //Tests if the update of the sharedpref worked:
            Log.d("sharedcourse", "Stats. set select: "+spinner.getSelectedItemPosition()
                + " get select: "+ getSharedCoursePos());
        }
        drawCharts();
    }

    private int getHoursSpent() {
        return ((dbAdapter.getSpentTime(currCourse) / 60));
    }

    private int getHoursLeft() {
        int total = (dbAdapter.getTimeOnCourse(currCourse) / 60);
        int spent = (dbAdapter.getSpentTime(currCourse) / 60);

        if (total <= spent) {
            return 0;
        } else {
            return total - spent;
        }
    }

    private int getAssDone() {
        return dbAdapter.getDoneAssignments(currCourse).getCount();
    }

    private int getAssLeft() {
        int assignments = dbAdapter.getAssignments(currCourse).getCount();
        Log.d("ass", "total amount of asses: " + assignments);
        int doneAssignments = dbAdapter.getDoneAssignments(currCourse).getCount();
        return (assignments - doneAssignments);
    }

    private boolean isCourses() {
        Cursor courses = dbAdapter.getOngoingCourses();
        if (courses.getCount() == 0) {
            return false;
        }
        return true;
    }

    private boolean courseHasAsses() {
        if (currCourse != null) {
            Cursor asses = dbAdapter.getAssignments(currCourse);
            if (asses.getCount() != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean courseHasSessions() {
        if (currCourse != null && isCourses()) {
            Cursor sessions = dbAdapter.getSessions();
            while (sessions.moveToNext()) {
                if (sessions.getString(sessions.getColumnIndex("_ccode")).equals(currCourse)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onStart() {
        super.onStart();
        if (isCourses() && spinner != null) {
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
        long idCourse = dbAdapter.insertCourse(course, "Datorteknik");
        if (idCourse > 0) {
            //Toast.makeText(getActivity(), course+" created", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getActivity(), "Failed to create course in Stats.", Toast.LENGTH_SHORT ).show();
        }

        //Insert sessions
        long idS1 = dbAdapter.insertSession(course, utils.getCurrWeekNumber(), 60);
        long idS2 = dbAdapter.insertSession(course, utils.getCurrWeekNumber(), 120);
        long idS3 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber() - 1), 300);
        long idS4 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber() - 1), 30);
        long idS5 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber() - 2), 60);
        long idS6 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber() - 2), 60);
        /*if (idS1 > 0 && idS2 > 0 && idS3 > 0 && idS4 > 0 && idS5 > 0 && idS6 > 0) {
            Toast.makeText(getActivity(), "Added six sessions to "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Sessions in Stats", Toast.LENGTH_SHORT).show();
        }*/

        //Insert TimeOnCourse.
        long idTOC = dbAdapter.insertTimeOnCourse(course, 1200);
        if (idTOC > 0) {
            //Toast.makeText(getActivity(), "Added TimeOnCourse 1200 for "+course, Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getActivity(), "Failed to add TimeOnCourse in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert Assignments
        /*long idA1 = dbAdapter.insertAssignment(course, 0, Utils.getCurrWeekNumber(), "2B", 15, 30, AssignmentType.READ, AssignmentStatus.DONE);
        if (idA1>0) {
            Toast.makeText(getActivity(), "Added DONE ASSIGNMENT for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Assignment in Stats", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void insertTestDataToDB2(String course) {
        //Insert course
        long idCourse = dbAdapter.insertCourse(course, "Diskret matematik");
        if (idCourse > 0) {
            //Toast.makeText(getActivity(), course+" created", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getActivity(), "Failed to create course in Stats.", Toast.LENGTH_SHORT).show();
        }

        //Insert sessions
        long idS1 = dbAdapter.insertSession(course, utils.getCurrWeekNumber(), 60);
        long idS2 = dbAdapter.insertSession(course, utils.getCurrWeekNumber(), 120);
        long idS3 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber() - 1), 400);
        long idS4 = dbAdapter.insertSession(course, (utils.getCurrWeekNumber() - 2), 50);
        /*if (idS1 > 0 && idS2 > 0 && idS3 > 0 && idS4 > 0) {
            Toast.makeText(getActivity(), "Added six sessions to "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Sessions in Stats", Toast.LENGTH_SHORT).show();
        }*/

        //Insert TimeOnCourse.
        long idTOC = dbAdapter.insertTimeOnCourse(course, 1200);
        if (idTOC > 0) {
            //Toast.makeText(getActivity(), "Added TimeOnCourse 1200 for "+course, Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getActivity(), "Failed to add TimeOnCourse in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert Assignments
        /*long idA1 = dbAdapter.insertAssignment(course, 0, Utils.getCurrWeekNumber(), "2B", 15, 30, AssignmentType.READ, AssignmentStatus.DONE);
        if (idA1>0) {
            Toast.makeText(getActivity(), "Added DONE ASSIGNMENT for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Assignment in Stats", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void updateView() {
        Log.d("sharedcourse", "stats updateview, sharedId: "+ getSharedCoursePos());
        setCourses();
        //Course has both sessions and assignments
        if (isCourses() && courseHasSessions() && courseHasAsses()) {
            spinner.setSelection(getSharedCoursePos());
            drawCharts();
            //Course has sessions but no assignments
        } else if (isCourses() && courseHasSessions()) {
            spinner.setSelection(getSharedCoursePos());
            drawSessionsChart();
        } else if (isCourses()) {
            spinner.setSelection(getSharedCoursePos());
            hideCharts();
            showNoDataView();
            //No courses
        } else {
            showNoCourseView();
        }
    }

    private void setSharedCoursePos(int pos){
        sharedPref = getActivity().getSharedPreferences(ccodePrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(ccodeExtraName, pos);
    }

    private int getSharedCoursePos(){
        sharedPref = getActivity().getSharedPreferences(ccodePrefName, Context.MODE_PRIVATE);
        return sharedPref.getInt(ccodeExtraName, 0);
    }

    private void drawSessionsChart() {
        instantiatePieAssignments(getAssDone(), getAssLeft());
        hideNoDataView();
        pieHours.setVisibility(View.VISIBLE);
    }


    private void drawCharts() {
        instantiatePieHours(getHoursSpent(), getHoursLeft());
        instantiatePieAssignments(getAssDone(), getAssLeft());
        instantiateLineChart();
        hideNoDataView();
        showCharts();
    }

    public boolean hasInit() {
        return hasInit;
    }
}
