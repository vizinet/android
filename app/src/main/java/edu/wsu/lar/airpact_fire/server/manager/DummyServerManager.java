package edu.wsu.lar.airpact_fire.server.manager;

import android.content.Context;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.server.callback.ServerCallback;

/**
 * Dummy server management class for testing & debugging.
 *
 * @see edu.wsu.lar.airpact_fire.app.manager.AppManager
 */
public class DummyServerManager implements ServerManager {

    private static boolean sBlindAuthentication = true;

    @Override
    public void onAppStart(Object... args) {

    }

    @Override
    public void onAppEnd(Object... args) {

    }

    @Override
    public void onActivityStart(Object... args) {

    }

    @Override
    public void onActivityEnd(Object... args) {

    }

    @Override
    public void onLogin(Object... args) {

    }

    @Override
    public void onLogout(Object... args) {

    }

    @Override
    public void onSubmit(Context context, PostInterfaceObject postInterfaceObject, ServerCallback serverCallback) {

    }

    @Override
    public void onAuthenticate(Context context, String username, String password, ServerCallback callback) {
        callback.onFinish(sBlindAuthentication, username, password);
    }
}
