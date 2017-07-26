// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.algorithm.ofo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.Date;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.util.target.manager.UITargetManager;
import lar.wsu.edu.airpact_fire.R;

import static android.app.Activity.RESULT_OK;

// TODO: Move target manager to ImageLabActivity
// TODO: Address the offset on the target_background
// TODO: When user retakes image, scrap this post and make a new one!
// TODO: Look into building a target_background API so we don't keep rewriting code and the fragments are elegant and simple

public class OneForOneAlphaFragment extends Fragment {

    private static final String sActionBarTitle = "Target Selection";
    private static final int sRequestImageCapture = 1;
    private static final int sRequestTakePhoto = 1;
    private static final int sTargetCount = 1;

    private AppManager mAppManager;
    private UserObject mUserObject;
    private PostObject mPostObject;

    private EditText mTargetDistanceEditText;
    private ImageView mMainImageView;
    private ImageView mTargetColorImageView;
    private LinearLayout mControlLinearLayout;
    private Button mProceedButton;

    private UITargetManager mUITargetManager;
    private int mCurrentTargetId;

    public OneForOneAlphaFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);
        ((ImageLabActivity) getActivity()).clearPadding();

        // Get fields from activity
        mAppManager = ((ImageLabActivity) getActivity()).getAppManager();
        mUserObject = ((ImageLabActivity) getActivity()).getUserObject();
        mPostObject = ((ImageLabActivity) getActivity()).getPostObject();

        // Get views
        View view = inflater.inflate(R.layout.fragment_one_for_one_alpha, container, false);
        mTargetDistanceEditText = (EditText) view.findViewById(R.id.target_distance_edit_text);
        mMainImageView = (ImageView) view.findViewById(R.id.main_image_view);
        mTargetColorImageView = (ImageView) view.findViewById(R.id.target_color_image_view);
        mControlLinearLayout = (LinearLayout) view.findViewById(R.id.control_linear_layout);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);

        // Target manager creation
        mUITargetManager = new UITargetManager(getActivity(), mMainImageView, sTargetCount);

        // Take pic
        takePicture();

        // Target movement
        mMainImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Handle displays
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mControlLinearLayout.setVisibility(View.VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mControlLinearLayout.setVisibility(View.GONE);
                }

                // Get touch coordinates
                int x = (int) event.getX();
                int y = (int) event.getY();

                // Handle displays
                //if (event.getAction() == MotionEvent.ACTION_UP) showDisplays();
                //else if (event.getAction() == MotionEvent.ACTION_DOWN) hideDisplays();

                // No target_background allowed outside image area
                if (y >= (v.getY() + v.getHeight())) return false;

                // Move the right target_background
                // TODO: Get color of target_background here
                // TODO: Get target region average, not just one point
                mUITargetManager.setTargetPosition(mCurrentTargetId, x, y);
                int targetColor = mUITargetManager.getTargetColor(mCurrentTargetId);
                mTargetColorImageView.setBackgroundColor(targetColor);

                return true;
            }
        });

        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: Some target to post method
                //mPostObject.setTargetDistance();

                Fragment startFragment = new OneForOneBetaFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.image_lab_container, startFragment).addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void takePicture() {

        // Ensure that there's a camera activity to handle the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            Uri imageUri = mPostObject.createImage();

            // Make sure we get file back, and enforce PORTRAIT camera mode
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            takePictureIntent.putExtra(
                    MediaStore.EXTRA_SCREEN_ORIENTATION,
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            );

            startActivityForResult(takePictureIntent, sRequestTakePhoto);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Call garbage collection
        // TODO: See if we can remove this
        Runtime.getRuntime().gc();

        if (requestCode == sRequestImageCapture && resultCode == RESULT_OK) {

            // Get bitmap
            Bitmap bitmap = mPostObject.getImageBitmap();
            if (bitmap == null) {
                // Abort mission
                //handleImageFailure();
                return;
            }

            // Resize bitmap for display (to screen proportions)
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;
            int imageHeight = (int) (bitmap.getHeight() *
                    (screenWidth / (float) bitmap.getWidth()));
            int imageWidth = screenWidth;
            bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);
            mPostObject.setImage(bitmap);

            // Set date the moment the image has been captured
            mPostObject.setDate(new Date());

            // Add placeholder geolocation
            mPostObject.setGPS(new double[] {
                    Reference.DEFAULT_GPS_LOCATION[0],
                    Reference.DEFAULT_GPS_LOCATION[1]
            });

            // Attempt to get real geolocation
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(
                    Context.LOCATION_SERVICE);
            boolean canAccessFineLocation = ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            boolean canAccessCourseLocation = ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            if (canAccessFineLocation || canAccessCourseLocation) {
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mPostObject.setGPS(new double[] { loc.getLatitude(), loc.getLatitude() });
            }

            // Set image view
            mMainImageView.setImageBitmap(bitmap);

            mAppManager.getServerManager().onSubmit(
                    getActivity().getApplicationContext(),
                    mPostObject,
                new ServerManager.ServerCallback() {
                    @Override
                    public Object onStart(Object... args) {
                        return null;
                    }
                    @Override
                    public Object onFinish(Object... args) {
                        return null;
                    }
                });

        } else {
            // If no image taken, go home
            //Util.goHome(this);
        }
    }
}
