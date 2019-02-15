// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.service;

import android.annotation.SuppressLint;
import android.os.Binder;
import android.os.Process;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import edu.wsu.lar.airpact_fire.app.Constant;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Service for accurately listening for GPS and allowing activities to request
 * GPS information at any moment.
 */
public class GpsService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private GpsLocationChangedCallback mLocationChangedSubscriber;

    private Realm mRealm;
    public GpsService() {
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public GpsService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GpsService.this;
        }
    }

    @Override
    public void onCreate() {

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location newLocation = locationResult.getLastLocation();
                mLocation = newLocation;
                notifyLocationChanged();
            };
        };

        // Start listening for location updates
        startLocationUpdates();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onRebind(intent);

        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

    }

    @Override
    public void onDestroy() { }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback,
                null);
    }

    public interface GpsLocationChangedCallback {
        void change(double[] gps);
    }

    public void subscribeLocationChanged(GpsLocationChangedCallback subscriber) {
        mLocationChangedSubscriber = subscriber;
    }

    /**
     * Notify subscriber that GPS location has been updated.
     *
     * Also save this GPS location in the database for quick lookup.
     */
    public void notifyLocationChanged() {
        if (mLocationChangedSubscriber == null) return;

        // TODO: Realm.init();
        // TODO: Find this.
        // I think the GPS service is being started by an activity that's being discarded and giving
        // us errors now. We should have a more reliable way of attaching to the service from the
        // activity and giving it a reliable realm instance.
        mRealm = Realm.getDefaultInstance();

        // Update last seen GPS for most recent user.
        double[] newGps = getGps();
        mRealm.executeTransaction(realm -> {
            // Get sessions ordered by date
            RealmResults<Session> orderedSessions = mRealm.where(Session.class).findAllSorted(
                    "startDate", Sort.DESCENDING);
            if (orderedSessions.isEmpty()) { return; }
            Session lastSession = orderedSessions.first();
            User user = lastSession.user;
            user.lastLatitude = newGps[0];
            user.lastLongitude = newGps[1];
            // Log out.
            String logMessage = String.format("GPS updated: '%s' -> (%f, %f)",
                    user.username, user.lastLatitude, user.lastLongitude);
            Log.d("BackgroundGpsService", logMessage);
        });

        mLocationChangedSubscriber.change(newGps);
    }

    public double[] getGps() {
        if (mLocation == null) return Constant.DEFAULT_GPS_LOCATION;
        return new double[] { mLocation.getLatitude(), mLocation.getLongitude() };
    }
}
