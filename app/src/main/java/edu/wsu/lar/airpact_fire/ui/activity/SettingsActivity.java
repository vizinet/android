package edu.wsu.lar.airpact_fire.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import edu.wsu.lar.airpact_fire.R;
import edu.wsu.lar.airpact_fire.util.Util;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Util.setupSecondaryNavBar(this, HomeActivity.class, "SETTINGS");
    }
}
