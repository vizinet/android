// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.server.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;
import edu.wsu.lar.airpact_fire.ui.activity.SignInActivity;
import lar.wsu.edu.airpact_fire.R;

/**
 * Implementors of this class will handle the pre- and post-authentication
 * steps.
 */
public class AuthenticationServerCallback implements ServerCallback {

    private Activity mActivity;
    private ProgressDialog mProgress;

    public AuthenticationServerCallback(Activity activity) {
        mActivity = activity;
    }

    @Override
    public Object onStart() {

        // Show loading display
        // NOTE: Found that using context rather than activity causes some annoyances
        mProgress = new ProgressDialog(mActivity);
        mProgress.setTitle("Signing In...");
        mProgress.setMessage("Please wait while we authenticate");
        mProgress.show();

        return null;
    }

    @Override
    public Object onFinish(Object... args) {

        boolean isUser = (boolean) args[0];
        String username = (String) args[1];
        String password = (String) args[2];

        // Dismiss loading dialog
        mProgress.dismiss();

        if (isUser) {
            Toast.makeText(mActivity, R.string.authentication_success,
                    Toast.LENGTH_LONG).show();
            ((SignInActivity) mActivity).login(username, password);
        } else {
            Toast.makeText(mActivity, R.string.authentication_failed,
                    Toast.LENGTH_SHORT).show();
        }

        return null;
    }
}
