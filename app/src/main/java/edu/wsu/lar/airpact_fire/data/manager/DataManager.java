package edu.wsu.lar.airpact_fire.data.manager;

import edu.wsu.lar.airpact_fire.data.model.User;

// Interface allowing app to deal with arbitrary database technologies (e.g. Realm and MySQL)
public interface DataManager {
    void init(); // Called on app's first run
    void startSession();
    boolean isAuthenticatedUser(String username, String password);
    void createAndAddUser(String username, String password);
    User getRecentUser();
}
