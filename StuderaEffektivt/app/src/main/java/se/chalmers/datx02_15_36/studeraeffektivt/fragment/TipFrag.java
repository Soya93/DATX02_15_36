package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * A class displaying the available tip of studytips depending on what is chosen.
 */
public class TipFrag extends Fragment {

    private List<Button> buttonList;
    private TextView header;

    private View view;
    private ViewGroup container;

    private Bundle bundleFromPreviousFragment;
    private int containerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_tip, container, false);
        this.view = rootView;
        this.container = container;

        initComponentsList();
        /*bundleFromPreviousFragment = this.getArguments();
        containerId = bundleFromPreviousFragment.getInt("containerId");*/
        containerId = getActivity().getWindow().getDecorView().findViewById(android.R.id.content).getId();

        return rootView;
    }

    private void initComponentsList() {

        buttonList = new ArrayList<Button>();

        buttonList.add((Button) view.findViewById(R.id.button1));
        buttonList.add((Button) view.findViewById(R.id.button2));
        buttonList.add((Button) view.findViewById(R.id.button3));
        buttonList.add((Button) view.findViewById(R.id.button4));
        buttonList.add((Button) view.findViewById(R.id.button5));
        buttonList.add((Button) view.findViewById(R.id.button6));
        buttonList.add((Button) view.findViewById(R.id.button7));
        buttonList.add((Button) view.findViewById(R.id.button8));
        buttonList.add((Button) view.findViewById(R.id.button9));
        buttonList.add((Button) view.findViewById(R.id.button10));

        for (Button b : buttonList) {
            b.setOnClickListener(myOnlyhandler);
        }
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            goToTip((Button) v);
        }
    };

    /**
     * Switches to the screen where the information of the selected tip is shown.
     * Called when the user clicks the on a tip-button.
     */
    public void goToTip(Button b) {

        Fragment fragment = new TipDetailedInfoFrag();

        Bundle bundle = new Bundle();
        bundle.putString("key", (String) b.getText());
        bundle.putString("PreviousTitle", "Studietips");
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(containerId, fragment, "detailedtipfragment");
        fragmentTransaction.hide(this);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        getActivity().finish();
    }
}