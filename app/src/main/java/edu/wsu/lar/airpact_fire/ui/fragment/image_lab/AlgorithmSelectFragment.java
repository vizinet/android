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
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.ArrayList;
import java.util.List;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.SessionObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

public class AlgorithmSelectFragment extends Fragment {

    private static final String sActionBarTitle = "Select Algorithm";

    private AppManager mAppManager;
    private List<Algorithm> mAlgorithms;

    private RadioGroup mAlgorithmRadioGroup;
    private CheckBox mRememberAlgorithmCheckBox;
    private Button mContinueButton;

    public AlgorithmSelectFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        // Grab app manager from parent activity
        mAppManager = ((ImageLabActivity) getActivity()).getAppManager();
        mAlgorithms = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_algorithm_select, container, false);
        mAlgorithmRadioGroup = (RadioGroup) view.findViewById(R.id.algorithm_radio);
        mRememberAlgorithmCheckBox = (CheckBox) view.findViewById(
                R.id.remember_algorithm_check_box);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);

        // Dynamically add choices for algorithms
        DataManager.PostAlgorithm[] postAlgorithms = DataManager.PostAlgorithm.values();
        for (DataManager.PostAlgorithm postAlgorithm : postAlgorithms) {
            Algorithm algorithm = postAlgorithm.getInstance();
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setPadding(20, 0, 0, 0);
            radioButton.setTextSize(20);
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.MATCH_PARENT));
            String radioButtonText = String.format("[%s] %s",
                    algorithm.getAbbreviation(),
                    algorithm.getName());
            radioButton.setText(radioButtonText);
            mAlgorithmRadioGroup.addView(radioButton);
            mAlgorithms.add(algorithm);
        }

        // Listen for "continue"
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserObject userObject = mAppManager.getDataManager().getApp().getLastUser();
                SessionObject sessionObject = mAppManager.getDataManager().getApp()
                        .getLastSession();

                // Update database with selections
                // NOTE: Modulus because radio button ID's are not reallocated once this fragment
                // is repopulated
                int radioButtonId = ((mAlgorithmRadioGroup.getCheckedRadioButtonId() - 1)
                        % mAlgorithmRadioGroup.getChildCount()) + 1;
                userObject.setRememberAlgorithmChoice(mRememberAlgorithmCheckBox.isChecked());
                //sessionObject.setSelectedAlgorithm(radioButtonId);

                // Grab algorithm of choice & notify parent activity
                Algorithm selectedAlgorithm = mAlgorithms.get(radioButtonId - 1);
                ((ImageLabActivity) getActivity()).setAlgorithm(selectedAlgorithm);

                // Give description of algorithm before real action happens
                Fragment startFragment = new AlgorithmStartFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.image_lab_container, startFragment).addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
