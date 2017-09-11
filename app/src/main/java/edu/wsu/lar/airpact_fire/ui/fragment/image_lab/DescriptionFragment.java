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
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

/**
 * Near-final page for user to enter description and string-based,
 * custom location for this post.
 */
public class DescriptionFragment extends Fragment {

    private static final String sActionBarTitle = "Description";

    private PostInterfaceObject mPostInterfaceObject;

    private EditText mLocationEditText;
    private EditText mDescriptionEditText;
    private Button mProceedButton;

    public DescriptionFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        mPostInterfaceObject = ((ImageLabActivity) getActivity()).getPostObject();

        View view = inflater.inflate(R.layout.fragment_description, container, false);
        mLocationEditText = (EditText) view.findViewById(R.id.location_edit_text);
        mDescriptionEditText = (EditText) view.findViewById(R.id.description_edit_text);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);

        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPostInterfaceObject.setLocation(mLocationEditText.getText().toString());
                mPostInterfaceObject.setDescription(mDescriptionEditText.getText().toString());

                Fragment nextFragment = new ActionFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.image_lab_container, nextFragment).addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
