package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * A class displaying the text of an studytechnique tip.
 */
public class TipDetailedInfoActivity extends ActionBarActivity {

    TextView tipViewInfoText;

    /**
     * Instansiates the view with the name of the tip as a header and
     * invokes a method for getting the text for the tip
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_view);

        String tipName = getIntent().getStringExtra("tipName");
        TextView tipHeader= (TextView)findViewById(R.id.tipHeader);
        tipHeader.setText(tipName);

       tipViewInfoText = (TextView)findViewById(R.id.tipViewInfoText);
        tipViewInfoText.setText(getTipInfoText(tipName));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tip_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
