// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab.tio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.wsu.lar.airpact_fire.R;
import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.TargetInterfaceObject;
import edu.wsu.lar.airpact_fire.image.manager.ImageManager;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.VisualRangeFragment;
import edu.wsu.lar.airpact_fire.ui.target.manager.UiTargetManager;
import edu.wsu.lar.airpact_fire.util.Util;

import static android.app.Activity.RESULT_OK;

/**
 * Single page for image captureImage and placement of two targets on two Points of Interest, along
 * with the inputting of their corresponding distance away from the user.
 *
 * @see edu.wsu.lar.airpact_fire.data.algorithm.tio.TwoInOneAlgorithm
 */
public class TwoInOneFragment extends Fragment {

    private static final int sTargetCount = 2;
    private static final int sFragmentId = 3;

    private PostInterfaceObject mPostInterfaceObject;
    private ImageInterfaceObject mImageInterfaceObject;
    private TargetInterfaceObject mTargetInterfaceObjectOne;
    private TargetInterfaceObject mTargetInterfaceObjectTwo;

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
    private Button mFlipButton;
    private Button mProceedButton;
    private ProgressBar mFlipProgressBar;

    public TwoInOneFragment() { }

    /**
     * Capture new image.
     *
     * Hide view and targets before picture has been captured and processed.
     */
    private void capture(View view) {
        view.setVisibility(View.INVISIBLE);
        mUiTargetManager.hideAll();
        ImageManager.captureImage(this, mImageInterfaceObject);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ((ImageLabActivity) getActivity()).clearPadding();

        // Get fields from activity.
        mPostInterfaceObject = ((ImageLabActivity) getActivity()).getPostObject();
        mUiTargetManager = ((ImageLabActivity) getActivity()).getUITargetManager();
        mImageInterfaceObject = mPostInterfaceObject.createImageObject();
        mTargetInterfaceObjectOne = mImageInterfaceObject.createTargetObject();
        mTargetInterfaceObjectTwo = mImageInterfaceObject.createTargetObject();

        // Get standard views.
        View view = inflater.inflate(R.layout.fragment_two_in_one, container, false);
        mMainImageView = view.findViewById(R.id.main_image_view);
        mTargetOneDistanceLinearLayout = view.findViewById(R.id.target_one_distance_linear_layout);
        mTargetTwoDistanceLinearLayout = view.findViewById(R.id.target_two_distance_linear_layout);
        mControlLinearLayout = view.findViewById(R.id.control_linear_layout);
        mTargetOneIdTextView = view.findViewById(R.id.target_one_id_text_view);
        mTargetTwoIdTextView = view.findViewById(R.id.target_two_id_text_view);
        mTargetOneDistanceEditText = view.findViewById(R.id.target_one_distance_edit_text);
        mTargetTwoDistanceEditText = view.findViewById(R.id.target_two_distance_edit_text);
        mTargetOneMetricAbbreviationTextView = view.findViewById(
                R.id.target_one_metric_abbreviation);
        mTargetTwoMetricAbbreviationTextView = view.findViewById(
                R.id.target_two_metric_abbreviation);
        mRetakeButton = view.findViewById(R.id.retake_button);
        mProceedButton = view.findViewById(R.id.proceed_button);
        mFlipProgressBar = view.findViewById(R.id.flip_progress_bar);

        // Store target color references in list.
        mTargetColorImageViews = new ArrayList<>();
        mTargetColorImageViews.add(view.findViewById(R.id.target_one_color_image_view));
        mTargetColorImageViews.add(view.findViewById(R.id.target_two_color_image_view));

        // Set selected target.
        mSelectedTargetId = 0;
        mTargetOneDistanceLinearLayout.setBackgroundColor(Color.parseColor("#EEEEEEEE"));

        // Target Movement
        mMainImageView.setOnTouchListener((v, event) -> {

            // Handle displays.
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mControlLinearLayout.setVisibility(View.VISIBLE);
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mControlLinearLayout.setVisibility(View.GONE);
            }

            // Get touch coordinates.
            int x = (int) event.getX();
            int y = (int) event.getY();

            mUiTargetManager.setTargetPosition(mSelectedTargetId, x, y);
            int targetColor = mUiTargetManager.getTargetColor(mSelectedTargetId);
            mTargetColorImageViews.get(mSelectedTargetId).setBackgroundColor(targetColor);

            return true;
        });

