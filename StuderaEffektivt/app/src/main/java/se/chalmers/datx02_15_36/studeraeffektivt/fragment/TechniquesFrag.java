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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * A class displaying the available tip of studytechniques depending on what is chosen.
 */
public class TechniquesFrag extends Fragment {

    private List<Button> buttonList;

    private View view;
    private ViewGroup container;

    private Bundle bundleFromPreviousFragment;
    private int containerId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_techniques, container, false);
        this.view = rootView;
        this.container = container;

        initComponentsList();
        containerId = getActivity().getWindow().getDecorView().findViewById(android.R.id.content).getId();

        return rootView;
    }

    private void initComponentsList() {

        buttonList = new ArrayList<Button>();

        buttonList.add((Button) view.findViewById(R.id.button11));
        buttonList.add((Button) view.findViewById(R.id.button12));
        buttonList.add((Button) view.findViewById(R.id.button13));
        buttonList.add((Button) view.findViewById(R.id.button14));
        buttonList.add((Button) view.findViewById(R.id.button15));
        buttonList.add((Button) view.findViewById(R.id.button16));
        buttonList.add((Button) view.findViewById(R.id.button17));
        buttonList.add((Button) view.findViewById(R.id.button18));

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
        bundle.putString("PreviousTitle", "Studietekniker");

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