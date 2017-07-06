// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmObject;

// Stores global app info and preferences
public class App extends RealmObject {

    public User lastUser;            // TODO: Trigger-update
    public boolean rememberUser; // Default: false

    @Override
    public String toString() {
        String userString = (lastUser == null) ? "[No last user]" : lastUser.toString();
        return userString + " - " + rememberUser;
    }
}
