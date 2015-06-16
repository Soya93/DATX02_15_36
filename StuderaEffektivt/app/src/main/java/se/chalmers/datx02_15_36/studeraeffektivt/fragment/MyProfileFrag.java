/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.database.Cursor;
    import android.graphics.Color;
    import android.net.Uri;
    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.AdapterView;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ListView;
    import android.widget.SimpleAdapter;
    import android.widget.Toast;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    import se.chalmers.datx02_15_36.studeraeffektivt.R;
    import se.chalmers.datx02_15_36.studeraeffektivt.activity.CourseActivity;
    import se.chalmers.datx02_15_36.studeraeffektivt.activity.CourseDetailedInfoActivity;
    import se.chalmers.datx02_15_36.studeraeffektivt.activity.StudyTaskActivity;
    import se.chalmers.datx02_15_36.studeraeffektivt.activity.TechsNTipsActivity;
    import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
    import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
    import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;

public class MyProfileFrag extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private Button addCourseButton;
    private Button seePrevCoursesButton;
    private Button addTaskButton;
    private Button tipButton;
    private Button techniqueButton;
    private ListView listOfCourses;
    private ViewGroup container;
    private View view;

    private boolean isInitialized;

    private EditText editTextCoursecode;
    private EditText editTextCoursename;

    private static List<Map<String,Course>> courseList = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private Bundle bundleToNextFragment;


    //The access point of the database.
    private DBAdapter dbAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /*
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fifthTapFragment.
     */

    public MyProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Create the database access point but check if the context is null first.
        if(getActivity() != null){
            dbAdapter = new DBAdapter(getActivity());
        }
        /*

        editTextCoursecode.getBackground().setColorFilter(Color.parseColor(Constants.primaryDarkColor), PorterDuff.Mode.SRC_ATOP);
        editTextCoursename.getBackground().setColorFilter(Color.parseColor(Constants.primaryDarkColor), PorterDuff.Mode.SRC_ATOP);

        Drawable cursor = getResources().getDrawable(R.drawable.ic_content_remove);
        cursor.setColorFilter(Color.parseColor(Constants.primaryDarkColor), PorterDuff.Mode.SRC_ATOP);
       */

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_my_profile, container, false);
        this.view = rootView;
        this.container = container;
        bundleToNextFragment = new Bundle();
        Log.d("FifthTabFrag ", "currentlyshown = fifthtabfragment");
        initComponents();

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        initOnResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void initComponents() {
        addCourseButton = (Button) view.findViewById(R.id.addCourse);
        addCourseButton.setOnClickListener(myOnlyhandler);

        seePrevCoursesButton= (Button) view.findViewById(R.id.prevCourses);
        seePrevCoursesButton.setOnClickListener(myOnlyhandler);

        addTaskButton= (Button) view.findViewById(R.id.addTask);
        addTaskButton.setOnClickListener(myOnlyhandler);

        tipButton = (Button) view.findViewById(R.id.tips);
        tipButton.setOnClickListener(myOnlyhandler);

        techniqueButton =  (Button) view.findViewById(R.id.techniques);
        techniqueButton.setOnClickListener(myOnlyhandler);

        listOfCourses = (ListView) view.findViewById(R.id.listOfCourses);

        simpleAdapter = new SimpleAdapter(this.getActivity(), courseList, android.R.layout.simple_list_item_1, new String[]{"Courses"}, new int[]{android.R.id.text1});
        listOfCourses.setAdapter(simpleAdapter);
        updateCourses();
        setListOfCourses();


    }

    public void initOnResume(){
        simpleAdapter = new SimpleAdapter(this.getActivity(), courseList, android.R.layout.simple_list_item_1, new String[]{"Courses"}, new int[]{android.R.id.text1});
        listOfCourses.setAdapter(simpleAdapter);
        updateCourses();
        setListOfCourses();
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            goToButtonView((Button) v);

        }
    };

    private void goToButtonView(Button b) {

        int id = b.getId();

        Bundle bundle = new Bundle();
        bundle.putInt("containerId", ((ViewGroup) container.getParent()).getId());

        switch (id) {
            case R.id.addCourse:
                initDialogToAddCourse();
                break;
            case R.id.prevCourses:

                Intent iCourse = new Intent(getActivity(), CourseActivity.class);
                startActivity(iCourse);
                break;
            case R.id.addTask:
                Intent i = new Intent(getActivity(), StudyTaskActivity.class);
                startActivity(i);
                break;
            case R.id.tips:
                Intent it = new Intent(getActivity(), TechsNTipsActivity.class);
                it.putExtra("ActivityTitle", "Studietips");
                startActivity(it);
                break;
            case R.id.techniques:
                Intent its = new Intent(getActivity(), TechsNTipsActivity.class);
                its.putExtra("ActivityTitle", "Studietekniker");
                startActivity(its);
                break;
        }
    }

    public void initDialogToAddCourse(){
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setView(inflater.inflate(R.layout.add_course_dialog, null))
                .setTitle("Lägg till kurs")
                .setPositiveButton("Lägg till", null) //Set to null. We override the onclick
                .setNegativeButton("Avbryt", null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button positive = d.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(editTextCoursecode.getText().toString().trim().length() == 0 || editTextCoursename.getText().toString().trim().length() == 0){
                            Toast toast = Toast.makeText(getActivity(), "Både kursnamn och kurskod måste fylls i!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            long result = dbAdapter.insertCourse(editTextCoursecode.getText().toString(), editTextCoursename.getText().toString());
                            if(result > 0) {
                                Toast toast = Toast.makeText(getActivity(), editTextCoursename.getText().toString() + " tillagd!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getActivity(), editTextCoursename.getText().toString() + "s kurskod är upptagen!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            updateCourses();
                            d.dismiss();
                        }

                    }
                });

                Button negative = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });
            }
        });

        d.show();


        Button okButton = d.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
        Button cancelButton = d.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));

        editTextCoursecode = (EditText) d.findViewById(R.id.codeEditText);
        editTextCoursename = (EditText) d.findViewById(R.id.nameEditText);
    }

    public void updateCourses(){
        courseList.clear();

        Cursor cursor = dbAdapter.getOngoingCourses();
        if (cursor.getCount() > 0){
            String ccode = "";
            String cname = "";
            while (cursor.moveToNext()) {
                ccode = cursor.getString(0);
                cname = cursor.getString(1);
                courseList.add(0,createCourse("Courses", new Course(cname, ccode)));
                simpleAdapter.notifyDataSetChanged();
                listOfCourses.setEnabled(true);

            }
        }else{
            courseList.add(createCourse("Courses", new Course("lägg till en kurs genom att klicka på knappen ovan!", "Du har för närvarande inga kurser, ")));
            listOfCourses.setEnabled(false);
        }

    }

    public void setListOfCourses() {
        listOfCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                HashMap courseMap = (HashMap) parent.getItemAtPosition(position);
                Course course1 = (Course) courseMap.get("Courses");
                goToDetails(course1);
            }
        });

    }

    public void goToDetails(Course course) {

        Intent intent = new Intent(getActivity(), CourseDetailedInfoActivity.class);
        intent.putExtra("CourseCode", course.getCourseCode());
        intent.putExtra("CourseName", course.getCourseName());
        startActivity(intent);

    }

    private HashMap<String, Course> createCourse(String key, Course course) {
        HashMap<String, Course> newCourse = new HashMap<String, Course>();
        newCourse.put(key, course);

        return newCourse;
    }
}
