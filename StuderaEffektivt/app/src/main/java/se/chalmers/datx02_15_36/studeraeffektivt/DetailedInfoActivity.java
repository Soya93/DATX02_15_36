package se.chalmers.datx02_15_36.studeraeffektivt;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.IO.TipHandler;


public class DetailedInfoActivity extends ActionBarActivity {

    private TextView tipViewInfoText;
    private TextView tipViewHeader;
    private String tipName;
    private TipHandler tipHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_view);
        tipName = getIntent().getStringExtra("key");
        Log.i("tipName", tipName);
        initComponents();
        tipHandler = new TipHandler(getApplicationContext());
        tipViewInfoText.setText(getTipInfoText(tipName));

    }

    private void initComponents() {
        tipViewInfoText = (TextView) findViewById(R.id.tipViewInfoText);
        tipViewHeader = (TextView) findViewById(R.id.tipHeader);

        tipViewHeader.setText(tipName);
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

}
