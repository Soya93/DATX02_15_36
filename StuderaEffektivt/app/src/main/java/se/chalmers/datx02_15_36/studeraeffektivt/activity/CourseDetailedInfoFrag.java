package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private Button taskButton;

    private ViewGroup container;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_course_details, container, false);
        bundleFromPreviousFragment = this.getArguments();
        containerId = bundleFromPreviousFragment.getInt("containerId");
        selectedCourse = bundleFromPreviousFragment.getInt("kurs");
        Course course = (Course) CourseFrag.courseList.get(selectedCourse).get("Courses");
        kursDetaljer = (TextView) view.findViewById(R.id.kursDetaljer);
        initComponents();
        fillActivity(course);
        this.container = container;
        return view;
    }

    public void fillActivity(Course course) {
        kursDetaljer.setText(course.toString());
    }

    public void initComponents(){
        taskButton = (Button) view.findViewById(R.id.taskButton);
        taskButton.setOnClickListener(myOnlyhandler);
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            goToTasks((Button) v);

        }
    };

    public void goToTasks(Button button){


        Fragment fragment;

        //Bundle bundle = new Bundle();
        //bundle.putString("key", (String)b.getText());
        //bundle.putInt("containerId", ((ViewGroup) container.getParent()).getId());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new StudyTaskFragment();
        //fragment.setArguments(bundle);
        fragmentTransaction.add(((ViewGroup) container.getParent()).getId(), fragment);
        //fragmentTransaction.replace(((ViewGroup) container.getParent()).getId(), fragment);
        //currentlyShown = fragment;

       // fragmentTransaction.hide(this);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
