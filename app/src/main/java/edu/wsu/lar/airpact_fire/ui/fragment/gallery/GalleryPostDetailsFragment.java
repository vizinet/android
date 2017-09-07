// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.server.callback.SubmissionServerCallback;
import edu.wsu.lar.airpact_fire.ui.activity.GalleryActivity;
import lar.wsu.edu.airpact_fire.R;

/**
 * Page for showing a single post's complete details.
 */
public class GalleryPostDetailsFragment extends Fragment {

    private static final String sActionBarTitle = "Post Details";

    private AppManager mAppManager;
    private PostInterfaceObject mPostInterfaceObject;

    private ImageView mPostImageView;
    private ImageView mSubmittedImageView;
    private ImageView mQueuedImageView;
    private TextView mPostStatusTextView;
    private TextView mAlgorithmTypeTextView;
    private TextView mLocationTextView;
    private TextView mEstimatedVisualRangeTextView;
    private TextView mComputedVisualRangeTextView;
    private TextView mDescriptionTextView;
    private TextView mDatetimeTextView;
    private Button mWebButton;
    private Button mSubmitButton;
    private Button mMapsButton;

    public GalleryPostDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((GalleryActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        mAppManager = ((GalleryActivity) getActivity()).getAppManager();

        View view = inflater.inflate(R.layout.fragment_gallery_post_details, container, false);
        mPostImageView = (ImageView) view.findViewById(R.id.post_image_view);
        mSubmittedImageView = (ImageView) view.findViewById(R.id.submitted_image_view);
        mQueuedImageView = (ImageView) view.findViewById(R.id.queued_image_view);
        mPostStatusTextView = (TextView) view.findViewById(R.id.post_status_text_view);
        mAlgorithmTypeTextView = (TextView) view.findViewById(R.id.algorithm_type_text_view);
        mLocationTextView = (TextView) view.findViewById(R.id.location_text_view);
        mEstimatedVisualRangeTextView = (TextView) view.findViewById(
                R.id.estimated_visual_range_text_view);
        mComputedVisualRangeTextView = (TextView) view.findViewById(
                R.id.computed_visual_range_text_view);
        mDescriptionTextView = (TextView) view.findViewById(R.id.description_text_view);
        mDatetimeTextView = (TextView) view.findViewById(R.id.datetime_text_view);
        mWebButton = (Button) view.findViewById(R.id.web_button);
        mSubmitButton = (Button) view.findViewById(R.id.submit_button);
        mMapsButton = (Button) view.findViewById(R.id.maps_button);

        populateDetails();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmissionServerCallback submissionServerCallback =
                        new SubmissionServerCallback(getActivity(), mPostInterfaceObject) {
                            @Override
                            public Object onFinish(Object... args) {
                                super.onFinish(args);
                                populateDetails();
                                return null;
                            }
                        };
                mAppManager.getServerManager().onSubmit(getActivity(), mPostInterfaceObject,
                        submissionServerCallback);
            }
        });
        mWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(
                        Reference.SERVER_IMAGE_BASE_URL + mPostInterfaceObject.getServerId()));
                startActivity(intent);
            }
        });
        mMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double[] gps = mPostInterfaceObject.getImageObjects().get(0).getGps();
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format("geo:%s,%s?q=%s,%s(%s+%s)",
                                gps[0], gps[1],
                                gps[0], gps[1],
                                mPostInterfaceObject.getLocation(), "")));
                startActivity(intent);
            }
        });

        return view;
    }

    public void setArguments(PostInterfaceObject postInterfaceObject) {
        mPostInterfaceObject = postInterfaceObject;
    }

    private void populateDetails() {

        // Get post mode & algorithm (two distinguishing features)
        DataManager.PostMode postMode = DataManager.getPostMode(mPostInterfaceObject.getMode());
        DataManager.PostAlgorithm postAlgorithm =
                DataManager.getAlgorithm(mPostInterfaceObject.getAlgorithm());

        // Display differently for queued images
        if (postMode == DataManager.PostMode.QUEUED) {
            // Post is queued
            mSubmittedImageView.setVisibility(View.GONE);
            mWebButton.setVisibility(View.GONE);
            mComputedVisualRangeTextView.setVisibility(View.GONE);
            mSubmitButton.setVisibility(View.VISIBLE);
            mQueuedImageView.setVisibility(View.VISIBLE);
            mPostStatusTextView.setText(String.format("%s (submit below)", postMode.getName()));
        } else {
            // Post has been submitted
            mSubmittedImageView.setVisibility(View.VISIBLE);
            mWebButton.setVisibility(View.VISIBLE);
            mComputedVisualRangeTextView.setVisibility(View.VISIBLE);
            mSubmitButton.setVisibility(View.GONE);
            mQueuedImageView.setVisibility(View.GONE);
            mPostStatusTextView.setText(postMode.getName());
        }

        // Populate views with post details
        mPostImageView.setImageBitmap(mPostInterfaceObject.getThumbnail());
        mLocationTextView.setText(mPostInterfaceObject.getLocation());
        mEstimatedVisualRangeTextView.setText(
                String.format("%s (estimated)", mPostInterfaceObject.getEstimatedVisualRange()));
        mComputedVisualRangeTextView.setText(
                String.format("%s (computed)", mPostInterfaceObject.getComputedVisualRange()));
        mAlgorithmTypeTextView.setText(postAlgorithm.getInstance().getAbbreviation());
        mDescriptionTextView.setText(mPostInterfaceObject.getDescription());
        mDatetimeTextView.setText(mPostInterfaceObject.getDate().toString());
    }
}
