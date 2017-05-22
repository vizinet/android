package edu.wsu.lar.airpact_fire.data.manager;

import edu.wsu.lar.airpact_fire.data.model.App;
import edu.wsu.lar.airpact_fire.data.model.Session;
import edu.wsu.lar.airpact_fire.data.model.User;

// Interface allowing app to deal with arbitrary database technologies (e.g. Realm and MySQL)
// NOTE: Any class can implement DataManager for to replace the current database manager
public interface DataManager {

    void onAppFirstRun(); // Called on app's first run
    boolean isUser(String username, String password);

    User getUser(String username, String password);
    App getApp();

    void rememberPassword(boolean b);
    boolean rememberPassword();

    String getLastUser(); // Return user in the current app session
    User getCurrentUser();
    Session getCurrentSession();

    void onAppStart();
    void onAppEnd();

    void onLogin(String username, String password);
    void onLogout();

    // Use reflection to get field value
    String getUserField(String fieldName);
    Object setUserField(String fieldName, Object fieldValue);
}
