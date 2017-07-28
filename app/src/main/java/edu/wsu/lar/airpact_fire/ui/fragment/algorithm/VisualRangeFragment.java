// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.algorithm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

public class VisualRangeFragment extends Fragment {

    private static final String sActionBarTitle = "Visual Range";

    private PostObject mPostObject;

    private ImageView mMainImageView;
    private EditText mVisualRangeDistanceEditText;
    private Button mProceedButton;

    public VisualRangeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        mPostObject = ((ImageLabActivity) getActivity()).getPostObject();

        View view = inflater.inflate(R.layout.fragment_visual_range, container, false);
        mMainImageView = (ImageView) view.findViewById(R.id.main_image_view);
        mVisualRangeDistanceEditText = (EditText) view.findViewById(
                R.id.visual_range_distance_edit_text);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);

        ImageObject imageObject = mPostObject.getImageObjects().get(0);
        mMainImageView.setImageBitmap(imageObject.getImageBitmap());

        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float estimatedVisualRange = Float.parseFloat(
                        mVisualRangeDistanceEditText.getText().toString());
                mPostObject.setEstimatedVisualRange(estimatedVisualRange);

                ((ImageLabActivity) getActivity()).restorePadding();

                // Proceed to enter visual range
                Fragment nextFragment = new DescriptionFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.image_lab_container, nextFragment).addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
