// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab.ofo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.TargetInterfaceObject;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.VisualRangeFragment;
import edu.wsu.lar.airpact_fire.ui.target.manager.UiTargetManager;
import lar.wsu.edu.airpact_fire.R;

import static android.app.Activity.RESULT_OK;

/**
 * Page resulting from the first second capture in a series
 * of two captures, but this time the user must be closer
 * to the same Point of Interest as in {@link OneForOneAlphaFragment}.
 *
 * <p>Same input process as in {@link OneForOneAlphaFragment}.</p>
 *
 * @see edu.wsu.lar.airpact_fire.data.algorithm.ofo.OneForOneAlgorithm
 */
public class OneForOneBetaFragment extends Fragment {

    private static final String sActionBarTitle = "Target Selection 2/2";
    private static final int sRequestImageCapture = 1;
    private static final int sRequestTakePhoto = 1;
    private static final int sTargetCount = 1;
    private static final int sFragmentId = 1;

    private PostInterfaceObject mPostInterfaceObject;
    private ImageInterfaceObject mImageInterfaceObject;
    private TargetInterfaceObject mTargetInterfaceObject;

    private UiTargetManager mUiTargetManager;
    private int mSelectedTargetId;

    private EditText mTargetDistanceEditText;
    private ImageView mMainImageView;
    private ImageView mTargetColorImageView;
    private LinearLayout mControlLinearLayout;
    private Button mProceedButton;

    public OneForOneBetaFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        // Get fields from activity
        mPostInterfaceObject = ((ImageLabActivity) getActivity()).getPostObject();
        mUiTargetManager = ((ImageLabActivity) getActivity()).getUITargetManager();
        mImageInterfaceObject = mPostInterfaceObject.createImageObject();
        mTargetInterfaceObject = mImageInterfaceObject.createTargetObject();

        // Get views
        View view = inflater.inflate(R.layout.fragment_one_for_one_beta, container, false);
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

                // No target_background allowed outside image area
                if (y >= (v.getY() + v.getHeight())) return false;

                // Move the right target_background
                // TODO: Get color of target_background here
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

                mTargetInterfaceObject.setDistance(targetDistance);
                mTargetInterfaceObject.setCoordinates(targetCoordinates);
                mUiTargetManager.hideAll();

                // Proceed to enter visual range
                Fragment nextFragment = new VisualRangeFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.image_lab_container, nextFragment).addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void takePicture() {

        // Ensure that there's a camera activity to handle the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            Uri imageUri = mImageInterfaceObject.createImage();

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
            Bitmap bitmap = mImageInterfaceObject.getBitmap();
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
            mImageInterfaceObject.setImage(bitmap);

            // Set date the moment the image has been captured
            mPostInterfaceObject.setDate(new Date());
            mImageInterfaceObject.setGps(((ImageLabActivity) getActivity())
                    .getAppManager().getGps());
            mMainImageView.setImageBitmap(bitmap);
            mUiTargetManager.setContext(sFragmentId, mMainImageView, sTargetCount);

        } else {
            // If no image taken, go home
            //Util.goHome(this);
        }
    }
}
