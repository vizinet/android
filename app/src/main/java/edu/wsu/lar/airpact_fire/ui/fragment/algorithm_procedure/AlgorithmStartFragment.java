// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.algorithm_procedure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

/**
 * Starting activity for the {@link edu.wsu.lar.airpact_fire.data.algorithm.OneForOneAlgorithm}
 * which gives the description of what the user must do for this algorithm before proceeding.
 *
 * @since 0.9
 */
public class AlgorithmStartFragment extends Fragment {

    private static final String sActionBarTitle = "Algorithm Details";

    private Algorithm mAlgorithm;

    private TextView mAlgorithmTitleTextView;
    private TextView mAlgorithmDescriptionTextView;
    private TextView mAlgorithmProcedureTextView;
    private Button mProceedButton;

    public AlgorithmStartFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set action bar title
       ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        // Get algorithm from activity
        mAlgorithm = ((ImageLabActivity) getActivity()).getAlgorithm();

        // Get views
        View view = inflater.inflate(R.layout.fragment_algorithm_start, container, false);
        mAlgorithmTitleTextView = (TextView) view.findViewById(R.id.algorithm_title_text_view);
        mAlgorithmDescriptionTextView = (TextView) view.findViewById(
                R.id.algorithm_description_text_view);
        mAlgorithmProcedureTextView = (TextView) view.findViewById(
                R.id.algorithm_procedure_text_view);
        mProceedButton = (Button) view.findViewById(R.id.proceed_button);

        // Present algorithm details
        mAlgorithmTitleTextView.setText(String.format(
                "[%s] %s",
                mAlgorithm.getAbbreviation(),
                mAlgorithm.getName()));
        mAlgorithmDescriptionTextView.setText(mAlgorithm.getDescription());
        mAlgorithmProcedureTextView.setText(mAlgorithm.getProcedure());

        // Listen to proceed for algorithm implementation
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create next fragment based on selected algorithm
                Fragment procedureFragment = (Fragment) mAlgorithm.getFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.image_lab_container, procedureFragment).addToBackStack(null)
                            .commit();
            }
        });

        return view;
    }
}
