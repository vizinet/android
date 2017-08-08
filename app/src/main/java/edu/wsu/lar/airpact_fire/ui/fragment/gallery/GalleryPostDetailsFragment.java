// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.ui.activity.GalleryActivity;
import lar.wsu.edu.airpact_fire.R;

public class GalleryPostDetailsFragment extends Fragment {

    private static final String sActionBarTitle = "Post Details";

    private PostObject mPostObject;

    private ImageView mPostImageView;
    private TextView mPostStatusTextView;
    private TextView mAlgorithmTypeTextView;
    private TextView mLocationTextView;
    private TextView mEstimatedVisualRangeTextView;
    private TextView mComputedVisualRangeTextView;
    private TextView mDescriptionTextView;
    private TextView mDatetimeTextView;

    public GalleryPostDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((GalleryActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        View view = inflater.inflate(R.layout.fragment_gallery_post_details, container, false);
        mPostImageView = (ImageView) view.findViewById(R.id.post_image_view);
        mPostStatusTextView = (TextView) view.findViewById(R.id.post_status_text_view);
        mAlgorithmTypeTextView = (TextView) view.findViewById(R.id.algorithm_type_text_view);
        mLocationTextView = (TextView) view.findViewById(R.id.location_text_view);
        mEstimatedVisualRangeTextView = (TextView) view.findViewById(
                R.id.estimated_visual_range_text_view);
        mComputedVisualRangeTextView = (TextView) view.findViewById(
                R.id.computed_visual_range_text_view);
        mDescriptionTextView = (TextView) view.findViewById(R.id.description_text_view);
        mDatetimeTextView = (TextView) view.findViewById(R.id.datetime_text_view);

        // Populate views with post details
        mPostImageView.setImageBitmap(mPostObject.getThumbnail());
        mPostStatusTextView.setText(Reference.POST_MODES[mPostObject.getMode() - 1]);
        mAlgorithmTypeTextView.setText(Reference.ALGORITHMS[mPostObject.getAlgorithm() - 1]
                .getName());
        mLocationTextView.setText(mPostObject.getLocation());
        mEstimatedVisualRangeTextView.setText("" + mPostObject.getEstimatedVisualRange());
        mComputedVisualRangeTextView.setText("" + mPostObject.getComputedVisualRange());
        mDescriptionTextView.setText(mPostObject.getDescription());
        mDatetimeTextView.setText(mPostObject.getDate().toString());

        return view;
    }

    public void setPostObject(PostObject postObject) {
        mPostObject = postObject;
    }
}
