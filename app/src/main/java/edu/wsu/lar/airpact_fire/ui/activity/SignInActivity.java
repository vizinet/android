// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.AppObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import lar.wsu.edu.airpact_fire.R;

// TODO: Address all below to-do statements
// TODO: Adapt UI changes based on sketches
// TODO: AppManager: Method which adds service to monitor app state (namely, exit, so we can call onAppEnd)
// TODO: Add copyright to every source file
// TODO: Add LICENCE.TXT
// TODO: Look into making custom image-capture activity
// TODO: Adapt to new research-based, clean design scheme
// TODO: Splash screen
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
    private RelativeLayout mPage;
    private ImageView mAppBanner;
    private EditText mPasswordView, mUsernameView;
    private Button mSignInButton;
    private TextView mRegisterLink, mInfoLink;
    private CheckBox mRememberMeCheckBox;
    private ImageButton mHelpImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Setup our application manager
        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);
        mAppManager.onAppStart(this);

        // TODO: Remove
        //mAppManager.getDebugManager().printLog(mAppManager.getAlgorithms().toString());

        // Sign last user in if that box was previously checked
        if (mAppManager.getDataManager().getApp().getRememberUser()) {
            UserObject userObject = mAppManager.getDataManager().getApp().getLastUser();
            login(userObject.getUsername(), userObject.getPassword());
        }

        // Attach objects to UI
        mPage = (RelativeLayout) findViewById(R.id.page);
        mAppBanner = (ImageView) findViewById(R.id.sign_in_banner);
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mRememberMeCheckBox = (CheckBox) findViewById(R.id.remember_password_checkbox);
        mRegisterLink = (TextView) findViewById(R.id.register_text);
        mHelpImageButton = (ImageButton) findViewById(R.id.help_image_button);

        // Set up the login form
        populateLoginFields();

        // Readjust page when keyboard shows/hides
        mPage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){
                    public void onGlobalLayout(){

                        Rect rect = new Rect();
                        mPage.getWindowVisibleDisplayFrame(rect);
                        int screenHeight = mPage.getRootView().getHeight();

                        // rect.bottom is the position above soft keypad or device button
                        // If keypad is shown, the rect.bottom is smaller than before
                        int keypadHeight = screenHeight - rect.bottom;
                        if (keypadHeight > (screenHeight * Reference.KEYPAD_OCCUPATION_RATIO)) {
                            // Keyboard is opened
                        }
                        else {
                            // Keyboard is closed
                            mUsernameView.clearFocus();
                            mPasswordView.clearFocus();
                        }
                    }
        });

        // Make space for writing when keyboard in use
        View.OnFocusChangeListener keyBoardFocusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mAppBanner.setVisibility(View.GONE);
                    mRegisterLink.setVisibility(View.GONE);
                    mHelpImageButton.setVisibility(View.GONE);
                } else {
                    mAppBanner.setVisibility(View.VISIBLE);
                    mRegisterLink.setVisibility(View.VISIBLE);
                    mHelpImageButton.setVisibility(View.VISIBLE);
                }
            }
        };
        mUsernameView.setOnFocusChangeListener(keyBoardFocusListener);
        mPasswordView.setOnFocusChangeListener(keyBoardFocusListener);

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

        /*
        // Redirect user to info on website
        mInfoLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Reference.SERVER_INFORMATION_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        */
    }

    @Override
    /** @description Remove keyboard when user "clicks out" of it */
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    // Set credentials of last user
    private void populateLoginFields() {

        // Setup registration link
        Spanned registerText = Html.fromHtml(
                String.format(
                        "<a href = '%s'>Sign Up for AIRPACT-Fire</a>",
                        Reference.SERVER_REGISTER_URL));
        mRegisterLink.setText(registerText);
        mRegisterLink.setMovementMethod(LinkMovementMethod.getInstance());

        // Update login fields
        AppObject appObject = mAppManager.getDataManager().getApp();
        UserObject lastUser = appObject.getLastUser();
        if (lastUser != null && appObject.getRememberUser()) {
            mUsernameView.setText(lastUser.getUsername());
        }
    }

    // Open home page
    private void login(String username, String password) {

        // Let DB know we're logging in with this user
        mAppManager.onLogin(username, password);

        if (mRememberMeCheckBox != null) {
            // Regular login: remember legit user upon request
            mAppManager.getDataManager().getApp().setRememberUser(mRememberMeCheckBox.isChecked());
            Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();
        } else {
            // Expedited login
            Toast.makeText(getApplicationContext(), R.string.expedited_login_success, Toast.LENGTH_LONG).show();
        }

        // Open home screen
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
