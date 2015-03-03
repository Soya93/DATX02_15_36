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

import java.util.zip.Inflater;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * A class displaying the available tip of studytechniques or studytips depending on what is chosen.
 */
public class TipActivity extends Fragment {

    private Button tipButton1;
    private Button tipButton2;
    private Button tipButton3;

    private TipDetailedInfoActivity tipDetails;

    private View view;
    private LayoutInflater inflater;
    private ViewGroup container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_tip, container, false);
        this.view = rootView;
        this.inflater = inflater;
        this.container = container;
        initComponentsList(view);
        return rootView;

    }

    private void initComponentsList(View view){

        tipButton1 = (Button) view.findViewById(R.id.tip1);
        tipButton2 = (Button) view.findViewById(R.id.tip2);
        tipButton3 = (Button) view.findViewById(R.id.tip3);
        tipButton1.setOnClickListener(myOnlyhandler);
        tipButton2.setOnClickListener(myOnlyhandler);
        tipButton3.setOnClickListener(myOnlyhandler);
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.tip1:
                    // it was the first button
                    goToTip(tipButton1);
                    break;
                case R.id.tip2:
                    // it was the second button
                    goToTip(tipButton2);
                    break;
            }
        }
    };

    /** Switches to the screen where the information of the selected tip is shown.
     * Called when the user clicks the on a tip-button. */
    public void goToTip(Button b) {

        Fragment fragment = new TipDetailedInfoActivity();

        Bundle bundle = new Bundle();
        bundle.putString("key", (String)b.getText());

        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(((ViewGroup)container.getParent()).getId(), fragment);
        fragmentTransaction.hide(this);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //this.view = inflater.inflate(R.layout.activity_tip_view, container, false);

    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);

        //Instantiating the textview and the buttons with the right text depending on if
        //its a studytechnique or a studytip
        String studyType = getIntent().getStringExtra("studyType");
        TextView tipHeader= (TextView)findViewById(R.id.tip_header);
        tipHeader.setText(studyType);

        String studyTypeSingular = studyType.substring(0,studyType.length()-1);
        Button button1 = (Button) findViewById(R.id.tip1);
        button1.setText(studyTypeSingular + " 1");

        Button button2 = (Button) findViewById(R.id.tip2);
        button2.setText(studyTypeSingular + " 2");

        Button button3 = (Button) findViewById(R.id.tip3);
        button3.setText(studyTypeSingular + " 3");
    }*/





}
