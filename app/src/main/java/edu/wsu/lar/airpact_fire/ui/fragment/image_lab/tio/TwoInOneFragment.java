// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab.tio;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.TargetObject;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.VisualRangeFragment;
import edu.wsu.lar.airpact_fire.ui.target.manager.UiTargetManager;
import lar.wsu.edu.airpact_fire.R;

import static android.app.Activity.RESULT_OK;

public class TwoInOneFragment extends Fragment {

    private static final String sActionBarTitle = "Target Selections";
    private static final int sRequestImageCapture = 1;
    private static final int sRequestTakePhoto = 1;
    private static final int sTargetCount = 2;
    private static final int sFragmentId = 3;

    private PostObject mPostObject;
    private ImageObject mImageObject;
    private TargetObject mTargetObjectOne;
    private TargetObject mTargetObjectTwo;

    private UiTargetManager mUiTargetManager;
    private List<ImageView> mTargetColorImageViews;
    private int mSelectedTargetId;

    private ImageView mMainImageView;
    private LinearLayout mControlLinearLayout;
    private LinearLayout mTargetOneDistanceLinearLayout;
    private LinearLayout mTargetTwoDistanceLinearLayout;
    private TextView mTargetOneIdTextView;
    private TextView mTargetTwoIdTextView;
    private EditText mTargetOneDistanceEditText;
    private EditText mTargetTwoDistanceEditText;
    private TextView mTargetOneMetricAbbreviationTextView;
    private TextView mTargetTwoMetricAbbreviationTextView;
    private Button mRetakeButton;
    private Button mProceedButton;

    public TwoInOneFragment() { }

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
        mTargetObjectOne = mImageObject.createTargetObject();
        mTargetObjectTwo = mImageObject.createTargetObject();

        // Get standard views
        View view = inflater.inflate(R.layout.fragment_two_in_one, container, false);
        mMainImageView = (ImageView) view.findViewById(R.id.main_image_view);
        mTargetOneDistanceLinearLayout = (LinearLayout) view.findViewById(
                R.id.target_one_distance_linear_layout);
        mTargetTwoDistanceLinearLayout = (LinearLayout) view.findViewById(
                R.id.target_two_distance_linear_layout);
        mControlLinearLayout = (LinearLayout) view.findViewById(R.id.control_linear_layout);
        mTargetOneIdTextView = (TextView) view.findViewById(R.id.target_one_id_text_view);
        mTargetTwoIdTextView = (TextView) view.findViewById(R.id.target_two_id_text_view);
        mTargetOneDistanceEditText = (EditText) view.findViewById(
                R.id.target_one_distance_edit_text);
        mTargetTwoDistanceEditText = (EditText) view.findViewById(
                R.id.target_two_distance_edit_text);
        mTargetOneMetricAbbreviationTextView = (TextView) view.findViewById(
                R.id.target_one_metric_abbreviation);
        mTargetTwoMetricAbbreviationTextView = (TextView) view.findViewById(
                R.id.target_two_metric_abbreviation);
        //mRetakeButton = (Button) view.findViewById(R.id.retake_button);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);

        // Store target color references in list
        mTargetColorImageViews = new ArrayList<ImageView>();
        mTargetColorImageViews.add((ImageView) view.findViewById(R.id.target_one_color_image_view));
        mTargetColorImageViews.add((ImageView) view.findViewById(R.id.target_two_color_image_view));

        // Set selected target
        mSelectedTargetId = 0;
        mTargetOneDistanceLinearLayout.setBackgroundColor(Color.parseColor("#EEEEEEEE"));

        // Get image from user
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

                mUiTargetManager.setTargetPosition(mSelectedTargetId, x, y);
                int targetColor = mUiTargetManager.getTargetColor(mSelectedTargetId);
                mTargetColorImageViews.get(mSelectedTargetId).setBackgroundColor(targetColor);

                return true;
            }
        });

        // Target selection
        mTargetOneDistanceLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mTargetOneDistanceLinearLayout.setBackgroundColor(Color.parseColor("#EEEEEEEE"));
                mTargetTwoDistanceLinearLayout.setBackgroundColor(Color.TRANSPARENT);
                mSelectedTargetId = 0;
                return false;
            }
        });
        mTargetTwoDistanceLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mTargetTwoDistanceLinearLayout.setBackgroundColor(Color.parseColor("#EEEEEEEE"));
                mTargetOneDistanceLinearLayout.setBackgroundColor(Color.TRANSPARENT);
                mSelectedTargetId = 1;
                return false;
            }
        });

        // Moving forward
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float targetOneDistance = Float.parseFloat(
                        mTargetOneDistanceEditText.getText().toString());
                float[] targetOneCoordinates = mUiTargetManager
                        .getTargetImagePosition(0);
                float targetTwoDistance = Float.parseFloat(
                        mTargetTwoDistanceEditText.getText().toString());
                float[] targetTwoCoordinates = mUiTargetManager
                        .getTargetImagePosition(1);

                mTargetObjectOne.setDistance(targetOneDistance);
                mTargetObjectOne.setCoordinates(targetOneCoordinates);
                mTargetObjectTwo.setDistance(targetTwoDistance);
                mTargetObjectTwo.setCoordinates(targetTwoCoordinates);

                mUiTargetManager.hideAll();

                Fragment startFragment = new VisualRangeFragment();
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

            mPostObject.setDate(new Date());
            mImageObject.setGps(((ImageLabActivity) getActivity()).getAppManager().getGps());
            mMainImageView.setImageBitmap(bitmap);
            mUiTargetManager.setContext(sFragmentId, mMainImageView, sTargetCount);

        } else {
            // If no image taken, go home
            //Util.goHome(this);
        }
    }
}
