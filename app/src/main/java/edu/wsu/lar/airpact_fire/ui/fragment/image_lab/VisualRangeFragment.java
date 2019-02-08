// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.util.Util;
import edu.wsu.lar.airpact_fire.R;

/**
 * Fragment for user to enter the visual range of the first
 * (and possibly the only) picture they captured for this
 * particular {@link edu.wsu.lar.airpact_fire.data.algorithm.Algorithm}.
 */
public class VisualRangeFragment extends Fragment {

    private static final String sActionBarTitle = "Visual Range";

    private PostInterfaceObject mPostInterfaceObject;

    private ImageView mMainImageView;
    private EditText mVisualRangeDistanceEditText;
    private Button mProceedButton;

    public VisualRangeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
//        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        mPostInterfaceObject = ((ImageLabActivity) getActivity()).getPostObject();

        View view = inflater.inflate(R.layout.fragment_visual_range, container, false);
        mMainImageView = (ImageView) view.findViewById(R.id.main_image_view);
        mVisualRangeDistanceEditText = (EditText) view.findViewById(
                R.id.visual_range_distance_edit_text);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);

        ImageInterfaceObject imageInterfaceObject = mPostInterfaceObject.getImageObjects().get(0);
        mMainImageView.setImageBitmap(imageInterfaceObject.getThumbnail());

        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String estimatedVisualRangeRaw = mVisualRangeDistanceEditText.getText().toString();
                if (Util.isNullOrEmpty(estimatedVisualRangeRaw)) {
                    Toast.makeText(getContext(), "Please enter a value.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                float estimatedVisualRange = Float.parseFloat(estimatedVisualRangeRaw);
                mPostInterfaceObject.setEstimatedVisualRange(estimatedVisualRange);

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
