package edu.wsu.lar.airpact_fire.data.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class User extends RealmObject {

    @PrimaryKey
    public String username;
    public String password;
    public Date firstLoginDate, lastLoginDate; // TODO: Trigger-update
    public RealmList<Session> sessions;  // App usage sessions by this user
    public RealmList<Post> posts;        // Queued and submitted posts
    public String distanceMetric;        // Kilometers or miles

    @Override
    public String toString() {
        return username;
    }
}
