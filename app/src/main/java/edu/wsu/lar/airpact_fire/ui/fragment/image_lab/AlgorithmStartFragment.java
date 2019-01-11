// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import edu.wsu.lar.airpact_fire.R;

/**
 * Starting activity for the {@link Algorithm} which gives the
 * description of what the user must do for this algorithm
 * before proceeding to further action.
 */
public class AlgorithmStartFragment extends Fragment {

    private static final String sActionBarTitle = "Algorithm Details";

    private Algorithm mAlgorithm;

    private TextView mAlgorithmTitleTextView;
    private TextView mAlgorithmDescriptionTextView;
    private TextView mAlgorithmProcedureTextView;
    private ImageView mAlgorithmThumbnailImageView;
    private Button mProceedButton;

    public AlgorithmStartFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        // Get algorithm from activity.
        mAlgorithm = ((ImageLabActivity) getActivity()).getAlgorithm();

        // Get views.
        View view = inflater.inflate(R.layout.fragment_algorithm_start, container,
                false);
        mAlgorithmTitleTextView = (TextView) view.findViewById(R.id.algorithm_title_text_view);
        mAlgorithmDescriptionTextView = (TextView) view.findViewById(
                R.id.algorithm_description_text_view);
        mAlgorithmProcedureTextView = (TextView) view.findViewById(
                R.id.algorithm_procedure_text_view);
        mAlgorithmThumbnailImageView = view.findViewById(R.id.algorithm_thumbnail_image_view);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);

        // Present algorithm details.
        mAlgorithmTitleTextView.setText(Html.fromHtml(String.format("<b>[%s]</b> %s",
                mAlgorithm.getAbbreviation().toUpperCase(),
                mAlgorithm.getName().toUpperCase())));
        mAlgorithmDescriptionTextView.setText(mAlgorithm.getDescription());
        mAlgorithmProcedureTextView.setText(mAlgorithm.getProcedure());
        mAlgorithmThumbnailImageView.setImageResource(mAlgorithm.getThumbnailResource());

        // Listen to proceed for algorithm implementation.
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create next fragment based on selected algorithm
                Fragment procedureFragment = (Fragment) mAlgorithm.getStartFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.image_lab_container, procedureFragment).addToBackStack(null)
                            .commit();
            }
        });

        return view;
    }
}
