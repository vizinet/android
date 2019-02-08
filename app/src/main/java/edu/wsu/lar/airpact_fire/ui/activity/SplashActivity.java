// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 *  Landing (splash) screen for the app, which displays the logo rather than a blank white screen,
 *  which is especially prominent in Android app cold starts.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Maybe do some decision making here to reduce the distance between the splash screen
        // and the desired screen for the user.
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}