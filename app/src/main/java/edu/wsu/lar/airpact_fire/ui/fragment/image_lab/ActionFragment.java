// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.server.callback.SubmissionServerCallback;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import edu.wsu.lar.airpact_fire.ui.activity.HomeActivity;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

public class ActionFragment extends Fragment {

    private static final String sActionBarTitle = "Post Action";

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
                mPostObject.delete();
                goHome();
            }
        });

        mQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostObject.setMode(DataManager.PostMode.QUEUED.getId());
                goHome();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SubmissionServerCallback submissionServerCallback = new SubmissionServerCallback(
                        getActivity(), mPostObject) {
                    @Override
                    public Object onFinish(Object... args) {
                        super.onFinish(args);
                        goHome();
                        return null;
                    }
                };

                mAppManager.getServerManager().onSubmit(
                        getActivity(), mPostObject, submissionServerCallback);
            }
        });

        return view;
    }

    private void goHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }
}
