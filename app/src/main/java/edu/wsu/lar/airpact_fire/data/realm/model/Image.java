// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Image extends RealmObject {

    @PrimaryKey
    public int postId;
    @PrimaryKey
    public int imageId;

    public String imageLocation;
    public RealmList<Target> targets;
    public Coordinate gpsCoordinate;
}
