package edu.wsu.lar.airpact_fire.data.manager;

import edu.wsu.lar.airpact_fire.data.model.App;
import edu.wsu.lar.airpact_fire.data.model.Session;
import edu.wsu.lar.airpact_fire.data.model.User;
import io.realm.RealmFieldType;

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

    // Utility function
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

    // Get/set any arbitrary app field
    Object getAppField(String fieldName);
    void setAppField(String fieldName, Object fieldValue);

    // Get/set any arbitrary field for current user
    Object getUserField(String fieldName);
    void setUserField(String fieldName, String fieldValue);
}
