// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;
import java.util.List;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.app.service.GpsService;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.util.Util;
import edu.wsu.lar.airpact_fire.R;

/**
 * Main activity of app, home to Google Map full of post markers and
 * the gateway to all main functionality, namely image captures and
 * the gallery.
 *
 * <p>Each color-coated post marker represents a post and it's state
 * (e.g. submitted or queued) and each post can be previewed through
 * an {@link com.google.android.gms.maps.GoogleMap.InfoWindowAdapter}
 * once clicked.</p>
 *
 * @see     OnMapReadyCallback
 * @see     com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
 */
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private static final int sCameraUpdateDistance = 3;

    private AppManager mAppManager;
    private DataManager mDataManager;
    private UserInterfaceObject mUserInterfaceObject;

    private ActionBar mActionBar;
    private GoogleMap mGoogleMap;
    private Button mCaptureButton;
    private Button mGalleryButton;

    private HashMap<Marker, PostInterfaceObject> mMarkerMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);
        mDataManager = mAppManager.getDataManager();
        mUserInterfaceObject = mDataManager.getApp().getLastUser();

        // Listen for when GPS is available
        mAppManager.subscribeGpsAvailable(new AppManager.GpsAvailableCallback() {
            @Override
            public void change() {
                listenGpsUpdates();
            }
        });

        // Set action menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ Color.WHITE, Color.TRANSPARENT });
        mActionBar.setBackgroundDrawable(gd);
        mActionBar.setTitle(mUserInterfaceObject.getUsername());

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

            /*
            case R.id.action_tutorial:
                // TODO: Open tutorial PDF
                return true;

            case R.id.action_settings:
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            */

            case R.id.action_sign_out:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logout();
    }

    private void logout() {

        // User logged out, stop remembering them
        mAppManager.getDataManager().getApp().setRememberUser(false);

        // Logout and end usage session
        mAppManager.onLogout();

        // Go to sign-in page
        Toast.makeText(getApplicationContext(), "Signed out.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mGoogleMap = map;

        // Add post locations to map
        List<PostInterfaceObject> postInterfaceObjects = mUserInterfaceObject.getPosts();
        for (PostInterfaceObject postInterfaceObject : postInterfaceObjects) {

            DataManager.PostMode postMode = DataManager.getPostMode(postInterfaceObject.getMode());
            if (postMode == DataManager.PostMode.DRAFTED) {
                // Catch those stray incomplete posts and delete them
                postInterfaceObject.delete();
                continue;
            }

            String postSnippet = postInterfaceObject.getDate().toString();
            double[] postGps = postInterfaceObject.getImageObjects().get(0).getGps();

            // Display queued and submitted posts differently
            String postTitle;
            float postMarkerColor;
            if (postMode == DataManager.PostMode.SUBMITTED) {
                postTitle = postInterfaceObject.getLocation() + " [submitted]";
                postMarkerColor = BitmapDescriptorFactory.HUE_RED;
            } else if (postMode == DataManager.PostMode.QUEUED) {
                postTitle = postInterfaceObject.getLocation() + " [queued]";
                postMarkerColor = BitmapDescriptorFactory.HUE_YELLOW;
            } else {
                // ERROR: some other kind of post we don't support
                postInterfaceObject.delete();
                continue;
            }

            // Add marker and remember it
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(postGps[0], postGps[1]))
                    .title(postTitle)
                    .snippet(postSnippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(postMarkerColor)));
            mMarkerMap.put(marker, postInterfaceObject);
        }

        // Map display details and preferences
        mGoogleMap.setMinZoomPreference(7.0f);
        mGoogleMap.setMaxZoomPreference(14.0f);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.setMyLocationEnabled(true);

        // Info window
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.setInfoWindowAdapter(new PostInfoWindowAdapter());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Open up the post details in gallery
        PostInterfaceObject postInterfaceObject = mMarkerMap.get(marker);
        Intent intent = new Intent(getBaseContext(), GalleryActivity.class);
        intent.putExtra("TARGETED_POST_DETAILS", postInterfaceObject.getId());
        startActivity(intent);
    }

    /**
     * Class for creating a custom {@link com.google.android.gms.maps.GoogleMap.InfoWindowAdapter}
     * which displays a particular post title, status, thumbnail, computed VR, and date.
     */
    class PostInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            PostInterfaceObject postInterfaceObject = mMarkerMap.get(marker);
            View infoWindowView = getLayoutInflater().inflate(R.layout.layout_map_info_window, null);

            ImageView postImageView = (ImageView)
                    infoWindowView.findViewById(R.id.post_image_view);
            TextView postLocationTextView = (TextView)
                    infoWindowView.findViewById(R.id.post_location_text_view);
            TextView postStatusTextView = (TextView)
                    infoWindowView.findViewById(R.id.post_status_text_view);
            TextView postVisualRangeTextView = (TextView)
                    infoWindowView.findViewById(R.id.post_visual_range_text_view);
            TextView postDateTextView = (TextView)
                    infoWindowView.findViewById(R.id.post_date_text_view);

            postImageView.setImageBitmap(postInterfaceObject.getThumbnail(250));
            postLocationTextView.setText(String.format("%s",
                    postInterfaceObject.getLocation().toUpperCase()));
            postStatusTextView.setText(String.format("[%s]",
                    DataManager.getPostMode(postInterfaceObject.getMode()).getName()).toLowerCase());
            postVisualRangeTextView.setText(String.format("VR: %s km",
                    Math.round(postInterfaceObject.getComputedVisualRange())));
            postDateTextView.setText(postInterfaceObject.getDate().toString());

            return infoWindowView;
        }
    }

    /**
     * Update the camera if the user moves enough.
     */
    private void listenGpsUpdates() {

        mAppManager.subscribeGpsLocationChanges(new GpsService.GpsLocationChangedCallback() {
            @Override
            public void change(double[] gps) {

                if (mGoogleMap == null) return;

                LatLng currentCameraPosition = mGoogleMap.getCameraPosition().target;
                LatLng newCameraPosition = new LatLng(gps[0], gps[1]);

                // Only move camera if there is an acceptable distance moved by user
                if (Util.distanceBetween(currentCameraPosition, newCameraPosition) >=
                        sCameraUpdateDistance) {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newCameraPosition));
                }
            }
        });
    }
}
