// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.realm.RealmDataManager;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.server.manager.HTTPServerManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;

import static android.content.Context.MODE_PRIVATE;

public class AIRPACTFireAppManager implements AppManager {

    private static final boolean sIsDebugging = true;
    private static final int sRequestExternalStorage = 1;
    private static final String[] sPermissionsStorage = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private DataManager mDataManager;
    private ServerManager mServerManager;
    private DebugManager mDebugManager;

    private Activity mActivity;

    @Override
    public boolean isDebugging() {
        return sIsDebugging;
    }

    @Override
    public DataManager getDataManager(Object... args) {
        return mDataManager;
    }

    @Override
    public ServerManager getServerManager(Object... args) {
        return mServerManager;
    }

    @Override
    public DebugManager getDebugManager(Object...args) {
        return mDebugManager;
    }

    @Override
    public void onAppStart(Object... args) {

        // Do app's first-run stuff
        Context context = (Context) args[0];

        // Check first-run
        SharedPreferences mPreferences = context.getSharedPreferences(context.getPackageName(),
                MODE_PRIVATE);
        if (mPreferences.getBoolean("firstrun", true)) {
            mDebugManager.printLog("App's first run");
            mDataManager.onAppFirstRun();
            mPreferences.edit().putBoolean("firstrun", false).commit();
        } else {
            mDebugManager.printLog("Not app's first run");
        }

        // Ensure we can store images
        // TODO: More convenient place for this to ask
        verifyStoragePermissions(mActivity);

        mDataManager.onAppStart();
        mServerManager.onAppStart();
    }

    @Override
    public void onAppEnd(Object... args) {
        mDataManager.onAppEnd();
        mServerManager.onAppEnd();
    }

    @Override
    public void onActivityStart(Object... args) {

        mActivity = (Activity) args[0];
        final Context context = mActivity.getApplicationContext();

        // Construct managers
        mDebugManager = new DebugManager(isDebugging());
        mDataManager = new RealmDataManager(mDebugManager, mActivity);
        mServerManager = new HTTPServerManager(mDebugManager, mActivity);

        mDebugManager.printLog("Started activity = " + context.toString());

        // Notify that activity has begun
        mDataManager.onActivityStart(context);
        mServerManager.onActivityStart();

        // Catch global exceptions
        // NOTE: Has never been thrown, to my observation
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                mDebugManager.printToast(context, "[Global exception caught]");
            }
        });
    }

    @Override
    public void onActivityEnd(Object... args) {
        mDataManager.onActivityEnd();
        mServerManager.onActivityEnd();
    }

    @Override
    public void onLogin(Object... args) {
        mDataManager.onLogin(args);
        mServerManager.onLogin(args);
    }

    @Override
    public void onLogout(Object... args) {
        mDataManager.onLogout();
        mServerManager.onLogout();
    }

    @Override
    public void onAuthenticate(String username, String password,
                               ServerManager.ServerCallback callback) {
        mServerManager.onAuthenticate(mActivity.getApplicationContext(), username, password,
                callback);
    }

    @Override
    public void onSubmit(PostObject postObject, ServerManager.ServerCallback serverCallback) {
        // Attempt submission to server, update database with results
        mServerManager.onSubmit(mActivity.getApplicationContext(), postObject, serverCallback);
    }

    /**
     * Checks if the app has permission to write to device storage.
     *
     * <p>If the app does not has permission then the user will be
     * prompted to grant permissions.</p>
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, sPermissionsStorage, sRequestExternalStorage);
        }
        mDebugManager.printLog("Verified storage permissions");
    }
}
