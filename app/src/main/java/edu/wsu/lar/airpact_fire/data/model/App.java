package edu.wsu.lar.airpact_fire.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// Stores global app info and preferences
public class App extends RealmObject {
    public User lastUser; // TODO: Trigger-update
    public boolean rememberPassword;
}
