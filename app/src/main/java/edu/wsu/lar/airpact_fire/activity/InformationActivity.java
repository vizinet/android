package edu.wsu.lar.airpact_fire.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import lar.wsu.edu.airpact_fire.R;
import edu.wsu.lar.airpact_fire.tool.Util;

//import com.google.android.gms.maps.*;
//import com.google.android.gms.maps.model.*;
//import android.app.Activity;
//import android.os.Bundle;

public class InformationActivity extends AppCompatActivity {//implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Util.setupSecondaryNavBar(this, HomeActivity.class, "INFORMATION");
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
