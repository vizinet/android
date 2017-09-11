// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.image_lab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.server.callback.SubmissionServerCallback;
import edu.wsu.lar.airpact_fire.ui.activity.HomeActivity;
import edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity;
import lar.wsu.edu.airpact_fire.R;

/**
 * Fragment for taking action on a completed post.
 *
 * <p>User can submit, queue, or delete a post from this page.</p>
 */
public class ActionFragment extends Fragment {

    private static final String sActionBarTitle = "Post Action";

    private AppManager mAppManager;
    private PostInterfaceObject mPostInterfaceObject;

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
        mPostInterfaceObject = ((ImageLabActivity) getActivity()).getPostObject();

        View view = inflater.inflate(R.layout.fragment_action, container, false);
        mDiscardButton = (Button) view.findViewById(R.id.discard_button);
        mQueueButton = (Button) view.findViewById(R.id.queue_button);
        mSubmitButton = (Button) view.findViewById(R.id.submit_button);

        mDiscardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostInterfaceObject.delete();
                goHome();
            }
        });

        mQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostInterfaceObject.setMode(DataManager.PostMode.QUEUED.getId());
                Toast.makeText(getActivity(), "Post has been queued. You can view it in the " +
                        "gallery.", Toast.LENGTH_SHORT).show();
                goHome();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SubmissionServerCallback submissionServerCallback = new SubmissionServerCallback(
                        getActivity(), mPostInterfaceObject) {
                    @Override
                    public Object onFinish(Object... args) {
                        super.onFinish(args);

                        // Grab data params from post submission
                        boolean didSubmit = (boolean) args[0];
                        double serverOutput = (double) args[1];
                        int serverImageId = (int) args[2];

                        if (didSubmit)

                        // Go home & return
                        goHome();
                        return null;
                    }
                };

                mAppManager.getServerManager().onSubmit(
                        getActivity(), mPostInterfaceObject, submissionServerCallback);
            }
        });

        return view;
    }

    private void goHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }
}
