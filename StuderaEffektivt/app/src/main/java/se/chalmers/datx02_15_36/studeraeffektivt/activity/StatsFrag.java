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

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;

public class StatsFrag extends Fragment {

    private View rootView;

    private TextView hoursSpent;
    private TextView hoursLeft;
    private TextView hoursTotal;
    private Spinner spinner;

    private DBAdapter dbAdapter;

    private String currCourse = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_stats, container, false);
        if (getActivity() != null) {
            dbAdapter = new DBAdapter(getActivity());
        }

        //insertTestDataToDB();
        instantiateView();

        return rootView;
    }

    private void instantiateView(){
        spinner = (Spinner) rootView.findViewById(R.id.spinner_stats);
        spinner.setSelection(0);
        setCourses();

        hoursSpent = (TextView) rootView.findViewById(R.id.hours_spent_show);
        setHoursSpent();
        hoursLeft = (TextView) rootView.findViewById(R.id.hours_left_show);
        setHoursLeft();
        hoursTotal = (TextView) rootView.findViewById(R.id.hours_total_show);
        setHoursTotal();
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
        Log.i("DB", "Spinner: "+spinner);
        Log.i("DB", "SelectedItem: "+spinner.getSelectedItem());
        String temp = spinner.getSelectedItem().toString();
        String[] parts = temp.split("-");
        this.currCourse = parts[0];
    }

    private void setHoursSpent(){
        setSelectedCourse();
        Log.d("currCourse in stats", "_"+currCourse+"_");
        String timeSpent = " "+(dbAdapter.getSpentTime(currCourse)/60)+" h";
        hoursSpent.setText(timeSpent);
    }

    private void setHoursLeft(){
        String timeLeft = " "+((dbAdapter.getTimeOnCourse(currCourse)/60)-(dbAdapter.getSpentTime(currCourse)/60))+" h";
        hoursLeft.setText(timeLeft);
    }

    private void setHoursTotal(){
        String timeTotal = " "+(dbAdapter.getTimeOnCourse(currCourse)/60)+" h";
        Log.i("DB", "time total: "+timeTotal);
        hoursTotal.setText(timeTotal);
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
    }
}
