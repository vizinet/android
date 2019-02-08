// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.welcome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.wsu.lar.airpact_fire.R;
import edu.wsu.lar.airpact_fire.ui.StreamDrawable;

public class WelcomeIntroductionFragment extends Fragment {

    private TextView mWelcomeTitleTextView;
    private ImageView mSampleImageView;

    private OnFragmentInteractionListener mListener;

    public WelcomeIntroductionFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param requestPermissionCode
     * @param permissionCodes
     * @return A new instance of fragment WelcomePermissionsFragment.
     */
    public static WelcomeIntroductionFragment newInstance() {
        WelcomeIntroductionFragment fragment = new WelcomeIntroductionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_introduction, container, false);
        mWelcomeTitleTextView = view.findViewById(R.id.welcome_title_text_view);
        mSampleImageView = view.findViewById(R.id.sample_image_view);

        int radius = 20;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.taking_nature_photo);
        StreamDrawable d = new StreamDrawable(bitmap, radius, 0);
        mSampleImageView.setBackground(d);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
}
