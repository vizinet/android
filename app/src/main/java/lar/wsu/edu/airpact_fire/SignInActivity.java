package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */
public class SignInActivity extends AppCompatActivity {

    // UI references
    private EditText mPasswordView, mUsernameView;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Attach objects to UI
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        // XML Stuff -- create XML if necessary, start with
        UserDataManager.init(getApplicationContext());

        // Set up the login form
        populateLoginFields();

        // Proceeds to home
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store values at the time of the login attempt.
                String username = mUsernameView.getText().toString();
                String password = mPasswordView.getText().toString();

                // Update user's password and login time
                boolean didUserExist = UserDataManager.setUserData(username, "password", password);
                UserDataManager.setUserData(username, "loginTime", (new Date()).toString());
                // User is now last user we've seen
                UserDataManager.setLastUser(username);

                // TODO: Figure out why it's not creating the new user passwords


                // TODO: Remove (deprecated)
                // Store info about user
                User.username = username;
                User.password = password;
                User.loginTime = new Date();

                // Determine welcome banner
                if (didUserExist) Toast.makeText(getApplicationContext(), "Credentials saved. Welcome back!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(getApplicationContext(), "Credentials saved. Hey, new guy!", Toast.LENGTH_SHORT).show();

                // Open up home screen
                openHomeScreen();
            }
        });
    }

    // Set credentials of last user
    private void populateLoginFields() {
        String lastUser = UserDataManager.getLastUser();
        String lastPassword = (String) UserDataManager.getUserData(lastUser, "password");

        mUsernameView.setText(lastUser);
        mPasswordView.setText(lastPassword);
    }

    // Start camera-taking page
    private void openHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}

