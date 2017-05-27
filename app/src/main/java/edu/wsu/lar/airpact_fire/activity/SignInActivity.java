package edu.wsu.lar.airpact_fire.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.wsu.lar.airpact_fire.Reference;
import edu.wsu.lar.airpact_fire.data.manager.AppDataManager;
import edu.wsu.lar.airpact_fire.data.Post;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.manager.AppManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import io.realm.RealmFieldType;
import lar.wsu.edu.airpact_fire.R;

// TODO: Address all below to-do statements

// TODO: See if we can get AppManager to handle both the DataManager and ServerManager
// TODO: AppManager: Method which adds service to monitor app state (namely, exit, so we can call onAppEnd)
// TODO: Add copyright/disclaimer/license code (Luke Weber, WSU, LAR, etc.) to each code file in AIRPACT-Fire, along with authorship information in each file
// TODO: Look into making custom image-capture activity
// TODO: Adapt to new research-based, clean design scheme
// TODO: Splash screen
// TODO: Remove AppDataManager and PostDataManager
// TODO: Add "alpha" print to logo and banner
// TODO: Have auto-fill for login page and post page
// TODO: Better image storage - perhaps Make separate files for the image, linked to by the <image /> tag by the user (e.g. "test_image.jpg")

// TODO: Internet status (color-coded) on home, view gallery option (web browser), as well as last login time and other stats
// TODO: Custom Toast display, to make it more obvious to user
// TODO: More responsive buttons
// TODO: "Last logged in X days ago" on home screen
// TODO: Figure out why picture details activity doesn't repopulate estimated visual range and description.
//  This way we'll only deal with the image when we take a new one, view it, or post it.
// TODO: Add notification when we have connection to server, and not just internet access. Although,
//  we still want to know about internet access so we can know when to queue posts? We could just check
//  to see if
// TODO: Add notifications for when server comes up. Have a batch of checks and actions done by the app occasionally,
//  say, every 3 hours, like posting for backlogged posts. Also have frequent checks while app is running
//  that give toast/notifications when server is up. Maybe do something with notifications as well.
// TODO: Show post trends (location, time, etc.)
// TODO: Allow user to view post coordinates in Google Maps
// TODO: When queued post is submitted, don't create a new post entirely for SQL database. Rather, just change the original.
// TODO: Have loading icon for SignInActivity on first-time install (because it takes a little while).
// TODO: Be able to handle null inputs on PictureDetailsActivity
// TODO: Be able to check for valid inputs on same activity.

// TODO: If a post has been queued, allow users to edit a limited amount of fields, like description, VR, and location
// TODO: Know if post is uncompleted -> notify user it has been drafted in toast and on home screen (this means we might
//  want to use SQL for everything and populate each SQL post gradually. Also, it means we'll have the following identifiers

/**
 * Activity for users to sign-in and proceed to main app or sign-up for
 * AIRPACT-Fire account
 *
 * @author  Luke Weber
 * @see     Reference
 * @see     AppManager
 * @since   0.1
 */
public class SignInActivity extends AppCompatActivity {

    private AppManager mAppManager;

