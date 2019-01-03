// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab.ofo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Date;
import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.TargetInterfaceObject;
import edu.wsu.lar.airpact_fire.image.manager.ImageManager;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.ui.target.manager.UiTargetManager;
import edu.wsu.lar.airpact_fire.util.Util;
import edu.wsu.lar.airpact_fire.R;

import static android.app.Activity.RESULT_OK;
import static edu.wsu.lar.airpact_fire.image.manager.ImageManager.adjustAndDisplayBitmap;
import static edu.wsu.lar.airpact_fire.image.manager.ImageManager.captureImage;
import static edu.wsu.lar.airpact_fire.image.manager.ImageManager.rotate;

// TODO: When user retakes image, scrap this imageObject and make a new one!

/**
 * Page resulting from the first image captureImage in a series
 * of two captures.
 *
 * <p>Here a user will captureImage an image of a Point of Interest,
 * place a target on that object, and enter the distance to it.</p>
 *
 * @see edu.wsu.lar.airpact_fire.data.algorithm.ofo.OneForOneAlgorithm
 */
public class OneForOneAlphaFragment extends Fragment {

    private static final String sActionBarTitle = "Target Selection 1/2";
    private static final int sRequestImageCapture = 1;
    private static final int sTargetCount = 1;
    private static final int sFragmentId = 0;

    private PostInterfaceObject mPostInterfaceObject;
    private ImageInterfaceObject mImageInterfaceObject;
    private TargetInterfaceObject mTargetInterfaceObject;

    private UiTargetManager mUiTargetManager;
    private int mSelectedTargetId;

    private EditText mTargetDistanceEditText;
    private ImageView mMainImageView;
    private ImageView mTargetColorImageView;
    private LinearLayout mControlLinearLayout;
    private Button mRetakeButton;
    private Button mFlipButton;
    private Button mProceedButton;

    public OneForOneAlphaFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);
        ((ImageLabActivity) getActivity()).clearPadding();

        // Get fields from activity
        mPostInterfaceObject = ((ImageLabActivity) getActivity()).getPostObject();
        mUiTargetManager = ((ImageLabActivity) getActivity()).getUITargetManager();
        mImageInterfaceObject = mPostInterfaceObject.createImageObject();
        mTargetInterfaceObject = mImageInterfaceObject.createTargetObject();

        // Get views
        View view = inflater.inflate(R.layout.fragment_one_for_one_alpha, container, false);
        mTargetDistanceEditText = (EditText) view.findViewById(R.id.target_distance_edit_text);
        mMainImageView = (ImageView) view.findViewById(R.id.main_image_view);
        mTargetColorImageView = (ImageView) view.findViewById(R.id.target_color_image_view);
        mControlLinearLayout = (LinearLayout) view.findViewById(R.id.control_linear_layout);
        mRetakeButton = view.findViewById(R.id.retake_button);
        mFlipButton = view.findViewById(R.id.flip_button);
        mProceedButton = view.findViewById(R.id.proceed_button);

        // Take pic
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

                // Move the right target
                // TODO: Get target region average, not just one point
                mUiTargetManager.setTargetPosition(mSelectedTargetId, x, y);
                int targetColor = mUiTargetManager.getTargetColor(mSelectedTargetId);
                mTargetColorImageView.setBackgroundColor(targetColor);

                return true;
            }
        });

        // Retaking a picture
        mRetakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(OneForOneAlphaFragment.this, mImageInterfaceObject);
            }
        });

        // Flip image 90 degrees
        mFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(getActivity(), mImageInterfaceObject, mMainImageView);
            }
        });

        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check for no distances entered
                if (Util.isNullOrEmpty(mTargetDistanceEditText.getText().toString())) {
                    Toast.makeText(getContext(), "Please enter distance.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                float targetDistance = Float.parseFloat(
                        mTargetDistanceEditText.getText().toString());
                float[] targetCoordinates = mUiTargetManager
                        .getTargetImagePosition(mSelectedTargetId);

                mTargetInterfaceObject.setDistance(targetDistance);
                mTargetInterfaceObject.setCoordinates(targetCoordinates);
                mUiTargetManager.hideAll();

                Fragment startFragment = new OneForOneBetaFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.image_lab_container, startFragment).addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

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
