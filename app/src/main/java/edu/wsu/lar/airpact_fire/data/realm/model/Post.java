// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Realm object describing all data associated with a post:
 * images, user creating the post, algorithm used in the
 * post, date of post, etc.
 */
public class Post extends RealmObject {

    @PrimaryKey
    public int postId;
    public String secretKey;
    public String serverId;
    public int metric;
    public int mode;
    public User user;
    public Date date;
    public int algorithm;
    public float estimatedVisualRange;
    public float computedVisualRange;
    public String description;
    public String location;
}