    // UI references
    private EditText mPasswordView, mUsernameView;
    private Button mSignInButton;
    private TextView mRegisterLink, mInfoLink;
    private CheckBox mRememberPasswordCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Setup our application manager
        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);
        mAppManager.onAppStart(this);

        // Attach objects to UI
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mRegisterLink = (TextView) findViewById(R.id.register_text);
        mInfoLink = (TextView) findViewById(R.id.info_text);
        mRememberPasswordCheckBox = (CheckBox) findViewById(R.id.remember_password_checkbox);

        // TODO: Remove -------
        // XML Stuff: create XML if necessary
        AppDataManager.init(getApplicationContext());
        // TODO: Remove -------

        // Set up the login form
        populateLoginFields();

        mRememberPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAppManager.getDataManager().setAppField("rememberPassword", b);
            }
        });

        // Checks credentials before proceeding to home
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Store credentials
                String username = mUsernameView.getText().toString();
                String password = mPasswordView.getText().toString();

                // Check if user exists
                if (mAppManager.getDataManager().isUser(username, password)) {
                    // Pre-authenticated user - continue
                    mAppManager.getDebugManager().printLog("Realm user already in DB");
                    login(username, password);
                } else {
                    // New guy - needs authentication
                    mAppManager.getDebugManager().printLog("Realm user does not exist");
                    mAppManager.getServerManager().authenticate(
                            SignInActivity.this, username, password,
                            new ServerManager.ServerCallback() {

                                private ProgressDialog mProgress;
                                private Context mContext;

                                @Override
                                public void onStart(Object... args) {

                                    mContext = (Context) args[0];

                                    // Show loading display
                                    mProgress = new ProgressDialog(mContext);
                                    mProgress.setTitle("Signing In...");
                                    mProgress.setMessage("Please wait while we authenticate");
                                    mProgress.show();
                                }

                                @Override
                                public void onFinish(Object... args) {

                                    boolean isUser = (boolean) args[0];
                                    String username = (String) args[1];
                                    String password = (String) args[2];

                                    // Dismiss loading dialog
                                    mProgress.dismiss();

                                    if (isUser) {
                                        Toast.makeText(mContext, R.string.authentication_success,
                                                Toast.LENGTH_LONG).show();

                                        // Log user in
                                        login(username, password);

                                    } else {
                                        Toast.makeText(mContext, R.string.authentication_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                // TODO: Remove -------
                if (true) return;
                // Update user's password and login time (and create one if we need to)
                AppDataManager.setUserData(username, "password", password);
                // Set as last user
                AppDataManager.setRecentUser(username);
                // For regulars; no network auth. required
                if (Boolean.parseBoolean(AppDataManager.getUserData(AppDataManager.getRecentUser(), "isAuth")))
                {
                    String loginTime = new SimpleDateFormat(DataManager.DATE_FORMAT).format(new Date());
                    AppDataManager.setUserData("lastLoginTime", loginTime);
                    Toast.makeText(SignInActivity.this, "Welcome back!", Toast.LENGTH_LONG).show();
                    //openHomeScreen();
                    return;
                }
                // Attempt first-time authentication
                // NOTE: First login time recorded here
                AuthenticateManager authenticateManager = new AuthenticateManager();
                authenticateManager.execute();
                // TODO: Remove -------
            }
        });

        // Allow user to register on website
        mRegisterLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Reference.SERVER_REGISTER_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        // Redirect user to info on website
        mInfoLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Reference.SERVER_INFORMATION_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    // Set credentials of last user
    private void populateLoginFields() {

        DataManager dataManager = mAppManager.getDataManager();
        String lastUsername = dataManager.getAppField("lastUser").toString();
        mUsernameView.setText(lastUsername);
        if ((boolean) dataManager.getAppField("rememberPassword")) {
            String lastPassword = (String) dataManager.getUserField("password");
            mPasswordView.setText(lastPassword);
            mRememberPasswordCheckBox.setChecked(true);
        }
    }

    // Open home page
    private void login(String username, String password) {

        // Let DB know we're logging in with this user
        mAppManager.onLogin(username, password);

        // Open home screen
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Deals with server
    // TODO: Remove -------
    class AuthenticateManager extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = new ProgressDialog(SignInActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Show loader
            progress.setTitle("Logging In");
            progress.setMessage("Please wait while we attempt authentication...");
            progress.show();

            // Make sure it displays before doing work
            while (!progress.isShowing()) try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Authentication URL
                URL authUrl = new URL(Post.SERVER_AUTH_URL);

                // JSON authentication (send) package
                JSONObject authSendJSON = new JSONObject();
                authSendJSON.put("username", AppDataManager.getRecentUser());
                authSendJSON.put("password",
                        AppDataManager.getUserData(AppDataManager.getRecentUser(), "password"));

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
                InputStream in = null;
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

                // Get SubmitFieldVars and see if server authenticated us
                isUser = Boolean.parseBoolean((String) authReceiveJSON.get("isUser"));
                if (isUser) {
                    // Don't do anything with key for now
                    //userKey = authReceiveJSON.get("secretKey").toString();
                    AppDataManager.setUserData(AppDataManager.getRecentUser(), "isAuth", "true");
                } else { // Exit if not a user
                    AppDataManager.setUserData(AppDataManager.getRecentUser(), "isAuth", "false");
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

            // To dismiss the dialog
            progress.dismiss();

            // Open up home screen
            if (Boolean.parseBoolean(AppDataManager.getUserData(AppDataManager.getRecentUser(),
                    "isAuth"))) {
                Toast.makeText(SignInActivity.this, "Authentication successful.\nWelcome!",
                        Toast.LENGTH_LONG).show();

                // Set first login time
                String firstLoginTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                AppDataManager.setUserData("firstLoginTime", firstLoginTime);

                // Go to home screen
                //openHomeScreen();
            } else {
                Toast.makeText(SignInActivity.this, "Could not authenticate user.\nPlease try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    // TODO: Remove -------
}
