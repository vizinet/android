package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references
    private EditText mPasswordView, mUsernameView;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Attach objects to UI
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        // Set up the login form
        populateLoginFields();

        // Proceeds to home
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store values at the time of the login attempt.
                String username = mUsernameView.getText().toString();
                String password = mPasswordView.getText().toString();

                //UserDataManager.context = getApplicationContext();
                //UserDataManager.setLastUser(username);

                // Store info about user
                User.username = username;
                User.password = password;
                User.loginTime = new Date();

                // Tell user info is stored
                Toast.makeText(getApplicationContext(), "Credentials stored", Toast.LENGTH_SHORT).show();

                // Open up home screen
                //openHomeScreen();
            }
        });
    }

    // Set credentials of last user
    private void populateLoginFields() {
        // TODO remove
        mUsernameView.setText(Constants.TEST_USERNAME);
        mPasswordView.setText(Constants.TEST_PASSWORD);
    }

    // Start camera-taking page
    private void openHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}

