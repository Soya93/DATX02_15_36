package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.common.SignInButton;

import se.chalmers.datx02_15_36.studeraeffektivt.IO.LogInHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.R;
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
        LogInHandler logInHandler;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logInHandler = new LogInHandler(this.getApplicationContext());

        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
        mPlusSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mPlusSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        mPlusSignInButton.setOnClickListener(this);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.getBackground().setColorFilter(Color.parseColor(Constants.secondaryColor), PorterDuff.Mode.SRC_IN);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.getBackground().setColorFilter(Color.parseColor(Constants.secondaryColor), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.plus_sign_in_button:

                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();

                logInHandler.writeToFile(email, password);

                email = logInHandler.getEmail();
                password = logInHandler.getPassword();

                //TODO: Actually verify and log in to the account specified.

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        }
    }
}
