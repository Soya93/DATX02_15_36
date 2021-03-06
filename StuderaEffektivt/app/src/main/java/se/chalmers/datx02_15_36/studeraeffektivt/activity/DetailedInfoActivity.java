package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.IO.TipHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class DetailedInfoActivity extends ActionBarActivity {

    private TextView tipViewInfoText;
    private TextView tipViewHeader;
    private String tipName;
    private TipHandler tipHandler;
    private ImageView tipImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_view);
        tipName = getIntent().getStringExtra("key");
        Log.i("tipName", tipName);
        initComponents();
        tipHandler = new TipHandler(getApplicationContext());
        tipViewInfoText.setText(getTipInfoText(tipName));
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initComponents() {
        tipViewInfoText = (TextView) findViewById(R.id.tipViewInfoText);
        tipViewHeader = (TextView) findViewById(R.id.tipHeader);
        tipImage = (ImageView) findViewById(R.id.tipImage);

        tipViewHeader.setText(tipName);
        setTipImage(tipName);

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
    public void setTipImage(String tipName){
        tipImage.setImageBitmap(tipHandler.readFromImageFile(tipName.replaceAll("ö", "o_").replaceAll("Ö", "O_").replaceAll("å", "a_").replaceAll("Å", "A_").replaceAll("ä", "_a").replaceAll("Ä", "_A").replaceAll(" ", "_")));
    }

}
