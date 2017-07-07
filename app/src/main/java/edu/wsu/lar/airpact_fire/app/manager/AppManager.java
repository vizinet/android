package edu.wsu.lar.airpact_fire.app.manager;

import java.util.List;

import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
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
public interface AppManager {

    boolean isDebugging();

    DataManager getDataManager(Object... args);
    ServerManager getServerManager(Object... args);
    DebugManager getDebugManager(Object... args);
    List<String> getAlgorithms();

    void onAppStart(Object... args);
    void onAppEnd(Object... args);
    void onActivityStart(Object... args);
    void onActivityEnd(Object... args);
    void onLogin(Object... args);
    void onLogout(Object... args);
    void onAuthenticate(String username, String password, ServerManager.ServerCallback callback);
    void onSubmit(PostObject postObject, ServerManager.ServerCallback serverCallback);
}
