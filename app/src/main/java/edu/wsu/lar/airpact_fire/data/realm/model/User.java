// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static edu.wsu.lar.airpact_fire.app.Reference.*;

public class User extends RealmObject {

    @PrimaryKey
    public String username;
    public String password;
    public RealmList<Session> sessions;     // App usage sessions by this user
    public RealmList<Post> posts;           // Queued and submitted posts
    /** @see DISTANCE_METRICS */
    public int distanceMetric;
    public boolean rememberAlgorithmChoice;

    @Override
    public String toString() {
        return username;
    }
}
