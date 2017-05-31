package edu.wsu.lar.airpact_fire.data.manager;

import edu.wsu.lar.airpact_fire.data.realm.model.App;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;

/**
 * This interface consists of variables and methods for handling this app's
 * persistently stored information under various database management platforms,
 * e.g. Realm and MySQL.
 *
 * <p>This data manager is always run on the UI thread and will be constructed/
 * deconstructed with the corresponding life-cycle of each succeeding activity.
 * Any implementer of this class must be initialized for its methods to be called.
 * </p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public interface DataManager {

    // Data standards
    String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    double[] GPS_DEFAULT_LOC = {46.73267, -117.163454}; // Pullman, WA

    // Fields for UI to request from a data manager
    enum DataField {
        // App meta
        APP_LAST_USER,
        APP_REMEMBER_PASSWORD,
        // User meta
        USER_USERNAME,
        USER_PASSWORD,
        USER_FIRST_LOGIN_DATE,
        USER_LAST_LOGIN_DATE,
        USER_DISTANCE_METRIC
        // Post meta

    }

    // Function wrapper
    interface Command {
        Object run(Object... args);
    }

    // Utility functions
    boolean isUser(String username, String password);
    User getUser(String username, String password);
    Session getLastSession();
    App getApp();

    // Activity lifecycle methods
    void onAppFirstRun(Object... args);
    void onAppStart(Object... args);
    void onAppEnd(Object... args);
    void onLogin(Object... args);
    void onLogout(Object... args);
    void onActivityStart(Object... args);
    void onActivityEnd(Object... args);

    // Get/set any arbitrary field in DB
    Object fieldAccess(Object... args);
}
