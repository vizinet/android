// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;

import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.ui.activity.GalleryActivity;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

public class MainGalleryFragment extends Fragment {

    private static final String sActionBarTitle = "Gallery";

    private AppManager mAppManager;

    public MainGalleryFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((GalleryActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        mAppManager = ((GalleryActivity) getActivity()).getAppManager();

        View view = inflater.inflate(R.layout.fragment_main_gallery, container, false);

        return view;
    }
}
