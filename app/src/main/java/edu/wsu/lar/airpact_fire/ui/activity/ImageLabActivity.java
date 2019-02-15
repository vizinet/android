// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import edu.wsu.lar.airpact_fire.app.Constant;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.AlgorithmSelectFragment;
import edu.wsu.lar.airpact_fire.ui.target.manager.UiTargetManager;
import edu.wsu.lar.airpact_fire.R;

/**
 * Activity to manage the collection of data for each described {@link Algorithm}.
 *
 * @see Algorithm
 * @see UiTargetManager
 */
public class ImageLabActivity extends AppCompatActivity {

    private AppManager mAppManager;
    private ServerManager mServerManager;

    private UserInterfaceObject mUserInterfaceObject;
    private PostInterfaceObject mPostInterfaceObject;
    private Algorithm mAlgorithm;
    private UiTargetManager mUiTargetManager;

    private ProgressBar mProgressBar;
    private ActionBar mActionBar;

    private int[] mPadding;

    /**
     * Create a post and open Fragment for user to select algorithm type.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_lab);

        // Set action bar.
//        setSupportActionBar(findViewById(R.id.my_toolbar));
//        mActionBar = getSupportActionBar();
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        GradientDrawable gd = new GradientDrawable(
//                GradientDrawable.Orientation.TOP_BOTTOM,
//                new int[] { Color.WHITE, Color.TRANSPARENT });
//        mActionBar.setBackgroundDrawable(gd);

        mProgressBar = findViewById(R.id.progress_bar);
        setProgressBarVisible(false);

        mAppManager = Constant.getAppManager();
        mAppManager.onActivityStart(this);
        mAppManager.rebindGpsService();
        mServerManager = mAppManager.getServerManager();

        // Create new post (for this image).
        mUserInterfaceObject = mAppManager.getDataManager().getApp().getLastUser();
        mPostInterfaceObject = mUserInterfaceObject.createPost();

        // Target manager creation.
        mUiTargetManager = new UiTargetManager(this);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout.
        if (findViewById(R.id.image_lab_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout.
            AlgorithmSelectFragment firstFragment = new AlgorithmSelectFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments.
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout.
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                mPostInterfaceObject.delete();
                mAppManager.goHome();
                return true;

            case R.id.action_help:
                // TODO: Open help fragment.
                return true;

            case R.id.action_home:
                mAppManager.goHome();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        mUiTargetManager.hideAll();
        super.onBackPressed();
    }

    /* Methods for fragments to control UI */

    /**
     * Allows child Fragments to clear the parent padding.
     */
    public void clearPadding() {
        View containerView = findViewById(R.id.image_lab_container);
        mPadding = new int[]{
                containerView.getPaddingLeft(),
                containerView.getPaddingTop(),
                containerView.getPaddingRight(),
                containerView.getPaddingBottom()
        };
        containerView.setPadding(0, 0, 0, 0);
    }

    /**
     * Allows child Fragments to restore the parent padding.
     */
    public void restorePadding() {
        View containerView = findViewById(R.id.image_lab_container);
        containerView.setPadding(mPadding[0], mPadding[1], mPadding[2], mPadding[3]);
    }

    public void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

    /* Methods for fragments to access activity fields. */

    public AppManager getAppManager() {
        return mAppManager;
    }

    public ServerManager getServerManager() { return mServerManager; }

    public UserInterfaceObject getUserObject() {
        return mUserInterfaceObject;
    }

    public PostInterfaceObject getPostObject() {
        return mPostInterfaceObject;
    }

    public Algorithm getAlgorithm() {
        return mAlgorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        mAlgorithm = algorithm;
        mPostInterfaceObject.setAlgorithm(mAlgorithm.getId());
    }

    public UiTargetManager getUITargetManager() {
        return mUiTargetManager;
    }

    public void setProgressBarVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.INVISIBLE;
        mProgressBar.setVisibility(visibility);
    }
}
