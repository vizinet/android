package edu.wsu.lar.airpact_fire.data.manager;

// Interface allowing app to deal with arbitrary database technologies (e.g. Realm and MySQL)
public interface DataManager {
    boolean isAuthenticatedUser(String username, String password);
    void createAndAddUser(String username, String password);
    void startSession();
    void init(); // Called on app's first run
}
