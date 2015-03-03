package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.Activity;

import java.io.InputStream;

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
        initComponents();

        bundleFromPreviousFragment = this.getArguments();
        tipName = bundleFromPreviousFragment.getString("key", "");

        tipViewInfoText.setText(tipName);

        getTipInfoText(tipName);

        return rootView;
    }

    private void initComponents(){
        tipViewInfoText = (TextView) view.findViewById(R.id.tipViewInfoText);
        tipViewHeader = (TextView) view.findViewById(R.id.tipHeader);
    }


    /**
     * A method getting the text describing a certain tip.
     * @param tipName
     * @return
     */
    public String getTipInfoText(String tipName){

        //TODO: Read strings from a file/database? depending on the tipName

        

        return tipName + ": Lorem ipsum dolor sit amet, consectetuer adipiscing elit, " +
                "sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat " +
                "volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper " +
                "suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum " +
                "iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum " +
                "dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim " +
                "qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla " +
                "facilisi. Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet " +
                "doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; " +
                "est usus legentis in iis qui facit eorum claritatem. Investigationes demonstraverunt " +
                "lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, " +
                "qui sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, " +
                "quam nunc putamus parum claram, anteposuerit litterarum formas humanitatis per seacula " +
                "quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, " +
                "fiant sollemnes in futurum.";
    }
}
