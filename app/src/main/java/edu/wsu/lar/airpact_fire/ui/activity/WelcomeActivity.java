// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import edu.wsu.lar.airpact_fire.R;
import edu.wsu.lar.airpact_fire.ui.fragment.welcome.WelcomeIntroductionFragment;
import edu.wsu.lar.airpact_fire.ui.fragment.welcome.WelcomePermissionsFragment;
import edu.wsu.lar.airpact_fire.util.Util;

import static edu.wsu.lar.airpact_fire.AIRPACTFireApplication.requestedPermissions;

public class WelcomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private RadioGroup mPageRadioGroup;

    private static final int sAllPermissionsCode = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the `ViewPager` with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        int padPx = Util.dpToPx(WelcomeActivity.this, 30);
        mViewPager.setPageMargin(padPx);
        mViewPager.getClipToOutline();
        mViewPager.setPadding(padPx, padPx, padPx, padPx);

        mPageRadioGroup = findViewById(R.id.page_radio_group);
        mPageRadioGroup.setOnCheckedChangeListener(this);
        mPageRadioGroup.setPadding(padPx, 0, padPx, padPx);
        for (Fragment fragment : mSectionsPagerAdapter.fragments) {
            RadioButton radioButton = new RadioButton(WelcomeActivity.this);
            mPageRadioGroup.addView(radioButton);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        int radioButtonId = mPageRadioGroup.getChildAt(position).getId();
        mPageRadioGroup.check(radioButtonId);
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
        int index = radioGroup.indexOfChild(checkedRadioButton);
        mViewPager.setCurrentItem(index, true);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public Fragment[] fragments = new Fragment[] {
                WelcomeIntroductionFragment.newInstance(),
                WelcomePermissionsFragment.newInstance(sAllPermissionsCode, requestedPermissions),
        };

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() { return fragments.length; }
    }

    /**
     * Respond to user enabling/disabling app permissions.
     *
     * @param requestCode code of request
     * @param permissions permissions requested
     * @param grantResults decisions of requested permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode != sAllPermissionsCode
                || grantResults.length != requestedPermissions.length) {
            return;
        }
        // Check denied permissions.
        int deniedCount = 0;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                deniedCount += 1;
            }
        }
        // Update permission fragment.
        for (Fragment fragment : mSectionsPagerAdapter.fragments) {
            if (fragment.getClass() == WelcomePermissionsFragment.class) {
                ((WelcomePermissionsFragment) fragment).updatePermissionStatus(deniedCount);
            }
        }
    }

    /**
     * Determine how many permissions the app still needs.
     *
     * @return number of denied permissions
     */
    public int getDeniedPermissions() {
        PackageManager pm = getPackageManager();
        int deniedCount = 0;
        for (String permission : requestedPermissions)  {
            int permissionCode = pm.checkPermission(permission, getPackageName());
            if (permissionCode == PackageManager.PERMISSION_DENIED) deniedCount++;
        }
        return deniedCount;
    }

    /**
     * Proceed to home app activity.
     */
    public void proceed() {
        Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
