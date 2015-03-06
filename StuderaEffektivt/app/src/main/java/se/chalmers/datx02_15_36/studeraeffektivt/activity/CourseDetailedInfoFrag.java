package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;

/**
 * Created by SoyaPanda on 15-03-06.
 */
public class CourseDetailedInfoFrag extends Fragment {
    private TextView kursDetaljer;
    private int selectedCourse;
    private View view;
    private Bundle bundleFromPreviousFragment;
    private int containerId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_course_details, container, false);
        bundleFromPreviousFragment = this.getArguments();
        containerId = bundleFromPreviousFragment.getInt("containerId");
        selectedCourse = bundleFromPreviousFragment.getInt("kurs");
        Course course = (Course) CourseFrag.courseList.get(selectedCourse).get("Courses");
        kursDetaljer = (TextView) view.findViewById(R.id.kursDetaljer);
        fillActivity(course);
        return view;
    }

    public void fillActivity(Course course) {
        kursDetaljer.setText(course.toString());
    }
}
