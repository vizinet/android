package edu.wsu.lar.airpact_fire.app.manager;

import edu.wsu.lar.airpact_fire.app.service.GpsService;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
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
 */
public abstract class AppManager {

    public interface GpsAvailableCallback {
        void change();
    }

    public abstract boolean isDebugging();
    public abstract void goHome();

    /* Manager methods */

    public abstract DataManager getDataManager(Object... args);
    public abstract ServerManager getServerManager(Object... args);
    public abstract DebugManager getDebugManager(Object... args);

    /* GPS service-related methods */

    public abstract double[] getGps();
    public abstract void startGpsService();
    public abstract void rebindGpsService();
    public abstract void endGpsService();
    public abstract void subscribeGpsAvailable(GpsAvailableCallback callback);
    public abstract void notifyGpsAvailable();
    public abstract void subscribeGpsLocationChanges(
            GpsService.GpsLocationChangedCallback callback);

    /* misc. */

    public abstract boolean isActivityVisible();

    /* Activity lifecycle methods */

    public abstract void onAppStart(Object... args);
    public abstract void onAppEnd(Object... args);
    public abstract void onActivityStart(Object... args);
    public abstract void onActivityPause(Object... args);
    public abstract void onActivityResume(Object... args);
    public abstract void onActivityEnd(Object... args);
    public abstract void onLogin(Object... args);
    public abstract void onLogout(Object... args);
    public abstract void onAuthenticate(String username, String password, ServerCallback callback);
    public abstract void onSubmit(PostInterfaceObject postInterfaceObject, ServerCallback serverCallback);
}
