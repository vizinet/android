package edu.wsu.lar.airpact_fire.server.manager;

import android.content.Context;

/**
 * Communication interface between app and some authenticating and submission-accepting entity
 *
 * <p>This manager should never touch a bit of user interface or database code</p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public interface ServerManager {

    /**
     * Communication interface between app and some authenticating and submission-accepting entity
     *
     * <p>This manager should never touch a bit of user interface or database code</p>
     *
     * @author  Luke Weber
     * @since   0.9
     */
    interface ServerCallback {
        void onStart(Object... args);
        void onFinish(Object... args);
    }

    /**
     * Async method for initializing authentication of user credentials with server
     *
     * @author  Luke Weber
     * @param   username    server alias of user
     * @param   password    password for given username
     * @param   callback    interface of callback functions
     * @see     ServerCallback
     * @since   0.9
     */
    void authenticate(Context context, String username, String password, ServerCallback callback);

    void onAppStart(Object... args);
    void onAppEnd(Object... args);
    void onActivityStart(Object... args);
    void onActivityEnd(Object... args);
    void onLogin(Object... args);
    void onLogout(Object... args);
}
