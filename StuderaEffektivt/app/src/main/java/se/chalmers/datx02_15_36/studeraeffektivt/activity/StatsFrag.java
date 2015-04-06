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

import org.w3c.dom.Text;

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

    private DBAdapter dbAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_stats, container, false);
        if (getActivity() != null) {
            dbAdapter = new DBAdapter(getActivity());
        }

        insertTestDataToDB();
        instantiateView();

        return rootView;
    }

    private void instantiateView(){
        spinner = (Spinner) rootView.findViewById(R.id.spinner_stats);
        spinner.setSelection(0);
        setCourses();

        setTextViews();
    }

    private void setTextViews(){
        hoursSpent = (TextView) rootView.findViewById(R.id.hours_spent_show);
        hoursSpent.setText(""+getHoursSpent());

        hoursLeft = (TextView) rootView.findViewById(R.id.hours_left_show);
        hoursLeft.setText(""+getHoursLeft());

        hoursTotal = (TextView) rootView.findViewById(R.id.hours_total_show);
        hoursTotal.setText(""+getHoursTotal());

        assDone = (TextView) rootView.findViewById(R.id.ass_done_show);
        assDone.setText(""+getAssDone());

        assLeft = (TextView) rootView.findViewById(R.id.ass_left_show);
        assLeft.setText(""+getAssLeft());
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
    }

    public void setSelectedCourse(){
        spinner.setSelection(0);
        String temp = spinner.getSelectedItem().toString();
        String[] parts = temp.split("-");
        this.currCourse = parts[0];
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

    private void insertTestDataToDB() {
        //Insert course
        long idCourse = dbAdapter.insertCourse("DDD111", "Default Course");
        if (idCourse > 0) {
            Toast.makeText(getActivity(), "DDD111 created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to create course in Stats.", Toast.LENGTH_SHORT).show();
        }

        //Insert sessions
        long idS1 = dbAdapter.insertSession("DDD111", 60);
        long idS2 = dbAdapter.insertSession("DDD111", 120);
        long idS3 = dbAdapter.insertSession("DDD111", 300);
        long idS4 = dbAdapter.insertSession("DDD111", 30);
        long idS5 = dbAdapter.insertSession("DDD111", 60);
        long idS6 = dbAdapter.insertSession("DDD111", 60);
        if (idS1 > 0 && idS2 > 0 && idS3 > 0 && idS4 > 0 && idS5 > 0 && idS6 > 0) {
            Toast.makeText(getActivity(), "Added six sessions to DDD111", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Sessions in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert TimeOnCourse.
        long idTOC = dbAdapter.insertTimeOnCourse("DDD111", 1200);
        if (idTOC>0) {
            Toast.makeText(getActivity(), "Added TimeOnCourse 1200 for DD111", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add TimeOnCourse in Stats", Toast.LENGTH_SHORT).show();
        }

        //Insert Assignments
        long idA1 = dbAdapter.insertAssignment("DDD111", 0, "2B", 15, 30, AssignmentType.READ, AssignmentStatus.DONE);
        if (idA1>0) {
            Toast.makeText(getActivity(), "Added DONE ASSIGNMENT for DD111", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to add Assignment in Stats", Toast.LENGTH_SHORT).show();
        }
    }
}
