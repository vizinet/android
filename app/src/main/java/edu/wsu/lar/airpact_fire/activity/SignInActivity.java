// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.wsu.lar.airpact_fire.Reference;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.AppObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.manager.AppManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import lar.wsu.edu.airpact_fire.R;

// TODO: Address all below to-do statements

// TODO: Adapt UI changes based on sketches
// TODO: AppManager: Method which adds service to monitor app state (namely, exit, so we can call onAppEnd)
// TODO: Add copyright/disclaimer/license code (Luke Weber, WSU, LAR, etc.) to each code file in AIRPACT-Fire, along with authorship information in each file
// TODO: Look into making custom image-capture activity
// TODO: Adapt to new research-based, clean design scheme
// TODO: Splash screen
// TODO: Remove AppDataManager and PostDataManager
// TODO: Add "alpha" print to logo
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
 * Activity for users to sign-in and proceed to main activity or sign-up for
 * AIRPACT-Fire account
 *
 * @author  Luke Weber
 * @see     Reference
 * @see     AppManager
 * @see     DataManager
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

        // Set up the login form
        populateLoginFields();

        // Listen for changes in user preference
        mRememberPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAppManager.getDataManager().getApp().setRememberPassword(b);
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
                if (mAppManager.getDataManager().getApp().getUser(username, password) != null) {

                    // Pre-authenticated user - continue
                    mAppManager.getDebugManager().printLog("Realm user already in DB");
                    login(username, password);

                } else {

                    // New guy - needs authentication
                    mAppManager.getDebugManager().printLog("Realm user does not exist");

                    mAppManager.onAuthenticate(
                            username, password,
                            new ServerManager.ServerCallback() {

                                private ProgressDialog mProgress;
                                private Activity mActivity;

                                @Override
                                public Object onStart(Object... args) {

                                    mActivity = (Activity) args[0];

                                    // Show loading display
                                    // NOTE: Found that using context rather than activity causes
                                    // some annoyances
                                    mProgress = new ProgressDialog(mActivity);
                                    mProgress.setTitle("Signing In...");
                                    mProgress.setMessage("Please wait while we authenticate");
                                    mProgress.show();

                                    return null;
                                }

                                @Override
                                public Object onFinish(Object... args) {

                                    boolean isUser = (boolean) args[0];
                                    String username = (String) args[1];
                                    String password = (String) args[2];

                                    // Dismiss loading dialog
                                    mProgress.dismiss();

                                    if (isUser) {
                                        Toast.makeText(mActivity, R.string.authentication_success,
                                                Toast.LENGTH_LONG).show();

                                        // Log user in
                                        login(username, password);

                                    } else {
                                        Toast.makeText(mActivity, R.string.authentication_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    return null;
                                }
                            });
                }
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

        mAppManager.getDebugManager().printLog("Populating login fields");
        AppObject appObject = mAppManager.getDataManager().getApp();
        UserObject lastUser = appObject.getLastUser();
        mAppManager.getDebugManager().printLog("lastUser = " + lastUser);

        if (lastUser != null) {
            mUsernameView.setText(lastUser.getUsername());
        }
        if (appObject.getRememberPassword()) {
            mRememberPasswordCheckBox.setChecked(true);
            if (mUsernameView.getText().toString().length() > 0) {
                mPasswordView.setText(lastUser.getPassword());
            }
        }
    }

    // Open home page
    private void login(String username, String password) {

        // Let DB know we're logging in with this user
        mAppManager.onLogin(username, password);

        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();

        // Open home screen
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
