package edu.wsu.lar.airpact_fire.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    public String username;
    public String password;
    public RealmList<Session> sessions;  // App usage sessions by this user
    public RealmList<Post> posts;        // Queued and submitted posts
}
