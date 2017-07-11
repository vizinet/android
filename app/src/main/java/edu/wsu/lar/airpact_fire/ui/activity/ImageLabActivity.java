// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.ui.fragment.AlgorithmSelectFragment;
import lar.wsu.edu.airpact_fire.R;

public class ImageLabActivity extends AppCompatActivity {

    private AppManager mAppManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_lab);

        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.image_lab_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            AlgorithmSelectFragment firstFragment = new AlgorithmSelectFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.image_lab_container, firstFragment).commit();
        }
    }

    // For fragments to access app manager
    public AppManager getAppManager() {
        return mAppManager;
    }
}
