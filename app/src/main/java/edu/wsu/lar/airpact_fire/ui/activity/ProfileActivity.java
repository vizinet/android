// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import lar.wsu.edu.airpact_fire.R;

public class ProfileActivity extends AppCompatActivity {//implements OnMapReadyCallback {

    private static final String sActionBarTitle = "Profile";

    private AppManager mAppManager;
    private UserObject mUserObject;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(sActionBarTitle);

        mAppManager = Reference.getAppManager();
        mAppManager.onActivityStart(this);

        mUserObject = mAppManager.getDataManager().getApp().getLastUser();




//
//            MapFragment mapFragment = (MapFragment) getFragmentManager()
//                    .findFragmentById(R.id.map);
//            mapFragment.getMapAsync(this);
        }
//
//        @Override
//        public void onMapReady(GoogleMap map) {
//            LatLng sydney = new LatLng(-33.867, 151.206);
//
//            map.setMyLocationEnabled(true);
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
//
//            map.addMarker(new MarkerOptions()
//                    .title("Sydney")
//                    .snippet("The most populous city in Australia.")
//                    .position(sydney));
//        }
    }