        // Target Selection
        mTargetOneDistanceLinearLayout.setOnTouchListener((view15, motionEvent) -> {
            mTargetOneDistanceLinearLayout.setBackgroundColor(
                    Color.parseColor("#EEEEEEEE"));
            mTargetTwoDistanceLinearLayout.setBackgroundColor(Color.TRANSPARENT);
            mSelectedTargetId = 0;
            return false;
        });
        mTargetOneDistanceEditText.setOnTouchListener((view14, motionEvent) -> {
            mTargetOneDistanceLinearLayout.setBackgroundColor(
                    Color.parseColor("#EEEEEEEE"));
            mTargetTwoDistanceLinearLayout.setBackgroundColor(Color.TRANSPARENT);
            mSelectedTargetId = 0;
            return false;
        });
        mTargetTwoDistanceLinearLayout.setOnTouchListener((view13, motionEvent) -> {
            mTargetTwoDistanceLinearLayout.setBackgroundColor(
                    Color.parseColor("#EEEEEEEE"));
            mTargetOneDistanceLinearLayout.setBackgroundColor(Color.TRANSPARENT);
            mSelectedTargetId = 1;
            return false;
        });
        mTargetTwoDistanceEditText.setOnTouchListener((view12, motionEvent) -> {
            mTargetTwoDistanceLinearLayout.setBackgroundColor(
                    Color.parseColor("#EEEEEEEE"));
            mTargetOneDistanceLinearLayout.setBackgroundColor(Color.TRANSPARENT);
            mSelectedTargetId = 1;
            return false;
        });

        // TODO: All these controls should be centralized in a manager, not amongst fragments.

        // Retake a picture.
        mRetakeButton.setOnClickListener(v -> {
            capture(getView());
        });

        // Moving forward
        mProceedButton.setOnClickListener(view1 -> {

            // Check for no distances entered
            if (Util.isNullOrEmpty(mTargetOneDistanceEditText.getText().toString())
                    || Util.isNullOrEmpty(mTargetTwoDistanceEditText.getText().toString())) {
                Toast.makeText(getContext(), "Please enter both distances.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            float targetOneDistance = Float.parseFloat(
                    mTargetOneDistanceEditText.getText().toString());
            float[] targetOneCoordinates = mUiTargetManager
                    .getTargetImagePosition(0);
            float targetTwoDistance = Float.parseFloat(
                    mTargetTwoDistanceEditText.getText().toString());
            float[] targetTwoCoordinates = mUiTargetManager
                    .getTargetImagePosition(1);

            mTargetInterfaceObjectOne.setDistance(targetOneDistance);
            mTargetInterfaceObjectOne.setCoordinates(targetOneCoordinates);
            mTargetInterfaceObjectTwo.setDistance(targetTwoDistance);
            mTargetInterfaceObjectTwo.setCoordinates(targetTwoCoordinates);

            mUiTargetManager.hideAll();

            Fragment startFragment = new VisualRangeFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.image_lab_container, startFragment).addToBackStack(null)
                    .commit();
        });

        capture(view);

        return view;
    }

    /**
     * Handle image-capture activity result from {@link ImageManager#captureImage}.
     *
     * @param requestCode   code of activity request
     * @param resultCode    code indicating failure/success
     * @param data          additional data resulting from activity (should be none)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            assert requestCode != ImageManager.REQUEST_IMAGE_CAPTURE_CODE;
            assert resultCode == RESULT_OK;

            Handler handler = new Handler();
            ((ImageLabActivity) getActivity()).setProgressBarVisible(true);

            // -----
            // TODO: Get all this to work, and then think about the principled approach.
            // -----

            // Pre-processing of data
            Bitmap bitmap = mImageInterfaceObject.getBitmap();
            String imageUri = mImageInterfaceObject.getImageFile().getAbsolutePath();

            assert bitmap != null;

            new Thread(() -> {

                // Main processing of the data
                Bitmap[] processedBitmaps = ImageManager.processBitmap(bitmap, getActivity(),
                        imageUri);

                // Post-processing in UI thread
                handler.post(() -> {

                    // TODO: We need the below validation to occur, and it's not failing this
                    // assertion in the UI even when we know the bitmap is non-null.
                    assert processedBitmaps != null;

                    ((ImageLabActivity) getActivity()).setProgressBarVisible(false);
                    getView().setVisibility(View.VISIBLE);
                    mImageInterfaceObject.setImage(processedBitmaps[0], processedBitmaps[1]);
                    mMainImageView.setImageBitmap(processedBitmaps[1]);

                    // Set post fields independent of raw image.
                    mPostInterfaceObject.setDate(new Date());
                    LatLng recentLatLng = ((ImageLabActivity) getActivity()).getUserObject()
                            .getRecentLatLng();
                    mImageInterfaceObject.setGps(new double[] {recentLatLng.latitude,
                            recentLatLng.longitude});
                    mUiTargetManager.setContext(sFragmentId, mMainImageView, sTargetCount);
                });
            }).start();

        } catch (Exception exception) {

            // Indicate failure to user.
            Toast.makeText(getContext(),
                    "Camera failed to take picture. Please try again later.",
                    Toast.LENGTH_LONG).show();

            // Report the issue to backend.
            exception = new Exception("Camera failed to take a picture.", exception);
            ((ImageLabActivity) getActivity()).getServerManager().reportException(exception);

            Util.goHome(getActivity());
            return;
        }
    }
}
