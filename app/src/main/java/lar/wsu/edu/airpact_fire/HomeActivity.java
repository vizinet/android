package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout mNewPicturePane, mInformationPane, mPictureGalleryPane, mSettingsPane;
    private FrameLayout mServerStatusContainer;
    private TextView mUsernameText, mNumberPostedText, mNumberQueuedText,
            mServerStatusText;
    private ImageView mNewPictureButton, mPictureGalleryButton, mInformationButton, mSettingsButton;

    // Map frames to their icons
    private Map<FrameLayout, ImageView> frameToIconMap;
    // Map frames to their following activities
    private Map<FrameLayout, Class<?>> frameToActivityMap;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Panes
        mNewPicturePane = (FrameLayout) findViewById(R.id.new_picture_pane);
        mInformationPane = (FrameLayout) findViewById(R.id.information_pane);
        mPictureGalleryPane = (FrameLayout) findViewById(R.id.picture_gallery_pane);
        mSettingsPane = (FrameLayout) findViewById(R.id.settings_pane);
        // Icons of panes
        mNewPictureButton = (ImageView) findViewById(R.id.new_picture_button);
        mInformationButton = (ImageView) findViewById(R.id.information_button);
        mPictureGalleryButton = (ImageView) findViewById(R.id.picture_gallery_button);
        mSettingsButton = (ImageView) findViewById(R.id.settings_button);

        // Nav-bar
        mUsernameText = (TextView) findViewById(R.id.username_text);
        mNumberPostedText = (TextView) findViewById(R.id.number_posted_text);
        mNumberQueuedText = (TextView) findViewById(R.id.number_queued_text);
        mServerStatusText = (TextView) findViewById(R.id.server_status_text);
        mServerStatusContainer = (FrameLayout) findViewById(R.id.server_status_container);

        // Update home variables
        updateHome();

        // Give each pane an event listener; respond to user events
        setupPaneEventListeners();

//        // New picture
//        mNewPicturePane.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        //mNewPictureButton.setColorFilter(getResources().getColor(R.color.schemeBlueHighlight));
//                        mNewPictureButton.setColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.MULTIPLY);
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        //mNewPictureButton.setColorFilter(getResources().getColor(R.color.schemeBlue));
//                        mNewPictureButton.clearColorFilter();
//                        return true;
//                }
//                return false;
//            }
//        });
//        mNewPicturePane.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // User will now take picture and select contrast points
//                Intent intent = new Intent(getApplicationContext(), SelectContrastActivity.class);
//                startActivity(intent);
//            }
//        });
//        // Picture gallery
//        mPictureGalleryPane.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // User can view their posts
//                Intent intent = new Intent(getApplicationContext(), QueuedPostsActivity.class);
//                startActivity(intent);
//            }
//        });
//        // Settings
//        mSettingsPane.setOnTouchListener(new View.OnTouchListener() {
//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mSettingsPane.setForegroundTintMode(PorterDuff.Mode.DARKEN);
//                // TODO: Darken whole pane
//                //mSettingsPane.setTint
//                //Toast.makeText(HomeActivity.this, "hover", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//        mSettingsPane.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
//                Toast.makeText(HomeActivity.this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
//                //startActivity(intent);
//            }
//        });
//        mInformationPane.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), InformationActivity.class);
//                Toast.makeText(HomeActivity.this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
//                //startActivity(intent);
//            }
//        });
    }

    // NOTE: user may have changed!
    @Override
    protected void onResume() {
        super.onResume();

        updateHome();
    }

    private void updateHome() {
        // Username
        mUsername = UserDataManager.getRecentUser();
        mUsernameText.setAllCaps(true);
        mUsernameText.setText(mUsername);

        // Post numbers
        int numPosted = PostDataManager.getNumSubmitted(getApplicationContext(), mUsername);
        int numQueued = PostDataManager.getNumQueued(getApplicationContext(), mUsername);
        mNumberPostedText.setAllCaps(true);
        mNumberPostedText.setText(numPosted + " posted");
        mNumberQueuedText.setAllCaps(true);
        mNumberQueuedText.setText(numQueued + " queued");

        // Internet connection
        //mServerStatusText.setAllCaps(true);
        mServerStatusContainer.setBackgroundColor(getResources().getColor(R.color.schemeRedHighlight));
        mServerStatusText.setText("SERVER DISCONNECTED (last check 5 minutes ago)");
        if (Util.isServerAvailable(HomeActivity.this)) {//if (Util.isNetworkAvailable(this)) {
            mServerStatusContainer.setBackgroundColor(getResources().getColor(R.color.schemeGreenHighlight));
            mServerStatusText.setText("SERVER CONNECTED (last check 5 minutes ago)");
        }
    }

    private void setupPaneEventListeners() {
        // Draw maps
        frameToIconMap = new HashMap<>();
        frameToIconMap.put(mNewPicturePane, mNewPictureButton);
        frameToIconMap.put(mInformationPane, mInformationButton);
        frameToIconMap.put(mPictureGalleryPane, mPictureGalleryButton);
        frameToIconMap.put(mSettingsPane, mSettingsButton);
        // ---
        frameToActivityMap = new HashMap<>();
        frameToActivityMap.put(mNewPicturePane, SelectContrastActivity.class);
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
