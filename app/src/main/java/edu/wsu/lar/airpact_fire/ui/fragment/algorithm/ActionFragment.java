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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

public class ActionFragment extends Fragment {

    private static final String sActionBarTitle = "Action";

    private AppManager mAppManager;
    private PostObject mPostObject;

    private Button mDiscardButton;
    private Button mQueueButton;
    private Button mSubmitButton;

    public ActionFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((ImageLabActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        mAppManager = ((ImageLabActivity) getActivity()).getAppManager();
        mPostObject = ((ImageLabActivity) getActivity()).getPostObject();

        View view = inflater.inflate(R.layout.fragment_action, container, false);
        mDiscardButton = (Button) view.findViewById(R.id.discard_button);
        mQueueButton = (Button) view.findViewById(R.id.queue_button);
        mSubmitButton = (Button) view.findViewById(R.id.submit_button);

        mDiscardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Delete post
                mPostObject.delete();
                goHome();
            }
        });

        mQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostObject.setMode(PostObject.PostMode.QUEUED.ordinal() + 1);
                goHome();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAppManager.getServerManager().onSubmit(
                        getActivity(), mPostObject, new ServerManager.ServerCallback() {
                    @Override
                    public Object onStart(Object... args) {
                        return null;
                    }

                    @Override
                    public Object onFinish(Object... args) {
                        return null;
                    }
                });

                goHome();
            }
        });

        return view;
    }

    private void goHome() {
        // TODO: Open HomeActivity
    }
}
