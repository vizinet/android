package edu.wsu.lar.airpact_fire.data.model;

import java.util.List;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    public String name;
    public String password;
    public List<Session> sessions;  // App usage sessions by this user
    public List<Post> posts;        // Queued and submitted posts
}
