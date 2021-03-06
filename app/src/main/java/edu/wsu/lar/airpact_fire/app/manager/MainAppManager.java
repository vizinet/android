// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.app.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import org.acra.ACRA;

import edu.wsu.lar.airpact_fire.service.GpsService;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.realm.manager.RealmDataManager;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.server.callback.ServerCallback;
import edu.wsu.lar.airpact_fire.server.manager.HTTPServerManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import edu.wsu.lar.airpact_fire.ui.activity.HomeActivity;

// TODO: Transfer a lot of this functionality to `Application` class.

/**
 * First implementation of the {@link AppManager} interface.
 */
public class MainAppManager extends AppManager {

    private static final boolean sIsDebugging = true;

    private DataManager mDataManager;
    private ServerManager mServerManager;
    private DebugManager mDebugManager;

    private Activity mActivity;
    private GpsService mGpsService;
    private GpsAvailableCallback mGpsAvailableCallback;

    private boolean mIsActivityVisible = true;

    private class GpsServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GpsService.LocalBinder binder = (GpsService.LocalBinder) service;
            mGpsService = binder.getService();
            mDebugManager.printLog("GPS service connected.");
            Toast.makeText(mActivity, "GPS service connected.", Toast.LENGTH_LONG);
            notifyGpsAvailable();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDebugManager.printLog("GPS service disconnected.");
            Toast.makeText(mActivity, "GPS service disconnected.", Toast.LENGTH_LONG);
            mGpsService = null;
        }
    }

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
    public double[] getGps() {
        if (mGpsService == null) {
            // TODO
            return null;
        }
        return mGpsService.getGps();
    }

    @Override
    public void startGpsService() {

        // Do not proceed without subscribers
        if (mGpsAvailableCallback == null) return;

        // Cannot start a service while app is in background
        if (mIsActivityVisible == false) return;

        // Start and bind GPS service
        Intent serviceIntent = new Intent(mActivity, GpsService.class);
        mActivity.startService(serviceIntent);
        mActivity.bindService(serviceIntent, new GpsServiceConnection(), Context.BIND_IMPORTANT);
    }

    @Override
    public void rebindGpsService() {

        if (isServiceRunning(GpsService.class)) {
            Intent serviceIntent = new Intent(mActivity, GpsService.class);
            mActivity.bindService(serviceIntent, new GpsServiceConnection(),
                    Context.BIND_IMPORTANT);
        } else {
            startGpsService();
        }
    }

    @Override
    public void endGpsService() {
        // End GPS service.
        Intent serviceIntent = new Intent(mActivity, GpsService.class);
        mActivity.stopService(serviceIntent);
    }

    /**
     * Spawn GPS services the moment somebody subscribes.
     *
     * @param callback
     */
    @Override
    public void subscribeGpsAvailable(GpsAvailableCallback callback) {
        mGpsAvailableCallback = callback;
        startGpsService();
    }

    @Override
    public void notifyGpsAvailable() {
        if (mGpsAvailableCallback == null) return;
        // Check in 1 second intervals for subscribers.
        mGpsAvailableCallback.change();
    }

    @Override
    public void subscribeGpsLocationChanges(GpsService.GpsLocationChangedCallback callback) {
        mGpsService.subscribeLocationChanged(callback);
    }

    @Override
    public boolean isActivityVisible() {
        return mIsActivityVisible;
    }

    @Override
    public void goHome() {
        Intent intent = new Intent(mActivity.getApplicationContext(), HomeActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public void onApplicationStart(Object... args) {

//        Context context = (Context) args[0];
//
//        setupManagers();
//
//        // Check first-run.
//        SharedPreferences mPreferences = context.getSharedPreferences(
//                context.getPackageName(),
//                MODE_PRIVATE);
//        if (mPreferences.getBoolean("firstrun", true)) {
//            mDebugManager.printLog("App's first run");
//            mDataManager.onAppFirstRun(context);
//            mPreferences.edit().putBoolean("firstrun", false).commit();
//        } else {
//            mDebugManager.printLog("Not app's first run");
//        }
//
//        mDataManager.onAppStart(context);
//        mServerManager.onAppStart(context);
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

        setupManagers();

        mDebugManager.printLog("Started activity = " + context.toString());

        // Notify that activity has begun
        mDataManager.onActivityStart(context);
        mServerManager.onActivityStart();
    }

    @Override
    public void onActivityPause(Object... args) {
        mIsActivityVisible = false;
    }

    @Override
    public void onActivityResume(Object... args) {
        mIsActivityVisible = true;
        // TODO: Need to start or restart GPS service once app is brought into focus
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
        startGpsService();
        // Make error reporter know last logged in user.
        ACRA.getErrorReporter().putCustomData("username", (String)args[0]);
    }

    @Override
    public void onLogout(Object... args) {
        mDataManager.onLogout();
        mServerManager.onLogout();
        endGpsService();
    }

    @Override
    public void onAuthenticate(String username, String password, ServerCallback callback) {
        mServerManager.onAuthenticate(
                mActivity.getApplicationContext(), username, password, callback);
    }

    @Override
    public void onSubmit(PostInterfaceObject postInterfaceObject, ServerCallback serverCallback) {
        // Attempt submission to server, update database with results
        mServerManager.onSubmit(mActivity.getApplicationContext(), postInterfaceObject, serverCallback);
    }

    /**
     * Construct managers; could have already been started by `onApplicationStart`.
     */
    private void setupManagers() {
        mDebugManager = mDebugManager == null
                ? new DebugManager(isDebugging())
                : mDebugManager;
        mDataManager = mDataManager == null
                ? new RealmDataManager(mDebugManager, mActivity)
                : mDataManager;
        mServerManager = mServerManager == null
                ? new HTTPServerManager(mDebugManager, mActivity)
                : mServerManager;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager)
                mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
