package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import se.chalmers.datx02_15_36.studeraeffektivt.IO.LogInHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.model.Time;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;

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
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mEmailLoginFormView;
    private SignInButton mPlusSignInButton;
    private View mSignOutButtons;
    private View mLoginFormView;
    boolean hasAlreadyLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        hasAlreadyLoggedIn = sharedPref.getBoolean("hasLoggedIn", false);


        if(!hasAlreadyLoggedIn) {
            mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
            mPlusSignInButton.setSize(SignInButton.SIZE_STANDARD);
            mPlusSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);
            this.setGooglePlusButtonText(mPlusSignInButton, "Logga in med ditt google konto");
            mPlusSignInButton.setOnClickListener(this);
        } else {
            //Do not show this window at all, just kill this screen
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
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
            case R.id.plus_sign_in_button:
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
