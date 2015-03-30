package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * Created by SoyaPanda on 15-03-06.
 */
public class CourseDetailedInfoFrag extends Fragment {
    private TextView kursDetaljer;
    private int selectedCourse;
    private View view;
    private Bundle bundleFromPreviousFragment;
    private Button taskButton;
    private ScrollView scrollViewOfTasks;
    private FlowLayout layoutWithinScrollViewOfTasks;

    private String courseCode;

    private int containerId;
    private ViewGroup container;

    private DBAdapter dbAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_course_details, container, false);
        this.view = rootView;
        //this.container = container;

        initComponents();

        bundleFromPreviousFragment = this.getArguments();
        //containerId = bundleFromPreviousFragment.getInt("containerId");
        selectedCourse = bundleFromPreviousFragment.getInt("kurs");
        courseCode = bundleFromPreviousFragment.getString("CourseCode");
        Course course = (Course) CourseFrag.courseList.get(selectedCourse).get("Courses");

        //Create the database access point but check if the context is null first.
        if (this != null) {
            dbAdapter = new DBAdapter(getActivity());
        }

        addTasksFromDatabase();

        fillActivity(course);

        return rootView;
        }

    public void fillActivity(Course course) {
        kursDetaljer.setText(course.toString());
    }

    public void initComponents(){
        taskButton = (Button) view.findViewById(R.id.taskButton);
        taskButton.setOnClickListener(myOnlyhandler);

        kursDetaljer = (TextView) view.findViewById(R.id.kursDetaljer);

        scrollViewOfTasks = (ScrollView) view.findViewById(R.id.scrollViewOfTasks);
        layoutWithinScrollViewOfTasks = (FlowLayout) view.findViewById(R.id.layoutWithinScrollViewOfTasks);

    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            goToTasks((Button) v);

        }
    };

    public void goToTasks(Button button){

        Intent i = new Intent(getActivity(), StudyTaskActivity.class);
        i.putExtra("CourseCode", courseCode);
        startActivity(i);

    }

    public void addTasksFromDatabase(){

        Cursor cursor = dbAdapter.getAssignments();

        if(cursor!=null) {
            while (cursor.moveToNext()) {

                AssignmentStatus assignmentStatus;
                AssignmentType assignmentType;
                if(cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())){
                    assignmentStatus = AssignmentStatus.DONE;
                }
                else{
                    assignmentStatus = null;
                }
                if(cursor.getString(cursor.getColumnIndex("type")).equals(AssignmentType.READ)){
                    assignmentType = AssignmentType.READ;
                }
                else{
                    assignmentType = AssignmentType.OTHER;
                }
                //TODO: Skall göra så att denna lägger in i olika listor baserat på om uppgiften är gjort eller inte och läggas upp efter det. Vill på något sätt även sortera detta.     Fixa
                layoutWithinScrollViewOfTasks.addView(new StudyTask(
                        getActivity(),
                        cursor.getString(cursor.getColumnIndex("_ccode")),
                        cursor.getInt(cursor.getColumnIndex("chapter")),
                        cursor.getString(cursor.getColumnIndex("assNr")),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("startPage"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("stopPage"))),
                        dbAdapter,
                        assignmentType,
                        assignmentStatus));                                             //TODO: sätta in rätt bool från databasen
            }
        }
    }
}
