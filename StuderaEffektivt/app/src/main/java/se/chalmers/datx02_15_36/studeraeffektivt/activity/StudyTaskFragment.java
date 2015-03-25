package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudyTaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudyTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyTaskFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText taskInput;
    private TextView taskOutput;
    private Button addButton;
    private EditText chapterEditText;
    private EditText taskParts;

    public String inputString;

    private Bundle bundleFromPreviousFragment;

    private HashMap<Integer, ArrayList<String>> taskMap = new HashMap<>();
    ArrayList<String> studyTaskList;

    private View view;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudyTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudyTaskFragment newInstance(String param1, String param2) {
        StudyTaskFragment fragment = new StudyTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StudyTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_study_task, container, false);
        this.view = rootView;

        initComponents();

        bundleFromPreviousFragment = this.getArguments();


        Log.d("StudyTaskFragment: ", "Nu skapades ett studytaskfragment");

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void initComponents(){
        addButton = (Button) view.findViewById(R.id.addButton);
        taskInput = (EditText) view.findViewById(R.id.taskInput);
        taskOutput = (TextView) view.findViewById(R.id.taskOutput);
        chapterEditText = (EditText) view.findViewById(R.id.chapterEditText);
        taskParts = (EditText) view.findViewById(R.id.taskParts);

        addButton.setOnClickListener(myOnlyhandler);

        taskOutput.setText("Hej");
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            addTask(Integer.parseInt(chapterEditText.getText().toString()), taskInput.getText().toString(), taskParts.getText().toString());

        }
    };

    public void addTask(int chapter, String taskString, String taskParts){
        //StudyTask studytask = new StudyTask(taskInput.getText().toString());
        //ArrayList<String> studyTaskList = studytask.getlist();

        ArrayList<String> stringlist = new ArrayList();
        String[] seperateLine;
        String[] seperateComma;

        String[] seperateTaskParts;
        seperateTaskParts = taskParts.split("");

        taskString.replaceAll("\\s+","");

        int start;
        int end;

        if(taskMap.containsKey(chapter)){
            studyTaskList = taskMap.get(chapter);
        }
        else{
            studyTaskList = new ArrayList<>();
        }

        if (taskString.contains(",")) {

            seperateComma = taskString.split(",");
        } else {
            seperateComma = new String[1];
            seperateComma[0] = taskString;
        }


        for (int a = 0; a < seperateComma.length; a++) {
            if (seperateComma[a].contains("-")) {

                seperateLine = seperateComma[a].split("-");
                start = Integer.parseInt(seperateLine[0]);
                end = Integer.parseInt(seperateLine[seperateLine.length - 1]);

                for (int i = start; i <= end; i++) {
                        stringlist.add("" + i);
                }
            } else {
                    stringlist.add(seperateComma[a]);
            }
        }

        if(taskParts!=""){
            for(String s : seperateTaskParts){
               for(String s2 : stringlist){
                    s2 = s2 + s;
                   if(!studyTaskList.contains(s2)){
                       studyTaskList.add(s2);
                   }
                }
            }
        }

        taskMap.put(chapter, studyTaskList);

        String taskMapString = taskMap.toString();

        Log.d("String för taskMap: ",taskMapString);

        String uppgifter = "";

        for(String s: studyTaskList){
            uppgifter += s + " ";
        }

        taskOutput.setText(uppgifter);

    }

    public void deleteTask(String taskString){
        //if()
    }

   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

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

}
