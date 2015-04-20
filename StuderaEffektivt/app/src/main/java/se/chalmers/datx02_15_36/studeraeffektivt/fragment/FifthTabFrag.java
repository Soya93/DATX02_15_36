package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

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

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FifthTabFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FifthTabFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FifthTabFrag extends Fragment {

    //The access point of the database.
    private DBAdapter dbAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button tipButton;
    private Button courseButton;
    private Button settingsButton;

    private ViewGroup container;
    private View view;
    private boolean hasInit = false;


    private Fragment currentlyShown = this;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fifthTapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FifthTabFrag newInstance(String param1, String param2) {
        FifthTabFrag fragment = new FifthTabFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public FifthTabFrag() {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_fifth_tab, container, false);
        this.view = rootView;
        this.container = container;
        currentlyShown = this;
        Log.d("FifthTabFrag ", "currentlyshown = fifthtabfragment");
        initComponents();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    /*@Override
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

    private void initComponents() {
        tipButton = (Button) view.findViewById(R.id.button1);
        tipButton.setOnClickListener(myOnlyhandler);
        courseButton = (Button) view.findViewById(R.id.button11);
        courseButton.setOnClickListener(myOnlyhandler);
        settingsButton = (Button) view.findViewById(R.id.button12);
        settingsButton.setOnClickListener(myOnlyhandler);
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {

            goToButtonView((Button) v);

        }
    };

    private void goToButtonView(Button b) {

        String buttonText = b.getText().toString();

        Fragment fragment;

        Bundle bundle = new Bundle();
        //bundle.putString("key", (String)b.getText());
        bundle.putInt("containerId", ((ViewGroup) container.getParent()).getId());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (buttonText) {
            case "Studietips och studiestilar":

                fragment = new TipFrag();
                fragment.setArguments(bundle);
                fragmentTransaction.add(((ViewGroup) container.getParent()).getId(), fragment, "tipfragment");
                //fragmentTransaction.replace(((ViewGroup) container.getParent()).getId(), fragment);
                currentlyShown = fragment;
                break;
            case "Kurser":
                fragment = new CourseFrag();
                fragment.setArguments(bundle);
                fragmentTransaction.add(((ViewGroup) container.getParent()).getId(), fragment, "coursefragment");
                //fragmentTransaction.replace(((ViewGroup) container.getParent()).getId(), fragment);
                currentlyShown = fragment;
                break;
            case "Inställningar":
                fragment = new MyProfileFrag();
                fragment.setArguments(bundle);
                fragmentTransaction.add(((ViewGroup) container.getParent()).getId(), fragment, "myprofilefrag");
                //fragmentTransaction.replace(((ViewGroup) container.getParent()).getId(), fragment);
                currentlyShown = fragment;
                break;
        }
        Log.d("FifthTabFrag ", "currentlyshown = " + currentlyShown);
        fragmentTransaction.hide(this);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public Fragment getCurrentlyShown() {
        return currentlyShown;
    }

    public boolean hasInit(){
        return hasInit;
    }

}
