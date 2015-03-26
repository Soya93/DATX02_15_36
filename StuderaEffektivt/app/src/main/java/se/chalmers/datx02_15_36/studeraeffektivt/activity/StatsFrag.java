package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
*/

import com.github.mikephil.charting.charts.PieChart;

import java.sql.Timestamp;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;

public class StatsFrag extends Fragment {

    private View rootView;

    private TextView hours_spent;
    private TextView hours_left;
    private TextView hours_total;

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
        hours_spent = (TextView) rootView.findViewById(R.id.hours_spent_show);
        setHoursSpent();
        hours_spent = (TextView) rootView.findViewById(R.id.hours_left_show);
        setHoursLeft();
        hours_spent = (TextView) rootView.findViewById(R.id.hours_total_show);
        setHoursTotal();
    }

    private void setHoursSpent(){

    }

    private void setHoursLeft(){

    }

    private void setHoursTotal(){

    }

    private void insertTestDataToDB(){
        //Insert Course.
        long idCourse = dbAdapter.insertCourse("DDD111", "Default Course");
        if(idCourse > 0){
            Toast.makeText(getActivity(),"DDD111 created",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),"Failed to create course in Stats.",Toast.LENGTH_SHORT).show();
        }

        //Insert Sessions for this course.
        long idS1 = dbAdapter.insertSession("DDD111", 60);
        long idS2 = dbAdapter.insertSession("DDD111", 120);
        long idS3 = dbAdapter.insertSession("DDD111", 300);
        long idS4 = dbAdapter.insertSession("DDD111", 30);
        long idS5 = dbAdapter.insertSession("DDD111", 60);
        long idS6 = dbAdapter.insertSession("DDD111", 60);
        if(idS1>0 && idS2>0 && idS3>0 && idS4>0 && idS5>0 && idS6>0){
            Toast.makeText(getActivity(),"Added six sessions to DDD111",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),"Failed to add Sessions in Stats",Toast.LENGTH_SHORT).show();
        }
    }
}
