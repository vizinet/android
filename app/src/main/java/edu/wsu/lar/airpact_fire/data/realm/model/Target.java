// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Definition of a target upon an {@link Image} in
 * the Realm database.
 */
public class Target extends RealmObject {

    @PrimaryKey
    public int targetId;
    public int imageId;
    public int postId;
    public Coordinate coordinate;
    public float distance;
    public int color;
}
