package edu.wsu.lar.airpact_fire.data.model;

import io.realm.RealmObject;

// Stores global app info and preferences
public class App extends RealmObject {

    public User lastUser;            // TODO: Trigger-update
    public boolean rememberPassword; // Default: false

    @Override
    public String toString() {
        String userString = (lastUser == null) ? "[No last user]" : lastUser.toString();
        return userString + " - " + rememberPassword;
    }
}
