// Copyright © 2016-2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.
// TODO: Add copyright and license to all source files.

package edu.wsu.lar.airpact_fire;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.widget.Toast;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

import edu.wsu.lar.airpact_fire.app.Constant;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.service.GpsService;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.realm.manager.RealmDataManager;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.server.manager.HTTPServerManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;

import static org.acra.ReportField.*;

// TODO: Integrate this into our app manager.
// TODO: We may want to use HTTP in the future and have our backend distribute the message to devs.

// TODO: Implement the notification system.

@AcraHttpSender(uri = Constant.SERVER_CRASH_REPORT_URL, httpMethod = HttpSender.Method.POST)
@AcraToast(resText = R.string.acra_email_notifcation, length = Toast.LENGTH_LONG)
@AcraCore(reportContent = { CUSTOM_DATA, APP_VERSION_CODE, ANDROID_VERSION, BUILD, BRAND,
        PHONE_MODEL, STACK_TRACE, USER_APP_START_DATE, USER_CRASH_DATE },
        buildConfigClass = BuildConfig.class, reportFormat = StringFormat.JSON)
public class AIRPACTFireApplication extends Application {

    private DataManager mDataManager;
    private ServerManager mServerManager;
    private DebugManager mDebugManager;

    private Activity mActivity;
    private GpsService mGpsService;
    private AppManager.GpsAvailableCallback mGpsAvailableCallback;

    private boolean mIsActivityVisible = true;

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        setupManagers();

        // Check first-run.
        SharedPreferences mPreferences = context.getSharedPreferences(
                context.getPackageName(),
                MODE_PRIVATE);
        if (mPreferences.getBoolean("firstrun", true)) {
            mDebugManager.printLog("App's first run");
            mDataManager.onAppFirstRun(context);
            mPreferences.edit().putBoolean("firstrun", false).commit();
        } else {
            mDebugManager.printLog("Not app's first run");
        }

        mDataManager.onAppStart(context);
        mServerManager.onAppStart(context);
    }

    /**
     * Construct managers; could have already been started by `onApplicationStart`.
     */
    private void setupManagers() {
        mDebugManager = mDebugManager == null
                ? new DebugManager(true)
                : mDebugManager;
        mDataManager = mDataManager == null
                ? new RealmDataManager(mDebugManager, mActivity)
                : mDataManager;
        mServerManager = mServerManager == null
                ? new HTTPServerManager(mDebugManager, mActivity)
                : mServerManager;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // The following line triggers the initialization of ACRA.
        ACRA.init(this);
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
