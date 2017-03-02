package lar.wsu.edu.airpact_fire;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

/* LOGIN SCREEN */
/*
 * [INSERT DESCRIPTION]
 *
 */

// NOTE: Problem is with retrieving <isAuth /> field in *XML*, not the network auth. I'm pretty sure
// it has to do with the image. This happened after I took a picture for that user and then logged out.
// TODO: Address above NOTE
// TODO: Internet status (color-coded) on home, view gallery option (web browser), as well as last login time and other stats
// TODO: Custom Toast display, to make it more obvious to user
// TODO: More responsive buttons
// TODO: "Last logged in X days ago" on home screen
// TODO: Figure out why picture details activity doesn't repopulate estimated visual range and description.
//  This way we'll only deal with the image when we take a new one, view it, or post it.
// TODO: Check for pre-existing user. If user doesn't exist, app must authenticate with server to continue and
//  be added to the local user database. This eliminates the need for an <isAuth /> field and decreases the space
//  consumed by the local user XML file.
// TODO: Make code more modular and expandable
// TODO: Make separate files for the image, linked to by the <image /> tag by the user (e.g. "test_image.jpg")
// TODO: Add "Created by Luke Weber" signature to all files
// TODO: Add notification when we have connection to server, and not just internet access. Although,
//  we still want to know about internet access so we can know when to queue posts? We could just check
//  to see if
// TODO: Add notifications for when server comes up. Have a batch of checks and actions done by the app occasionally,
//  say, every 3 hours, like posting for backlogged posts. Also have frequent checks while app is running
//  that give toast/notifications when server is up. Maybe do something with notifications as well.
// TODO: Add home button to each page
// TODO: Find better way to do data management (i.e. better data structures and algorithms)
// TODO: Make it so we don't have to do GetUserData(USER, element), because we will always use lastUser
// TODO: Show post trends (location, time, etc.)
// TODO: Have auto-fill for login page and post page
// TODO: Allow user to view post coordinates in Google Maps
// TODO: When queued post is submitted, don't create a new post entirely for SQL database. Rather, just change the original.
// TODO: Have loading icon for SignInActivity on first-time install (because it takes a little while).
// TODO: Be able to handle null inputs on PictureDetailsActivity
// TODO: Be able to check for valid inputs on same activity.

// TODO: If a post has been queued, allow users to edit a limited amount of fields, like description, VR, and location
// TODO: Know if post is uncompleted -> notify user it has been drafted in toast and on home screen (this means we might
//  want to use SQL for everything and populate each SQL post gradually. Also, it means we'll have the following identifiers
//  for posts: submitted, queued, and drafted. This proposes a drastic design change.)

// TODO: Organize resources and java files in directories
// TODO: Rename things for efficiency


public class SignInActivity extends AppCompatActivity {

    private boolean hasLoadedBG = false;

    // UI references
    private RelativeLayout mPageLayout;
    private EditText mPasswordView, mUsernameView;
    private ImageButton mEmailSignInButton, mRegisterButton;

    // Startup progress
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // TODO: Remove
        // NOTE: Having trouble with image stuff when I do a test proceed?
        //testProceed();

        // Attach objects to UI
        mPageLayout = (RelativeLayout) findViewById(R.id.page);
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (ImageButton) findViewById(R.id.email_sign_in_icon);
        //mRegisterButton = (Button) findViewById(R.id.register_button);

        progress = new ProgressDialog(SignInActivity.this);

        // XML Stuff -- create XML if necessary
        AppDataManager.init(getApplicationContext());

        // Set up the login form
        populateLoginFields();

        // Proceeds to home
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store values at the time of the login attempt.
                String username = mUsernameView.getText().toString();
                String password = mPasswordView.getText().toString();

                // Update user's password and login time (and create one if we need to)
                AppDataManager.setUserData(username, "password", password);
                // Set as last user
                AppDataManager.setRecentUser(username);

                // For regulars; no network auth. required
                if (Boolean.parseBoolean(AppDataManager.getUserData(AppDataManager.getRecentUser(), "isAuth")))
                {
                    String loginTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    AppDataManager.setUserData("lastLoginTime", loginTime);
                    Toast.makeText(SignInActivity.this, "Welcome back!", Toast.LENGTH_LONG).show();
                    openHomeScreen();
                    return;
                }

                // Attempt first-time authentication
                // NOTE: First login time recorded here
                AuthenticateManager authenticateManager = new AuthenticateManager();
                authenticateManager.execute();
            }
        });

//        // Open web URL
//        mRegisterButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse(Post.SERVER_REGISTER_URL);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });

        // Check if scripts are updated
        //ScriptManager.update(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Load background once page is in view
        if (hasFocus) {

            // Check if background already loaded
            if (hasLoadedBG) return;
            hasLoadedBG = true;

            /** Bypassing cool background **/

//            // Get background resource
//            Bitmap landscape = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.washington_forest);
//            // Crop image
//            int landscapeWidth = landscape.getWidth();
//            int landscapeHeight = landscape.getHeight();
//            int screenWidth = Util.getScreenWidth(this);
//            int screenHeight = Util.getScreenHeight(this);
//            int cropWidth = (landscapeWidth < screenWidth) ? landscapeWidth : screenWidth;
//            int cropHeight = (landscapeHeight < screenHeight) ? landscapeHeight : screenHeight;
//            landscape = Bitmap.createBitmap(landscape, 0, 0, cropWidth, cropHeight);
//            // Apply blur
//            landscape = Util.doBlur(getApplicationContext(), landscape);
//            Drawable background = new BitmapDrawable(getResources(), landscape);
//            // Apply filter
//            int filterColor = Color.parseColor("#a0" + "5C5C5C");
//            mPageLayout.setBackgroundColor(filterColor);
//            // Set background (doesn't change with ScrollView)
//            getWindow().setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TODO: Fix issue with QPython not executing our Python code!

        if (true) return;

        /*
        if (requestCode == ScriptManager.SCRIPT_EXEC_PY) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String flag = bundle.getString("flag"); // flag you set
                String param = bundle.getString("param"); // param you set
                String result = bundle.getString("result"); // Result your Pycode generate
                Toast.makeText(this, "onQPyExec: return (" + result + ")", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "onQPyExec: data is null", Toast.LENGTH_SHORT).show();
            }
        }
        */
    }

    // Set credentials of last user
    private void populateLoginFields() {
        String lastUser = AppDataManager.getRecentUser();
        String lastPassword = AppDataManager.getUserData(lastUser, "password");

        mUsernameView.setText(lastUser);
        mPasswordView.setText(lastPassword);
    }

    // Open main page
    private void openHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // DEBUGGING: Move on from home screen
    private void testProceed() {
        // Get input data
        String username = "test";
        String password = "1234567890";

        // Begin XML if needed
        AppDataManager.init(getApplicationContext());

        // Create new authenticated user
        AppDataManager.setRecentUser(username);
        AppDataManager.setUserData(username, "isAuth", "true");
        AppDataManager.setUserData(username, "password", password);
        AppDataManager.setUserData(username, "loginTime", (new Date()).toString());

        // Open home
        openHomeScreen();
    }

    // Deals with server
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

                // Set first (and last) login time!
                String firstLoginTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                AppDataManager.setUserData("firstLoginTime", firstLoginTime);
                AppDataManager.setUserData("lastLoginTime", firstLoginTime);
                openHomeScreen();
            } else {
                Toast.makeText(SignInActivity.this, "Could not authenticate user.\nPlease try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
