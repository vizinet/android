// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab.ofo;

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
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.TargetObject;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.ui.target.manager.UiTargetManager;
import lar.wsu.edu.airpact_fire.R;

import static android.app.Activity.RESULT_OK;

// TODO: When user retakes image, scrap this imageObject and make a new one!

public class OneForOneAlphaFragment extends Fragment {

    private static final String sActionBarTitle = "Target Selection 1/2";
    private static final int sRequestImageCapture = 1;
    private static final int sRequestTakePhoto = 1;
    private static final int sTargetCount = 1;
    private static final int sFragmentId = 0;

    private PostObject mPostObject;
    private ImageObject mImageObject;
    private TargetObject mTargetObject;

    private UiTargetManager mUiTargetManager;
    private int mSelectedTargetId;

    private EditText mTargetDistanceEditText;
    private ImageView mMainImageView;
    private ImageView mTargetColorImageView;
    private LinearLayout mControlLinearLayout;
    private Button mProceedButton;

    public OneForOneAlphaFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);
        ((ImageLabActivity) getActivity()).clearPadding();

        // Get fields from activity
        mPostObject = ((ImageLabActivity) getActivity()).getPostObject();
        mUiTargetManager = ((ImageLabActivity) getActivity()).getUITargetManager();
        mImageObject = mPostObject.createImageObject();
        mTargetObject = mImageObject.createTargetObject();

        // Get views
        View view = inflater.inflate(R.layout.fragment_one_for_one_alpha, container, false);
        mTargetDistanceEditText = (EditText) view.findViewById(R.id.target_distance_edit_text);
        mMainImageView = (ImageView) view.findViewById(R.id.main_image_view);
        mTargetColorImageView = (ImageView) view.findViewById(R.id.target_color_image_view);
        mControlLinearLayout = (LinearLayout) view.findViewById(R.id.control_linear_layout);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);

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

                // Move the right target
                // TODO: Get target region average, not just one point
                mUiTargetManager.setTargetPosition(mSelectedTargetId, x, y);
                int targetColor = mUiTargetManager.getTargetColor(mSelectedTargetId);
                mTargetColorImageView.setBackgroundColor(targetColor);

                return true;
            }
        });

        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float targetDistance = Float.parseFloat(
                        mTargetDistanceEditText.getText().toString());
                float[] targetCoordinates = mUiTargetManager
                        .getTargetImagePosition(mSelectedTargetId);

                mTargetObject.setDistance(targetDistance);
                mTargetObject.setCoordinates(targetCoordinates);
                mUiTargetManager.hideAll();

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

            Uri imageUri = mImageObject.createImage();

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
            Bitmap bitmap = mImageObject.getBitmap();
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
            mImageObject.setImage(bitmap);

            // Set date the moment the image has been captured
            // TODO: Make the date in the image object
            mPostObject.setDate(new Date());

            // Add placeholder geolocation
            mImageObject.setGps(new double[] {
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
                mImageObject.setGps(new double[] { loc.getLatitude(), loc.getLatitude() });
            }

            mMainImageView.setImageBitmap(bitmap);
            mUiTargetManager.setContext(sFragmentId, mMainImageView, sTargetCount);

        } else {
            // If no image taken, go home
            //Util.goHome(this);
        }
    }
}
