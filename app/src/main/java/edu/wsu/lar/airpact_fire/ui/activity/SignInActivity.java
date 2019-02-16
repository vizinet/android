// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.wsu.lar.airpact_fire.AIRPACTFireApplication;
import edu.wsu.lar.airpact_fire.app.Constant;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.AppInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.server.callback.AuthenticationServerCallback;
import edu.wsu.lar.airpact_fire.util.Util;
import edu.wsu.lar.airpact_fire.R;

/**
 * Activity for users to sign-in and proceed to app or sign-up for
 * an AIRPACT-Fire account.
 *
 * <p>Authentication with the server works on a one-time basis, meaning
 * an internet connection is required to successfully validate the user
 * in the app. Once successfully authenticated, that specific user will
 * be permitted to use the app offline.</p>
 *
 * @see     Constant
 * @see     AppManager
 * @see     DataManager
 */
public class SignInActivity extends AppCompatActivity {

    private AppManager mAppManager;

    private String mUsername;
    private String mPassword;

    // UI References
    private RelativeLayout mPage;
    private ImageView mAppBanner;
    private EditText mPasswordView, mUsernameView;
    private Button mSignInButton;
    private TextView mRegisterLink;
    private CheckBox mRememberMeCheckBox;
    private ImageButton mHelpImageButton;

    /**
     * Start {@link AppManager} up and move to home activity if this
     * user has been saved from the prior session.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Setup our application manager.
        mAppManager = Constant.getAppManager();
        mAppManager.onApplicationStart(this);
        mAppManager.onActivityStart(this);

        // Sign last user in if that box was previously checked.
        if (mAppManager.getDataManager().getApp().getRememberUser()) {
            UserInterfaceObject userInterfaceObject = mAppManager.getDataManager()
                    .getApp().getLastUser();
            mUsername = userInterfaceObject.getUsername();
            mPassword = userInterfaceObject.getPassword();
            proceed();
        }

        // Attach objects to UI.
        mPage = findViewById(R.id.page);
        mAppBanner = findViewById(R.id.sign_in_banner);
        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mSignInButton = findViewById(R.id.sign_in_button);
        mRememberMeCheckBox = findViewById(R.id.remember_password_checkbox);
        mRegisterLink = findViewById(R.id.register_text);
        mHelpImageButton = findViewById(R.id.help_image_button);

        // Set up the login form.
        populateLoginFields();

        // Readjust page when keyboard shows/hides.
        mPage.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

                    Rect rect = new Rect();
                    mPage.getWindowVisibleDisplayFrame(rect);
                    int screenHeight = mPage.getRootView().getHeight();

                    // rect.bottom is the position above soft keypad or device button
                    // If keypad is shown, the rect.bottom is smaller than before
                    int keypadHeight = screenHeight - rect.bottom;
                    if (keypadHeight > (screenHeight * Constant.KEYPAD_OCCUPATION_RATIO)) {
                        // Keyboard is opened
                        mAppBanner.setVisibility(View.GONE);
                        mRegisterLink.setVisibility(View.GONE);
                        mHelpImageButton.setVisibility(View.GONE);
                    }
                    else {
                        // Keyboard is closed
                        mAppBanner.setVisibility(View.VISIBLE);
                        mRegisterLink.setVisibility(View.VISIBLE);
                        mHelpImageButton.setVisibility(View.VISIBLE);
                    }
                });

        // Checks credentials before proceeding to home
        mSignInButton.setOnClickListener(v -> {
            // Store credentials
            mUsername = mUsernameView.getText().toString();
            mPassword = mPasswordView.getText().toString();

            // Validate credentials
            if (Util.isNullOrEmpty(mUsername) || Util.isNullOrEmpty(mPassword)) {
                Toast.makeText(SignInActivity.this,
                        "Please enter valid credentials", Toast.LENGTH_LONG).show();
            }

            // Check if user exists
            if (mAppManager.getDataManager().getApp().getUser(mUsername, mPassword) != null) {
                // Pre-authenticated user - continue
                mAppManager.getDebugManager().printLog("Realm user already in DB");
                proceed();
            } else {
                // TODO: The AuthenticationServerCallback doesn't even need to pass the username/password back (already being set above)
                // New guy - needs authentication
                mAppManager.getDebugManager().printLog("Realm user does not exist");
                mAppManager.onAuthenticate(mUsername, mPassword,
                        new AuthenticationServerCallback(SignInActivity.this) {
                            @Override
                            public Object onFinish(Object... args) {
                                boolean isUser = (boolean) args[0];
                                if (isUser) {
                                    Toast.makeText(SignInActivity.this,
                                            R.string.authentication_success,
                                            Toast.LENGTH_LONG).show();
                                    proceed();
                                } else {
                                    Toast.makeText(SignInActivity.this,
                                            R.string.authentication_failed,
                                            Toast.LENGTH_SHORT).show();
                                }

                                return null;
                            }
                        });
            }
        });

        // Redirect user to info on website
        mHelpImageButton.setOnClickListener(v -> {
            Uri uri = Uri.parse(Constant.SERVER_INFORMATION_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    /**
     * Remove keyboard when user "clicks out" of it.
     */
    @Override
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

    /**
     * Setup the page and show last user logged in.
     */
    private void populateLoginFields() {
        // Setup registration link
        Spanned registerText = Html.fromHtml(String.format(
            "<a href = '%s'>Sign Up for AIRPACT-Fire</a>",
            Constant.SERVER_REGISTER_URL));
        mRegisterLink.setText(registerText);
        mRegisterLink.setMovementMethod(LinkMovementMethod.getInstance());

        // Update login fields
        AppInterfaceObject appInterfaceObject = mAppManager.getDataManager().getApp();
        UserInterfaceObject lastUser = appInterfaceObject.getLastUser();
        if (lastUser != null && appInterfaceObject.getRememberUser()) {
            mUsernameView.setText(lastUser.getUsername());
        }
    }

    /**
     * Open {@link HomeActivity} and begin a new
     * {@link edu.wsu.lar.airpact_fire.data.realm.model.Session}.
     */
    public void proceed() {

        // Let DB know we're logging in with this user.
        mAppManager.onLogin(mUsername, mPassword);

        if (mRememberMeCheckBox != null) {
            // Regular login: remember legit user upon request.
            mAppManager.getDataManager().getApp().setRememberUser(mRememberMeCheckBox.isChecked());
            Toast.makeText(getApplicationContext(), R.string.login_success,
                    Toast.LENGTH_LONG).show();
        } else {
            // Expedited login
            Toast.makeText(getApplicationContext(), R.string.expedited_login_success, Toast.LENGTH_LONG).show();
        }

        // Route user to next screen.
        Class nextClass = null;
        if (AIRPACTFireApplication.isFirstRun(this)) {
            nextClass = WelcomeActivity.class;
        } else {
            nextClass = HomeActivity.class;
        }
        Intent intent = new Intent(this, nextClass);
        startActivity(intent);
    }
}
