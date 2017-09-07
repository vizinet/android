// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.server.manager;

import android.content.Context;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.server.callback.ServerCallback;

/**
 * Communication interface between app and some authenticating and
 * submission-accepting entity.
 *
 * <p>This manager should never touch a bit of user interface or
 * database code.</p>
 *
 * @see edu.wsu.lar.airpact_fire.app.manager.AppManager
 */
public interface ServerManager {

    /**
     * Async method for initializing authentication of user credentials with server
     *
     * @param   username    server alias of user
     * @param   password    password for given username
     * @param   callback    interface of callback functions
     * @see     ServerCallback
     */
    void onAuthenticate(Context context, String username, String password, ServerCallback callback);

    void onAppStart(Object... args);
    void onAppEnd(Object... args);
    void onActivityStart(Object... args);
    void onActivityEnd(Object... args);
    void onLogin(Object... args);
    void onLogout(Object... args);
    void onSubmit(Context context,
                  PostInterfaceObject postInterfaceObject,
                  ServerCallback serverCallback);
}
