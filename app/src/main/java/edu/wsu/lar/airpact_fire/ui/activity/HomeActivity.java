// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import lar.wsu.edu.airpact_fire.R;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private AppManager mAppManager;
    private DataManager mDataManager;
    private UserObject mUserObject;

    private String mUsername;

    private GoogleMap mGoogleMap;
    private Button mCaptureButton;
    private Button mGalleryButton;

    // -- OLDIES


    private Map<FrameLayout, ImageView> frameToIconMap;    // Map frames to their icons
    private Map<FrameLayout, Class<?>> frameToActivityMap; // Map frames to their following activities

    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private FrameLayout mNewPicturePane, mInformationPane, mPictureGalleryPane, mSettingsPane;
    private FrameLayout mBackButton;
    private LinearLayout mButtonPage;
    private TextView mUsernameText, mNumberPostedText, mNumberQueuedText, mRegisterDateText;
    private ImageView mNewPictureButton, mPictureGalleryButton, mInformationButton, mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);
        mDataManager = mAppManager.getDataManager();
        mUserObject = mDataManager.getApp().getLastUser();

        // Set action menu_alpha
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        Color.WHITE,
                        Color.TRANSPARENT
                });
        mActionBar.setBackgroundDrawable(gd);
        mActionBar.setTitle(mUserObject.getUsername());

        // Map fragment loading
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mCaptureButton = (Button) findViewById(R.id.capture_button);
        mGalleryButton = (Button) findViewById(R.id.gallery_button);

        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent captureIntent = new Intent(HomeActivity.this, ImageLabActivity.class);
                startActivity(captureIntent);
            }
        });
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(HomeActivity.this, GalleryActivity.class);
                startActivity(galleryIntent);
            }
        });

        // ---

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

        // Nav-menu_alpha
        mBackButton = (FrameLayout) findViewById(R.id.back_button);
        mUsernameText = (TextView) findViewById(R.id.username_text);
        mNumberPostedText = (TextView) findViewById(R.id.number_posted_text);
        mNumberQueuedText = (TextView) findViewById(R.id.number_queued_text);
        mRegisterDateText = (TextView) findViewById(R.id.member_register_date_text);


        // Give home a spankin'
        //updateHome();

        // Give each pane an event listener; respond to user events
        //setupUIEventListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_alpha, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Go to Android home (not app home)
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                return true;

            case R.id.action_profile:
                Intent userDataIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(userDataIntent);
                return true;

            case R.id.action_tutorial:
                // TODO: Open tutorial PDF
                return true;

            case R.id.action_settings:
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateHome();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logout();
    }

    private void setupHome() {
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
        frameToActivityMap.put(mNewPicturePane, ImageLabActivity.class);
        frameToActivityMap.put(mInformationPane, ProfileActivity.class);
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
                logout();
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

    private void logout() {

        // User logged out, stop remembering them
        mAppManager.getDataManager().getApp().setRememberUser(false);

        // TODO: End session (possibly in LoginActivity, checking if a session is running and ending it)

        // Go to sign-in page
        Toast.makeText(getApplicationContext(), "Signed out.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mGoogleMap = map;

        // Add post locations to map
        List<PostObject> postObjects = mUserObject.getPosts();
        for (PostObject postObject : postObjects) {

            // Skip un-submitted posts
            if (DataManager.getPostMode(postObject.getMode()) != DataManager.PostMode.SUBMITTED) {
                continue;
            }

            double[] postGps = postObject.getImageObjects().get(0).getGps();
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(postGps[0], postGps[1]))
                    .title(postObject.getLocation())
                    .snippet(postObject.getDate().toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }

        // Position the map's camera near current location
        double[] currentGps = mAppManager.getGps(HomeActivity.this);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(currentGps[0], currentGps[1])));

        // Set a preference for minimum and maximum zoom.
        mGoogleMap.setMinZoomPreference(7.0f);
        mGoogleMap.setMaxZoomPreference(14.0f);

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.setMyLocationEnabled(true);
    }
}
