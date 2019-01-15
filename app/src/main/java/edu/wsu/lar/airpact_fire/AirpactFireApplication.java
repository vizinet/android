// Copyright © 2016-2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.
// TODO: Add copyright and license to all source files.

package edu.wsu.lar.airpact_fire;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.Toast;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.data.StringFormat;

import edu.wsu.lar.airpact_fire.app.Constant;

// TODO: Integrate this into our app manager.
// TODO: We may want to use HTTP in the future and have our backend distribute the message to devs.

@AcraMailSender(mailTo = Constant.DEVELOPER_EMAIL)
@AcraToast(resText = R.string.acra_email_notifcation, length = Toast.LENGTH_LONG)
@AcraCore(buildConfigClass = BuildConfig.class)
public class AirpactFireApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA.
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
        builder.setBuildConfigClass(BuildConfig.class).setReportFormat(StringFormat.JSON);
        ACRA.init(this, builder);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
