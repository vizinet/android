package lar.wsu.edu.airpact_fire;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */
public class SignInActivity extends AppCompatActivity {

    // UI references
    private EditText mPasswordView, mUsernameView;
    private Button mEmailSignInButton, mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Attach objects to UI
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mRegisterButton = (Button) findViewById(R.id.register_button);

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

                // Update user's password and login time.
                // Create on if we need to.
                UserDataManager.setUserData(username, "password", password);
                UserDataManager.setUserData(username, "loginTime", (new Date()).toString());
                // User is now last user we've seen
                UserDataManager.setLastUser(username);

//                Toast.makeText(SignInActivity.this, UserDataManager.getUserData(UserDataManager.getLastUser(), "isAuth"),
//                        Toast.LENGTH_LONG).show();

                // If we know this user is authenticated, move on
                if (Boolean.parseBoolean(UserDataManager.getUserData(UserDataManager.getLastUser(), "isAuth")))
                {
                    Toast.makeText(SignInActivity.this, "Welcome back!", Toast.LENGTH_LONG).show();
                    openHomeScreen();
                    return;
                }

                Toast.makeText(SignInActivity.this, "User is new. Authenticating...", Toast.LENGTH_SHORT).show();

                // Attempt first-time authentication
                AuthenticateManager authenticateManager = new AuthenticateManager();
                authenticateManager.execute();
            }
        });

        // Open web URL
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Post.SERVER_REGISTER_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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

    // Deals with server
    class AuthenticateManager extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Authentication URL
                URL authUrl = new URL(Post.SERVER_AUTH_URL);

                // JSON authentication (send) package
                JSONObject authSendJSON = new JSONObject();
                authSendJSON.put("username", UserDataManager.getLastUser());
                authSendJSON.put("password", UserDataManager.getUserData(UserDataManager.getLastUser(), "password"));
                String sendMessage = authSendJSON.toJSONString(),
                        serverResponse,
                        userKey;
                Boolean isUser;

                // JSON receive package
                JSONObject authReceiveJSON;

                // Establish HTTP connection
                HttpURLConnection authConn = (HttpURLConnection) authUrl.openConnection();

                // Set connection properties
                authConn.setReadTimeout(10000);
                authConn.setConnectTimeout(15000);
                authConn.setRequestMethod("POST");
                authConn.setDoInput(true);
                authConn.setDoOutput(true);
                authConn.setFixedLengthStreamingMode(sendMessage.getBytes().length);
                authConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                authConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                // Connect to server
                authConn.connect();

                // JSON package sent
                OutputStream authOutputStream = new BufferedOutputStream(authConn.getOutputStream());
                authOutputStream.write(sendMessage.getBytes());
                // NOTE: Had an error here before because I didn't flush
                authOutputStream.flush();

                // Server reply
                InputStream in = null; // TODO Error here
                try {
                    in = authConn.getInputStream();
                    int ch;
                    StringBuffer sb = new StringBuffer();
                    while ((ch = in.read()) != -1) {
                        sb.append((char) ch);
                    }

                    serverResponse = sb.toString();
                } catch (IOException e) {
                    throw e;
                }
                if (in != null) {
                    in.close();
                }

                // Parse JSON
                authReceiveJSON = (JSONObject) new JSONParser().parse(serverResponse);

                // Get fields and see if server authenticated us
                isUser = Boolean.parseBoolean((String) authReceiveJSON.get("isUser"));
                if (isUser) {
                    userKey = authReceiveJSON.get("secretKey").toString();
                    //User.postKeys.add(userKey);
                    UserDataManager.setUserData(UserDataManager.getLastUser(), "isAuth", "true");
                } else { // Exit if not a user
                    UserDataManager.setUserData(UserDataManager.getLastUser(), "isAuth", "false");
                    return null;
                }

                authOutputStream.flush();
                authOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void context) {
//        if (Post.DidFail) {
//            Toast.makeText(Post.Context, "Failed to post :(", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(Post.Context, "Post was successful :)", Toast.LENGTH_SHORT).show();
//        }

            // Open up home screen
            if (Boolean.parseBoolean(UserDataManager.getUserData(UserDataManager.getLastUser(),
                    "isAuth"))) {
                Toast.makeText(SignInActivity.this, "Authentication successful", Toast.LENGTH_LONG).show();
                openHomeScreen();
            }
            else
                Toast.makeText(SignInActivity.this, "Could not authenticate user", Toast.LENGTH_SHORT).show();
        }
    }
}
