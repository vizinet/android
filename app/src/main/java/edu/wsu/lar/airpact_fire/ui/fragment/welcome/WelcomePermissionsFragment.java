// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.welcome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.wsu.lar.airpact_fire.R;
import edu.wsu.lar.airpact_fire.ui.activity.WelcomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomePermissionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomePermissionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomePermissionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String sPermissionCountFormat = "Permissions accepted: %d/%d";

    private int requestPermissionCode;
    private String[] permissionCodes;

    private TextView mPermissionCountTextView;
    private Button mProceedButton;

    private OnFragmentInteractionListener mListener;

    public WelcomePermissionsFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param requestPermissionCode
     * @param permissionCodes
     * @return A new instance of fragment WelcomePermissionsFragment.
     */
    public static WelcomePermissionsFragment newInstance(int requestPermissionCode,
                                                         String[] permissionCodes) {
        WelcomePermissionsFragment fragment = new WelcomePermissionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, requestPermissionCode);
        args.putStringArray(ARG_PARAM2, permissionCodes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestPermissionCode = getArguments().getInt(ARG_PARAM1);
            permissionCodes = getArguments().getStringArray(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_permissions, container, false);
        mPermissionCountTextView = view.findViewById(R.id.permission_count_text_view);
        mProceedButton = view.findViewById(R.id.proceed_button);

        int deniedPermissionCount = ((WelcomeActivity) getActivity()).getDeniedPermissions();
        int allowedPermissionCount = permissionCodes.length - deniedPermissionCount;
        mPermissionCountTextView.setText(String.format(sPermissionCountFormat,
                allowedPermissionCount, permissionCodes.length));
        mProceedButton.setOnClickListener(clickedView -> requestPermissions());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    /**
     * Proceed to main app iff user accepts permissions.
     *
     * @param deniedCount number of denied permissions
     */
    public void updatePermissionStatus(int deniedCount) {
        if (deniedCount == 0) {
            ((WelcomeActivity) getActivity()).proceed();
            return;
        }
        int acceptedCount = permissionCodes.length - deniedCount;
        mPermissionCountTextView.setText(String.format(sPermissionCountFormat, acceptedCount,
                permissionCodes.length));
        Toast.makeText(getActivity(), "Please accept all permissions before continuing.",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Request user to explicitly accept app permissions.
     */
    public void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), permissionCodes, requestPermissionCode);
    }
}
