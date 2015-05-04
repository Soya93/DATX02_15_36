package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.StudyTaskActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * Created by SoyaPanda on 15-03-06.
 */
public class CourseDetailedInfoActivity extends ActionBarActivity {
    private TextView kursDetaljer;
    private int selectedCourse;
    private View view;
    private Bundle bundleFromPreviousFragment;
    private Button taskButton;
    private FlowLayout layoutWithinScrollViewOfTasks;

    private String courseCode;
    private String courseName;

    private DBAdapter dbAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_course_details, container, false);
        this.view = rootView;

        initComponents();

        //bundleFromPreviousFragment = this.getArguments();
        //selectedCourse = bundleFromPreviousFragment.getInt("kurs");
        //courseCode = bundleFromPreviousFragment.getString("CourseCode");
        //courseName = bundleFromPreviousFragment.getString("CourseName");
        //Course course = CourseFrag.courseList.get(selectedCourse).get("Courses");

        //Create the database access point but check if the context is null first.
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }

        layoutWithinScrollViewOfTasks.addTasksFromDatabase(dbAdapter, courseCode, AssignmentType.READ);

        fillActivity(courseCode, courseName);

        return rootView;
        }

    public void fillActivity(String courseCode, String courseName) {
        kursDetaljer.setText(courseCode + " - " + courseName);
    }

    public void initComponents(){
        taskButton = (Button) view.findViewById(R.id.taskButton);
        taskButton.setOnClickListener(myOnlyhandler);

        kursDetaljer = (TextView) view.findViewById(R.id.kursDetaljer);

        layoutWithinScrollViewOfTasks = (FlowLayout) view.findViewById(R.id.layoutWithinScrollViewOfTasks);

    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            goToTasks((Button) v);

        }
    };

    public void goToTasks(Button button){

        Intent i = new Intent(this, StudyTaskActivity.class);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);

    }
}
