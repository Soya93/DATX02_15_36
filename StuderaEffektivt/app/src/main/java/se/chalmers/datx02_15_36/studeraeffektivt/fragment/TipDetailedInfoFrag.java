package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.IO.TipHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;

/**
 * A class displaying the text of an studytechnique tip.
 */
public class TipDetailedInfoFrag extends Fragment {

    private TextView tipViewInfoText;
    private TextView tipViewHeader;
    private ImageView tipImage;
    private View view;
    private Bundle bundleFromPreviousFragment;
    private String tipName;
    private String previousTitle;
    private String filename;
    private TipHandler tipHandler;
    android.support.v7.app.ActionBar actionBar;

  /**
   * Instansiates the view with the name of the tip as a header and
   * invokes a method for getting the text for the tip
   *
   * @param savedInstanceState
   */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_tip_view, container, false);
        this.view = rootView;
        bundleFromPreviousFragment = this.getArguments();
        tipName = bundleFromPreviousFragment.getString("key", "");
        previousTitle = bundleFromPreviousFragment.getString("PreviousTitle", "");

        Log.i("OnCreate prev", previousTitle);

        filename = tipName;

        filename = tipName.replace("-", "_");
        filename = filename.toLowerCase();

        initComponents();
        tipHandler = new TipHandler(this.getActivity().getApplicationContext());
        tipViewInfoText.setText(getTipInfoText(filename));
        tipViewHeader.setText(tipName);

        actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(tipName);

        return rootView;
    }

    private void initComponents() {
        tipViewInfoText = (TextView) view.findViewById(R.id.tipViewInfoText);
        tipViewHeader = (TextView) view.findViewById(R.id.tipHeader);
        tipImage = (ImageView) view.findViewById(R.id.tipImage);

        String uri = "@drawable/" + filename + "_.png";
        Log.i("drawable", uri);
        int imageResource = getResources().getIdentifier(filename+"_", "drawable", getActivity().getPackageName());
        tipImage.setImageResource(imageResource);
    }


    /**
     * A method getting the text describing a certain tip.
     *
     * @param tipName
     * @return
     */
    public String getTipInfoText(String tipName) {
        return tipHandler.readFromFile(tipName.replaceAll("ö", "o_").replaceAll("Ö", "O_").replaceAll("å", "a_").replaceAll("Å", "A_").replaceAll("ä", "_a").replaceAll("Ä", "_A"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Prev", previousTitle);
        actionBar.setTitle(previousTitle);
    }
}
