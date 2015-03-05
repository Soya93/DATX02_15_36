package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.IO.TipHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * A class displaying the text of an studytechnique tip.
 */
public class TipDetailedInfoActivity extends Fragment {

    private TextView tipViewInfoText;
    private  TextView tipViewHeader;
    private View view;
    private  Bundle bundleFromPreviousFragment;
    private String tipName;
    private TipHandler tipHandler;

    public boolean isActive;

    /**
     * Instansiates the view with the name of the tip as a header and
     * invokes a method for getting the text for the tip
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState)         {

        View rootView = inflater.inflate(R.layout.activity_tip_view, container, false);
        this.view = rootView;


        bundleFromPreviousFragment = this.getArguments();
        tipName = bundleFromPreviousFragment.getString("key", "");
        initComponents();
        tipHandler = new TipHandler(this.getActivity().getApplicationContext());

        tipViewInfoText.setText(getTipInfoText(tipName));

        isActive = true;

        return rootView;
    }

    private void initComponents(){
        tipViewInfoText = (TextView) view.findViewById(R.id.tipViewInfoText);
        tipViewHeader = (TextView) view.findViewById(R.id.tipHeader);

        tipViewHeader.setText(tipName);
    }


    /**
     * A method getting the text describing a certain tip.
     * @param tipName
     * @return
     */
    public String getTipInfoText(String tipName){
        return tipHandler.readFromFile(tipName.replaceAll("ö","o_").replaceAll("Ö","O_").replaceAll("å","a_").replaceAll("Å","A_").replaceAll("ä","_a").replaceAll("Ä","_A"));
    }

    public void destroy(){
        this.onDestroyView();
    }
}
