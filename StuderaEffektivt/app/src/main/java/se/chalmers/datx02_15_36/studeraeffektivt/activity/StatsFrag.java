package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.database.Cursor;
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
import android.widget.Toast;
/*
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
*/

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.w3c.dom.Text;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;

public class StatsFrag extends Fragment {

    private View rootView;

    private Spinner spinner;
    private String currCourse = "";

    private TextView hoursSpent;
    private TextView hoursLeft;
    private TextView hoursTotal;

    private TextView assDone;
    private TextView assLeft;

    private PieChart pieHours;
    private PieChart pieAssignments;

    private DBAdapter dbAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_stats, container, false);
        if (getActivity() != null) {
            dbAdapter = new DBAdapter(getActivity());
        }

        //insertTestDataToDB("DDD111");
        //insertTestDataToDB("BBB222");
        //insertDifferentTestData("OOO333");
        instantiateView();

        return rootView;
    }

    private void instantiateView(){
        spinner = (Spinner) rootView.findViewById(R.id.spinner_stats);
        setCourses();
        spinner.setSelection(0);
        Log.i("DB", "initial selection: "+spinner.getSelectedItem());

        instantiatePieHours();
    }

    private void instantiatePieHours(){
        pieHours = (PieChart) rootView.findViewById(R.id.pie_hours);
        pieHours.setNoDataTextDescription("TIMMAR DU LAGT");

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
        pieHours.setData(pieData);

        //Style pie chart data
        pieHours.setDescription("");
        pieHours.setDrawHoleEnabled(true);
        pieHours.setUsePercentValues(true);

        //Show pie chart data
        pieHours.invalidate();
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
            adapter.add(ccode + "-" + cname);
        }
        cursor.close();
    }

    private void setSelectedCourse(){
        Log.i("DB", "spinner's selected item: "+spinner.getSelectedItem());
        if(spinner.getSelectedItem() != null){
            String temp = spinner.getSelectedItem().toString();
            String[] parts = temp.split("-");
            this.currCourse = parts[0];
        }
    }

    /*private void setTextViews() {
        hoursSpent = (TextView) rootView.findViewById(R.id.hours_spent_show);
        setHoursSpent();
        hoursLeft = (TextView) rootView.findViewById(R.id.hours_left_show);
        setHoursLeft();
        hoursTotal = (TextView) rootView.findViewById(R.id.hours_total_show);
        setHoursTotal();

        assDone = (TextView) rootView.findViewById(R.id.ass_done_show);
        setAssDone();
        assLeft = (TextView) rootView.findViewById(R.id.ass_left_show);
        setAssLeft();
    }

    private void setHoursSpent(){
        String timeSpent = " "+(dbAdapter.getSpentTime(currCourse)/60)+" h";
        hoursSpent.setText(timeSpent);
    }

    private void setHoursLeft(){
        String timeLeft = " "+((dbAdapter.getTimeOnCourse(currCourse)/60)-(dbAdapter.getSpentTime(currCourse)/60))+" h";
        hoursLeft.setText(timeLeft);
    }

    private void setHoursTotal(){
        String timeTotal = " "+(dbAdapter.getTimeOnCourse(currCourse)/60)+" h";
        hoursTotal.setText(timeTotal);
    }

    private void setAssDone(){
        Cursor cursor = dbAdapter.getDoneAssignments(currCourse);
        String assignments = " "+cursor.getCount();
        cursor.close();
        assDone.setText(assignments);
    }

    private void setAssLeft(){
        int assignments = dbAdapter.getAssignments(currCourse).getCount();
        int doneAssignments = dbAdapter.getDoneAssignments(currCourse).getCount();
        assLeft.setText(" "+(assignments-doneAssignments));
    }*/

    public void onStart(){
        super.onStart();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedCourse();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void onResume(){
        super.onResume();
        Log.i("DB", "In onResume");
        //instantiateView();
    }

    private void insertTestDataToDB(String course) {
        //Insert course
        long idCourse = dbAdapter.insertCourse(course, "Default Course");
        if (idCourse > 0) {
            Toast.makeText(getActivity(), course+" created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to create course in Stats.", Toast.LENGTH_SHORT).show();
        }

        //Insert sessions
        long idS1 = dbAdapter.insertSession(course, 60);
        long idS2 = dbAdapter.insertSession(course, 120);
        long idS3 = dbAdapter.insertSession(course, 300);
        long idS4 = dbAdapter.insertSession(course, 30);
        long idS5 = dbAdapter.insertSession(course, 60);
        long idS6 = dbAdapter.insertSession(course, 60);
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
        long idA1 = dbAdapter.insertAssignment(course, 0, "2B", 15, 30, AssignmentType.READ, AssignmentStatus.DONE);
        if (idA1>0) {
            Toast.makeText(getActivity(), "Added DONE ASSIGNMENT for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Assignment in Stats", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertDifferentTestData(String course){
        //Insert course
        long idCourse = dbAdapter.insertCourse(course, "Other Course");
        if (idCourse > 0) {
            Toast.makeText(getActivity(), course+" created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to create course in Stats.", Toast.LENGTH_SHORT).show();
        }

        //Insert sessions

        long idS5 = dbAdapter.insertSession(course, 60);
        long idS6 = dbAdapter.insertSession(course, 60);
        if (idS5 > 0 && idS6 > 0) {
            Toast.makeText(getActivity(), "Added two sessions to "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Sessions in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert TimeOnCourse.
        long idTOC = dbAdapter.insertTimeOnCourse(course, 500);
        if (idTOC>0) {
            Toast.makeText(getActivity(), "Added TimeOnCourse 500 for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add TimeOnCourse in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert Assignments
        long idA1 = dbAdapter.insertAssignment(course, 0, "2B", 15, 30, AssignmentType.READ, AssignmentStatus.DONE);
        if (idA1>0) {
            Toast.makeText(getActivity(), "Added DONE ASSIGNMENT for "+course, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Assignment in Stats", Toast.LENGTH_SHORT).show();
        }
    }
}
