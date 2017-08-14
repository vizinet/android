package edu.wsu.lar.airpact_fire.app.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.server.callback.ServerCallback;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;

/**
 * This interface declares the methods with which an app activity will attempt to
 * exercise custom app functionality, namely data storage and retrieval and server
 * communication.
 *
 * <p>Implementors to this interface will be called according to the activity's life
 * cycle, in addition to some other custom events (e.g. login/logout)</p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public abstract class AppManager {

    public abstract boolean isDebugging();

    public static double[] getGps(Activity activity) {

        // Attempt to get real geolocation
        LocationManager locationManager = (LocationManager) activity.getSystemService(
                Context.LOCATION_SERVICE);
        boolean canAccessFineLocation = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean canAccessCourseLocation = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        if (canAccessFineLocation || canAccessCourseLocation) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return new double[] { loc.getLatitude(), loc.getLongitude() };
        }

        return Reference.DEFAULT_GPS_LOCATION;
    }

    public abstract DataManager getDataManager(Object... args);
    public abstract ServerManager getServerManager(Object... args);
    public abstract DebugManager getDebugManager(Object... args);

    public abstract void goHome();

    public abstract void onAppStart(Object... args);
    public abstract void onAppEnd(Object... args);
    public abstract void onActivityStart(Object... args);
    public abstract void onActivityEnd(Object... args);
    public abstract void onLogin(Object... args);
    public abstract void onLogout(Object... args);
    public abstract void onPostCreated(Object... args);
    public abstract void onPostFinished(Object... args);
    public abstract void onAuthenticate(String username, String password, ServerCallback callback);
    public abstract void onSubmit(PostObject postObject, ServerCallback serverCallback);
}
