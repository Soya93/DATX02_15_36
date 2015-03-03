package se.chalmers.datx02_15_36.studeraeffektivt.view;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.CourseActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;


public class CourseDetails extends ActionBarActivity {
    TextView kursDetaljer;
    Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        course = (Course) CourseActivity.courseList.get(getIntent().getIntExtra("kurs", 2)).get("Courses");

        kursDetaljer = (TextView) findViewById(R.id.kursDetaljer);
        fillActivity(course);
    }

    public void fillActivity(Course course){
        kursDetaljer.setText(course.toString());
    }
}
