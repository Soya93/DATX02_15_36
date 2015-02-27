package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.os.Bundle;
<<<<<<< HEAD
import android.app.Activity;

=======
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
>>>>>>> calendar
import se.chalmers.datx02_15_36.studeraeffektivt.R;

public class Statistics extends Fragment {

    @Override
<<<<<<< HEAD
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
=======
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_statistics, container, false);
        //Tillfällig kod för testning
        //Intent intent = new Intent(this, CourseActivity.class);
        //startActivity(intent);
        //Slut på tillfällig kod för testning
        return rootView;
>>>>>>> calendar
    }



}
