// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.manager;

import edu.wsu.lar.airpact_fire.data.object.AppObject;

/**
 * This interface consists of variables and methods for handling this app's
 * persistently stored information under various database management platforms,
 * e.g. Realm and MySQL.
 *
 * <p>This data manager <b>must</b> always run on the UI thread and will be constructed/
 * deconstructed with the corresponding life-cycle of each succeeding activity.
 * Any implementer of this class must be initialized for its methods to be called.
 * </p>
 *
 * <p>Any DataManager deals with the lending of database objects (e.g. AppObject)
 * to the UI, as well as conforming to standard (and custom) activity lifecycle
 * methods, such as onLogin, onAppFirstStart, and onActivityEnd.</p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public interface DataManager {

    // Data standards
    String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    double[] GPS_DEFAULT_LOCATION = {46.73267, -117.163454}; // Pullman, WA

    // Create user and start app usage session
    void startSession(String username, String password);
    void endSession();

    // Give app object - key to all of database access for UI
    AppObject getApp();

    // Activity lifecycle methods
    void onAppFirstRun(Object... args);
    void onAppStart(Object... args);
    void onAppEnd(Object... args);
    void onLogin(Object... args);
    void onLogout(Object... args);
    void onActivityStart(Object... args);
    void onActivityEnd(Object... args);
}
