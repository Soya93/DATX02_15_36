package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;

/**
 * A class displaying the available tip of studytechniques or studytips depending on what is chosen.
 */
public class TipActivity extends ActionBarActivity {

    @Override
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tips, menu);
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

    /** Switches to the screen where the information of the selected tip is shown.
     * Called when the user clicks the on a tip-button. */
    public void goToTip(View view) {
        Button b = (Button)view;
        String tipName = b.getText().toString();
        Intent intent = new Intent(this, TipDetailedInfoActivity.class);
        intent.putExtra("tipName", tipName);
        startActivity(intent);
    }

}
