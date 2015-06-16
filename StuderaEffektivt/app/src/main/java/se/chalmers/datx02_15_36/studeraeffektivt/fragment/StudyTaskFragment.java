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

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
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

    //Components in the view
    private EditText taskInput;
    private TextView taskOutput;
    private Button addButton;
    private EditText chapterEditText;
    private EditText taskParts;

    //The bundle given from the fragment before this
    private Bundle bundleFromPreviousFragment;
    private Course course;

    //HashMap with the chapters as keys and a list of tasks as the elements.
    private HashMap<Integer, ArrayList<StudyTask>> taskMap = new HashMap<>();
    ArrayList<StudyTask> studyTaskList;

    private View view;

    //The access point of the database.
    private DBAdapter dbAdapter;

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

        //Create the database access point but check if the context is null first.
        if (getActivity() != null) {
            dbAdapter = new DBAdapter(getActivity());
        }


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


    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            addTask(Integer.parseInt(chapterEditText.getText().toString()), taskInput.getText().toString(), taskParts.getText().toString());

        }
    };

    //Meod för att lägga till en uppgift

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

        //Kolla om kapitlet man vill lägga till i redan finns bland uppgifterna, om inte: skapa en nyckel för detta kapitel

        if(taskMap.containsKey(chapter)){
            studyTaskList = taskMap.get(chapter);
        }
        else{
            studyTaskList = new ArrayList<>();
        }

        //Kollar om det finns kommatecken i input för uppgifter och separerar i så fall stringen så att alla element hamnar separat
        if (taskString.contains(",")) {

            seperateComma = taskString.split(",");  //Delar upp stringen till en array med elementen mellan kommatecknerna
        } else {
            seperateComma = new String[1];
            seperateComma[0] = taskString;
        }

        //Kollar elementen var för sig och ser om de är ett spann av uppgifter att lägga till 1-3 gör så att 1, 2 och 3 läggs till
        for (int a = 0; a < seperateComma.length; a++) {
            if (seperateComma[a].contains("-")) {

                seperateLine = seperateComma[a].split("-");   //Delar upp stringen till en array med elementen mellan bindesstrecken
                start = Integer.parseInt(seperateLine[0]);    //Start och end är intervallet för de element som skall läggas till
                end = Integer.parseInt(seperateLine[seperateLine.length - 1]);

                for (int i = start; i <= end; i++) {
                        stringlist.add("" + i);
                }
            } else {
                    stringlist.add(seperateComma[a]);
            }
        }

        //Lägger till deluppgifter om input för detta finns, tex a, b c.
      /* if(seperateTaskParts.length > 1){
            String elementToAdd;
            for(int i = 1; i< seperateTaskParts.length; i++){       //För varje deluppgift
               for(String s2 : stringlist){                         //För varje vihuv uppgift
                    elementToAdd = s2 + seperateTaskParts[i];       //Sätt ihop dessa Huvuduppgift 1 och deluppgift a blir 1a
                   if(!studyTaskList.contains(elementToAdd)){
                       studyTaskList.add(new StudyTask(getActivity(),
                                             bundleFromPreviousFragment.getString("CourseCode"),
                                             chapter,
                                             elementToAdd));             //Lägger till dessa i listan för det aktuella kapitlet och elementet inte finns.
                   }
                }
            }
       }
       //lägger till huvuduppgifterna då deluppgifter inte finns
        else{
            for(String s : stringlist){         //För varje huvuduppgift
                if(!studyTaskList.contains(s))  //Lägg till om den inte redan finns
                studyTaskList.add(new StudyTask(getActivity(),
                        bundleFromPreviousFragment.getString("CourseCode"),
                        chapter,
                        s));
            }
        }*/

        taskMap.put(chapter, studyTaskList);        //Uppdatera Hashmappen för nyckeln för kapitlet

        String taskMapString = taskMap.toString();

        Log.d("String för taskMap: ",taskMapString);

        }

    public void deleteTask(String taskString){
        //if()
    }

    public void addToDatabase(){

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
