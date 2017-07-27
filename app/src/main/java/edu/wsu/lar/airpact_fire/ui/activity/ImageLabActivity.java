// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.ui.fragment.algorithm.AlgorithmSelectFragment;
import edu.wsu.lar.airpact_fire.ui.target.manager.UITargetManager;
import lar.wsu.edu.airpact_fire.R;

public class ImageLabActivity extends AppCompatActivity {

    private AppManager mAppManager;
    private UserObject mUserObject;
    private PostObject mPostObject;
    private Algorithm mAlgorithm;
    private UITargetManager mUITargetManager;

    private ActionBar mActionBar;

    private int[] mPadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_lab);

        // Set action bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {
                        Color.WHITE,
                        Color.TRANSPARENT
                });
        mActionBar.setBackgroundDrawable(gd);

        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);

        // Create new post (for this image)
        mUserObject = mAppManager.getDataManager().getApp().getLastUser();
        mPostObject = mUserObject.createPost();

        // Target manager creation
        mUITargetManager = new UITargetManager(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_beta, menu);
        return true;
    }

    /* Methods for fragments to control UI */

    public void clearPadding() {
        View containerView = findViewById(R.id.image_lab_container);
        mPadding = new int[] {
                containerView.getPaddingLeft(),
                containerView.getPaddingTop(),
                containerView.getPaddingRight(),
                containerView.getPaddingBottom()
        };
        containerView.setPadding(0, 0, 0, 0);
    }

    public void restorePadding() {
        View containerView = findViewById(R.id.image_lab_container);
        containerView.setPadding(mPadding[0], mPadding[1], mPadding[2], mPadding[3]);
    }

    public void setActionBarTitle(String title) {
       mActionBar.setTitle(title);
    }

    /* Methods for fragments to access activity fields */

    public AppManager getAppManager() {
        return mAppManager;
    }

    public UserObject getUserObject() {
        return mUserObject;
    }

    public PostObject getPostObject() {
        return mPostObject;
    }

    public Algorithm getAlgorithm() {
        return mAlgorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        mAlgorithm = algorithm;
        mPostObject.setAlgorithm(mAlgorithm.getId());
    }

    public UITargetManager getUITargetManager() {
        return mUITargetManager;
    }
}
