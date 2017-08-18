// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.server.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import lar.wsu.edu.airpact_fire.R;

public class SubmissionServerCallback implements ServerCallback {

    private Activity mActivity;
    private PostObject mPostObject;
    private ProgressDialog mProgress;

    public SubmissionServerCallback(Activity activity, PostObject postObject) {
        mActivity = activity;
        mPostObject = postObject;
    }

    @Override
    public Object onStart() {

        // Show progress display
        mProgress = new ProgressDialog(mActivity);
        mProgress.setTitle("Submitting Image...");
        mProgress.setMessage("Please wait while your image is being " +
                "sent");
        mProgress.show();

        return null;
    }

    @Override
    public Object onFinish(Object... args) {

        boolean didSubmit = (boolean) args[0];
        double serverOutput = (double) args[1];
        int serverImageId = (int) args[2];

        if (didSubmit) {
            mPostObject.setMode(DataManager.PostMode.SUBMITTED.getId());
            mPostObject.setComputedVisualRange((float) serverOutput);
            mPostObject.setServerId("" + serverImageId);
            Toast.makeText(mActivity, R.string.submission_success, Toast.LENGTH_LONG).show();
        } else {
            mPostObject.setMode(DataManager.PostMode.QUEUED.getId());
            Toast.makeText(mActivity, R.string.submission_failed, Toast.LENGTH_SHORT).show();
        }

        mProgress.dismiss();

        return null;
    }

}
