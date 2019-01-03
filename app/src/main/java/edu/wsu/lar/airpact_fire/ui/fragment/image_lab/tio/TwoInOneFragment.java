// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab.tio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.TargetInterfaceObject;
import edu.wsu.lar.airpact_fire.image.manager.ImageManager;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.VisualRangeFragment;
import edu.wsu.lar.airpact_fire.ui.target.manager.UiTargetManager;
import edu.wsu.lar.airpact_fire.util.Util;
import edu.wsu.lar.airpact_fire.R;

import static android.app.Activity.RESULT_OK;
import static edu.wsu.lar.airpact_fire.image.manager.ImageManager.adjustAndDisplayBitmap;
import static edu.wsu.lar.airpact_fire.image.manager.ImageManager.captureImage;
import static edu.wsu.lar.airpact_fire.image.manager.ImageManager.rotate;

/**
 * Single page for image captureImage and placement of two targets on
 * two Points of Interest, along with the inputting of their
 * corresponding distance away from the user.
 *
 * @see edu.wsu.lar.airpact_fire.data.algorithm.tio.TwoInOneAlgorithm
 */
public class TwoInOneFragment extends Fragment {

    private static final String sActionBarTitle = "Target Selections";
//    private static final int sRequestImageCapture = 1;
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
    private FrameLayout mImageAreaLayout;
    private FrameLayout mProgressBarLayout;
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

    public TwoInOneFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);
        ((ImageLabActivity) getActivity()).clearPadding();

        // Get fields from activity
        mPostInterfaceObject = ((ImageLabActivity) getActivity()).getPostObject();
        mUiTargetManager = ((ImageLabActivity) getActivity()).getUITargetManager();
        mImageInterfaceObject = mPostInterfaceObject.createImageObject();
        mTargetInterfaceObjectOne = mImageInterfaceObject.createTargetObject();
        mTargetInterfaceObjectTwo = mImageInterfaceObject.createTargetObject();

        // Get standard views
        View view = inflater.inflate(R.layout.fragment_two_in_one, container, false);
        mMainImageView = (ImageView) view.findViewById(R.id.main_image_view);
        mImageAreaLayout = view.findViewById(R.id.image_area_layout);
        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout);
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
        mRetakeButton = (Button) view.findViewById(R.id.retake_button);
        mFlipButton = (Button) view.findViewById(R.id.flip_button);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);
        mFlipProgressBar = view.findViewById(R.id.flip_progress_bar);

        // Store target color references in list
        mTargetColorImageViews = new ArrayList<ImageView>();
        mTargetColorImageViews.add((ImageView) view.findViewById(R.id.target_one_color_image_view));
        mTargetColorImageViews.add((ImageView) view.findViewById(R.id.target_two_color_image_view));

        // Set selected target
        mSelectedTargetId = 0;
        mTargetOneDistanceLinearLayout.setBackgroundColor(Color.parseColor("#EEEEEEEE"));

        // Get image from user
        captureImage(this, mImageInterfaceObject);

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
                mTargetOneDistanceLinearLayout.setBackgroundColor(
                        Color.parseColor("#EEEEEEEE"));
                mTargetTwoDistanceLinearLayout.setBackgroundColor(Color.TRANSPARENT);
                mSelectedTargetId = 0;
                return false;
            }
        });
        mTargetTwoDistanceLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mTargetTwoDistanceLinearLayout.setBackgroundColor(
                        Color.parseColor("#EEEEEEEE"));
                mTargetOneDistanceLinearLayout.setBackgroundColor(Color.TRANSPARENT);
                mSelectedTargetId = 1;
                return false;
            }
        });

        // TODO: All these controls should be centralized in a manager, not amongst fragments

        // Retaking a picture
        mRetakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(TwoInOneFragment.this, mImageInterfaceObject);
            }
        });

        // Flip image 90 degrees
        mFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(getActivity(), mImageInterfaceObject, mMainImageView);
            }
        });

        // Moving forward
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });

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

        if ((requestCode == ImageManager.REQUEST_IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) &&
                null != adjustAndDisplayBitmap(getActivity(),
                        mImageInterfaceObject, mMainImageView)) {
            mPostInterfaceObject.setDate(new Date());
            mImageInterfaceObject.setGps(((ImageLabActivity) getActivity())
                    .getAppManager().getGps());
            mUiTargetManager.setContext(sFragmentId, mMainImageView, sTargetCount);
        } else {
            // If no image taken or error, go home
            Toast.makeText(getContext(),
                    "Camera failed to take picture. Please try again later.",
                    Toast.LENGTH_LONG).show();
            Util.goHome(getActivity());
        }
    }
}
