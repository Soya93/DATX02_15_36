package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.common.SignInButton;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarChoiceItem;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarModel;
import se.chalmers.datx02_15_36.studeraeffektivt.model.CalendarsFilterItem;
import se.chalmers.datx02_15_36.studeraeffektivt.util.CalendarUtils;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */

public class LoginActivity extends Activity implements OnClickListener{
        private SharedPreferences sharedPref;
        private String prefName = "LogInPref";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.

    private Button mPlusSignInButton;

    boolean hasAlreadyLoggedIn;
    private CalendarModel calendarModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        hasAlreadyLoggedIn = sharedPref.getBoolean("hasLoggedIn", false);

        calendarModel = new CalendarModel();



        if(!hasAlreadyLoggedIn) {
            mPlusSignInButton = (Button) findViewById(R.id.sign_in_button);

            mPlusSignInButton.setOnClickListener(this);
        } else {
            //Do not show this window at all, just kill this screen
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }

        //Find the users home calendar
        sharedPref = getSharedPreferences("calendarPref", Context.MODE_PRIVATE);
        calendarModel.findHomeCal(getContentResolver(), sharedPref);

    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sign_in_button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("hasLoggedIn", true);
                editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        hasAlreadyLoggedIn = sharedPref.getBoolean("hasLoggedIn", false);

        if(hasAlreadyLoggedIn) {
           this.finish();

        }
    }


}
