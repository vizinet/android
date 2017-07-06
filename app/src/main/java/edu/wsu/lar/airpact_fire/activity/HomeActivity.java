// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import edu.wsu.lar.airpact_fire.Reference;
import edu.wsu.lar.airpact_fire.manager.AppManager;
import lar.wsu.edu.airpact_fire.R;

public class HomeActivity extends AppCompatActivity {

    private AppManager mAppManager;

    private String mUsername;

    private Toolbar mToolbar;
    private FrameLayout mNewPicturePane, mInformationPane, mPictureGalleryPane, mSettingsPane;
    private FrameLayout mBackButton;
    private LinearLayout mButtonPage;
    private TextView mUsernameText, mNumberPostedText, mNumberQueuedText, mRegisterDateText;
    private ImageView mNewPictureButton, mPictureGalleryButton, mInformationButton, mSettingsButton;

    private Map<FrameLayout, ImageView> frameToIconMap;    // Map frames to their icons
    private Map<FrameLayout, Class<?>> frameToActivityMap; // Map frames to their following activities

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // App manager
        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);

        // Action bar
        mToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().hide();

        // Panes
        mNewPicturePane = (FrameLayout) findViewById(R.id.new_picture_pane);
        mInformationPane = (FrameLayout) findViewById(R.id.information_pane);
        mPictureGalleryPane = (FrameLayout) findViewById(R.id.picture_gallery_pane);
        mSettingsPane = (FrameLayout) findViewById(R.id.settings_pane);
        mButtonPage = (LinearLayout) findViewById(R.id.button_page);

        // Icons of panes
        mNewPictureButton = (ImageView) findViewById(R.id.new_picture_button);
        mInformationButton = (ImageView) findViewById(R.id.information_button);
        mPictureGalleryButton = (ImageView) findViewById(R.id.picture_gallery_button);
        mSettingsButton = (ImageView) findViewById(R.id.settings_button);

        // Nav-bar
        mBackButton = (FrameLayout) findViewById(R.id.back_button);
        mUsernameText = (TextView) findViewById(R.id.username_text);
        mNumberPostedText = (TextView) findViewById(R.id.number_posted_text);
        mNumberQueuedText = (TextView) findViewById(R.id.number_queued_text);
        mRegisterDateText = (TextView) findViewById(R.id.member_register_date_text);

        // Give home a spankin'
        setupHome();
        updateHome();

        // Give each pane an event listener; respond to user events
        setupUIEventListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHome();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Go to sign in page
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void setupHome() {
        mUsernameText.setAllCaps(true);
    }

    private void updateHome() {

        // Username
        mUsername = mAppManager.getDataManager().getApp().getLastUser().getUsername();
        mToolbar.setTitle(String.format("[ %s ]", mUsername.toUpperCase()));

        // Limit length for cutoff reasons
        int cutoffLength = 10;
        String displayName = (mUsername.length() >= cutoffLength)
                ? mUsername.substring(0, cutoffLength) + "..."
                : mUsername;
        mUsernameText.setText(String.format("[ %s ]", displayName));

        // Post numbers
        int numPosted = 0; //PostDataManager.getNumSubmitted(getApplicationContext(), mUsername);
        int numQueued = 0; //PostDataManager.getNumQueued(getApplicationContext(), mUsername);
        mNumberPostedText.setAllCaps(true);
        mNumberPostedText.setText(numPosted + "");
        mNumberQueuedText.setAllCaps(true);
        mNumberQueuedText.setText(numQueued + "");
    }

    private void setupUIEventListeners() {

        // Draw maps
        frameToIconMap = new HashMap<>();
        frameToIconMap.put(mNewPicturePane, mNewPictureButton);
        frameToIconMap.put(mInformationPane, mInformationButton);
        frameToIconMap.put(mPictureGalleryPane, mPictureGalleryButton);
        frameToIconMap.put(mSettingsPane, mSettingsButton);
        // ---
        frameToActivityMap = new HashMap<>();
        frameToActivityMap.put(mNewPicturePane, TargetSelectActivity.class);
        frameToActivityMap.put(mInformationPane, UserDataActivity.class);
        frameToActivityMap.put(mPictureGalleryPane, GalleryActivity.class);
        frameToActivityMap.put(mSettingsPane, SettingsActivity.class);

        // Run through frames and add event listeners
        Iterator it = frameToIconMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            setupListeners((FrameLayout) pair.getKey());
            it.remove(); // avoids a ConcurrentModificationException
        }

        // More
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to sign-in page
                Toast.makeText(getApplicationContext(), "Signed out.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
    }
    // Setup event listeners for give frame layout
    private void setupListeners(FrameLayout frameLayout) {

        // Get frame's icon & activity
        final ImageView frameIcon = frameToIconMap.get(frameLayout);
        final Class frameActivity = frameToActivityMap.get(frameLayout);

        // Setup touch animation
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        frameIcon.setColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.MULTIPLY);
                        return true;

                    case MotionEvent.ACTION_UP:

                        // Start it's activity
                        Intent intent = new Intent(getApplicationContext(), frameActivity);
                        startActivity(intent);
                        frameIcon.clearColorFilter();
                        return true;
                }
                return false;
            }
        });

        // TODO: Deal with this problem of not setting an onclicklistener
    }
}
