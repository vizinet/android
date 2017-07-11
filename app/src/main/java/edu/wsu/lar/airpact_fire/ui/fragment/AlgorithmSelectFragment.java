package edu.wsu.lar.airpact_fire.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.data.object.SessionObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlgorithmSelectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlgorithmSelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlgorithmSelectFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AppManager mAppManager;
    private List<Algorithm> mAlgorithms;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RadioGroup mAlgorithmRadioGroup;
    private CheckBox mRememberAlgorithmCheckBox;
    private Button mContinueButton;

    private OnFragmentInteractionListener mListener;

    public AlgorithmSelectFragment() { }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        //args.get
        ///mAppManager = appManager;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlgorithmSelectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlgorithmSelectFragment newInstance(String param1, String param2) {
        AlgorithmSelectFragment fragment = new AlgorithmSelectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Grab app manager from parent activity
        mAppManager = ((ImageLabActivity) getActivity()).getAppManager();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_algorithm_select, container, false);
        mAlgorithmRadioGroup = (RadioGroup) view.findViewById(R.id.algorithm_radio);
        mRememberAlgorithmCheckBox = (CheckBox) view.findViewById(R.id.remember_algorithm_check_box);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);

        // Dynamically add choices for algorithms
        Class[] algorithmClasses = Reference.ALGORITHMS;
        for (Class c : algorithmClasses) {
            try {
                Algorithm algorithm = (Algorithm) c.newInstance();
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
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Listen for "continue"
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserObject userObject = mAppManager.getDataManager().getApp().getLastUser();
                SessionObject sessionObject = mAppManager.getDataManager().getApp()
                        .getLastSession();

                // Update database with selections
                int radioButtonId = mAlgorithmRadioGroup.getCheckedRadioButtonId();
                userObject.setRememberAlgorithmChoice(mRememberAlgorithmCheckBox.isChecked());
                sessionObject.setSelectedAlgorithm(radioButtonId);

                // Create next fragment based on selected algorithm
                Algorithm selectedAlgorithm = mAlgorithms.get(radioButtonId - 1);
                selectedAlgorithm.getFragment();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        */
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
