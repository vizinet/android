// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.wsu.lar.airpact_fire.app.Constant;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.R;

/**
 * Activity for user to view their profile stats and visit
 * their online profile.
 *
 * @see UserInterfaceObject
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String sActionBarTitle = "Profile";

    private AppManager mAppManager;
    private UserInterfaceObject mUserInterfaceObject;

    private ActionBar mActionBar;
    private TextView mUsernameTextView;
    private TextView mFirstLoginDateTextView;
    private TextView mNumberLoginsTextView;
    private TextView mNumberSubmittedPostsTextView;
    private Button mWebButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(sActionBarTitle);

        mAppManager = Constant.getAppManager();
        mAppManager.onActivityStart(this);

        mUserInterfaceObject = mAppManager.getDataManager().getApp().getLastUser();

        mUsernameTextView = (TextView) findViewById(R.id.username_text_view);
        mFirstLoginDateTextView = (TextView) findViewById(R.id.first_login_date_text_view);
        mNumberLoginsTextView = (TextView) findViewById(R.id.number_logins_text_view);
        mNumberSubmittedPostsTextView = (TextView)
                findViewById(R.id.number_submitted_posts_text_view);
        mWebButton = (Button) findViewById(R.id.web_button);

        mUsernameTextView.setText(mUserInterfaceObject.getUsername());
        mFirstLoginDateTextView.setText(mUserInterfaceObject.getFirstLoginDate());
        int sessionCount = (mUserInterfaceObject.getSessions() == null)
                ? 0
                : mUserInterfaceObject.getSessions().size();
        mNumberLoginsTextView.setText("" + sessionCount);
        int submittedPostCount = 0;
        for (PostInterfaceObject postInterfaceObject : mUserInterfaceObject.getPosts()) {
            if (DataManager.getPostMode(postInterfaceObject.getMode()) == DataManager.PostMode.SUBMITTED) {
                submittedPostCount++;
            }
        }
        mNumberSubmittedPostsTextView.setText("" + submittedPostCount);

        mWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(Constant.SERVER_PROFILE_BASE_URL + mUserInterfaceObject.getUsername());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
