// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.object.RealmPostObject;
import edu.wsu.lar.airpact_fire.ui.fragment.gallery.GalleryPostDetailsFragment;
import edu.wsu.lar.airpact_fire.ui.fragment.gallery.MainGalleryFragment;
import lar.wsu.edu.airpact_fire.R;

public class GalleryActivity extends AppCompatActivity {

    private AppManager mAppManager;
    private UserObject mUserObject;

    private ActionBar mActionBar;

    private int[] mPadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);

        mUserObject = mAppManager.getDataManager().getApp().getLastUser();

        if (findViewById(R.id.gallery_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            Integer postId = getIntent().getIntExtra("TARGETED_POST_DETAILS", -1);
            if (postId > 0) {
                // Specific post details requested from outside activity
                // TODO: Get specific realm object via id
                PostObject postObject = new RealmPostObject();
                Fragment postDetailsFragment = new GalleryPostDetailsFragment();
                ((GalleryPostDetailsFragment) postDetailsFragment).setArguments(postObject);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gallery_container, postDetailsFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Default gallery
                MainGalleryFragment mainFragment = new MainGalleryFragment();
                mainFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.gallery_container, mainFragment).commit();
            }
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

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_help:
                // TODO: Open help dialog
                return true;

            case R.id.action_home:
                mAppManager.goHome();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
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
}
