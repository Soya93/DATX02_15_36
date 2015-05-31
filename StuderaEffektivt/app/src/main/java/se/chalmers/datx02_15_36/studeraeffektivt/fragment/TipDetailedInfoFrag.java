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
        if(filename.contains("-")){
            filename = filename.replace("-", "_");
        }
        filename = filename.toLowerCase();

        initComponents();


        actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(tipName);

        return rootView;
    }

    private void initComponents() {
        tipViewInfoText = (TextView) view.findViewById(R.id.tipViewInfoText);
        tipViewHeader = (TextView) view.findViewById(R.id.tipHeader);
        tipImage = (ImageView) view.findViewById(R.id.tipImage);

        tipHandler = new TipHandler(this.getActivity().getApplicationContext());

        filename = filename+"_";

        tipViewInfoText.setText(getTipInfoText(filename));
        tipViewHeader.setText(tipName);

        if(filename.contains(" ")){
            filename = filename.replace(" ", "_");
        }

        if(filename.contains(",")){
            filename = filename.replace(",", "_");
        }

        filename = convertSwedishLetters(filename);

        int imageResource = getResources().getIdentifier(filename, "drawable", getActivity().getPackageName());
        tipImage.setImageResource(imageResource);
    }


    private String convertSwedishLetters(String text){
        return text.replaceAll("ö", "o_").replaceAll("Ö", "O_").replaceAll("å", "a_").
                replaceAll("Å", "A_").replaceAll("ä", "_a").replaceAll("Ä", "_A");
    }


    /**
     * A method getting the text describing a certain tip.
     *
     * @param tipName
     * @return
     */
    public String getTipInfoText(String tipName) {
        return tipHandler.readFromFile(convertSwedishLetters(tipName));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Prev", previousTitle);
        actionBar.setTitle(previousTitle);
    }
}
