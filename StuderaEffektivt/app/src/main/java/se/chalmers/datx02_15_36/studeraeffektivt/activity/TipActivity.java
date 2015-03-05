package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * A class displaying the available tip of studytechniques or studytips depending on what is chosen.
 */
public class TipActivity extends Fragment {

    private List<Button> buttonList;

    private View view;
    private ViewGroup container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_tip, container, false);
        this.view = rootView;
        this.container = container;
        initComponentsList(view);
        return rootView;


    }

    private void initComponentsList(View view){

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
        buttonList.add((Button) view.findViewById(R.id.button11));
        buttonList.add((Button) view.findViewById(R.id.button12));
        buttonList.add((Button) view.findViewById(R.id.button13));
        buttonList.add((Button) view.findViewById(R.id.button14));
        buttonList.add((Button) view.findViewById(R.id.button15));
        buttonList.add((Button) view.findViewById(R.id.button16));
        buttonList.add((Button) view.findViewById(R.id.button17));
        buttonList.add((Button) view.findViewById(R.id.button18));

        for(Button b : buttonList){
            b.setOnClickListener(myOnlyhandler);
        }
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            goToTip((Button) v);
        }
    };

    /** Switches to the screen where the information of the selected tip is shown.
     * Called when the user clicks the on a tip-button. */
    public void goToTip(Button b) {

        Fragment fragment = new TipDetailedInfoActivity();

        Bundle bundle = new Bundle();
        bundle.putString("key", (String)b.getText());

        fragment.setArguments(bundle);

        FragmentManager fragmentManager = this.getFragmentManager();
        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(((ViewGroup)container.getParent()).getId(),fragment);
        //fragmentTransaction.replace(((ViewGroup)container.getParent()).getId(), fragment);
        fragmentTransaction.hide(this);
        //fragmentTransaction.show(fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}