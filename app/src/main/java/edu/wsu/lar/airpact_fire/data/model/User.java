package edu.wsu.lar.airpact_fire.data.model;

import java.util.List;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    public String name;
    public String password;
    public List<Session> sessions; // Helps give first and last login times
    public List<Post> posts;
}
