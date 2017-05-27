package edu.wsu.lar.airpact_fire.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import edu.wsu.lar.airpact_fire.data.manager.PostDataManager;
import edu.wsu.lar.airpact_fire.manager.AppManager;
import lar.wsu.edu.airpact_fire.R;
import edu.wsu.lar.airpact_fire.util.Util;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout mNewPicturePane, mInformationPane, mPictureGalleryPane, mSettingsPane;
    private FrameLayout mBackButton;
    private LinearLayout mButtonPage;
    private TextView mUsernameText, mNumberPostedText, mNumberQueuedText, mRegisterDateText;
    private ImageView mNewPictureButton, mPictureGalleryButton, mInformationButton, mSettingsButton;

    private Map<FrameLayout, ImageView> frameToIconMap;    // Map frames to their icons
    private Map<FrameLayout, Class<?>> frameToActivityMap; // Map frames to their following activities

    private String mUsername;
    private AppManager mAppManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        // Catch errors
        // TODO: Let's get this to work on all activities with simple util function
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Toast.makeText(getApplicationContext(), "GLOBAL EXCEPTION CAUGHT", Toast.LENGTH_LONG).show();
            }
        });

        mAppManager = Reference.getAppManager();

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

        setupHome();
        updateHome();

        // Give each pane an event listener; respond to user events
        setupUIEventListeners();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // TODO: Move

        // Load background once page is in view
        if (hasFocus) {

            // Skipping out on background stuff for now
            if (true) return;

            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    //new int[] {0xFF616261,0xFF131313});
                    new int[]{
                            getResources().getColor(R.color.schemeTransparentLight),
                            getResources().getColor(R.color.schemeTransparentDark)
                    });
            gd.setCornerRadius(0f);

            mButtonPage.setBackgroundDrawable(gd);

            // TODO: Remove below line
            if (true) return;
            // Get background resource
            // TODO: Check if not first time. If so, don't add landscape again
            Bitmap landscape = BitmapFactory.decodeResource(getResources(),
                    R.drawable.washington_forest_cropped);
            // Crop image
            int landscapeWidth = landscape.getWidth();
            int landscapeHeight = landscape.getHeight();
            int screenWidth = Util.getScreenWidth(this);
            int screenHeight = Util.getScreenHeight(this);
            int cropWidth = (landscapeWidth < screenWidth) ? landscapeWidth : screenWidth;
            int cropHeight = (landscapeHeight < screenHeight) ? landscapeHeight : screenHeight;
            landscape = Bitmap.createBitmap(landscape, 0, 0, cropWidth, cropHeight);
            // Apply blur
            landscape = Util.doBlur(getApplicationContext(), landscape);
            Drawable background = new BitmapDrawable(getResources(), landscape);
            // Apply filter
            //int filterColor = Color.parseColor("#a0" + "ffffff");
            //mButtonPage.setBackgroundColor(filterColor);
            // Set background (doesn't change with ScrollView)
            getWindow().setBackgroundDrawable(background);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        String displayName;
        int cutoffLength = 10;
        mUsername = mAppManager.getDataManager().getLastUser().toString();

        // Make sure name gets cutoff if exceeds max length
        displayName = (mUsername.length() >= cutoffLength)
                ? (mUsername.substring(0, cutoffLength) + "...")
                : mUsername;
        mUsernameText.setText(displayName);

        // Member register date (just grab the "yyyy.MM.dd" part)
        // mRegisterDateText.setText("First login on " +
        // mDataManager.getUserField("firstLoginTime").substring(0, 10));

        // Post numbers
        int numPosted = PostDataManager.getNumSubmitted(getApplicationContext(), mUsername);
        int numQueued = PostDataManager.getNumQueued(getApplicationContext(), mUsername);
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
        frameToActivityMap.put(mNewPicturePane, SelectTargetsActivity.class);
        frameToActivityMap.put(mInformationPane, InformationActivity.class);
        frameToActivityMap.put(mPictureGalleryPane, QueuedPostsActivity.class);
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

//        // Setup action
//        frameLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), frameActivity);
//                Toast.makeText(HomeActivity.this, "setOnClickListener", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
//            }
//        });

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
//                        if (Util.isPointInView(v, Math.round(event.getX()), Math.round(event.getY()))) {
                        Intent intent = new Intent(getApplicationContext(), frameActivity);
                        //Toast.makeText(HomeActivity.this, "setOnClickListener", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
//                        }
                        frameIcon.clearColorFilter();
                        return true;
                }
                return false;
            }
        });

        // TODO: Deal with this problem of not setting an onclicklistener
    }
}
