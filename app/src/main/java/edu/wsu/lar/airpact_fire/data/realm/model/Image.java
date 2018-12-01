// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Realm model portraying a single image capture for
 * some {@link Post}.
 */
public class Image extends RealmObject {
    @PrimaryKey
    public int imageId;
    public int postId;
    public String rawImageLocation;
    public String compressedImageLocation;
    public Coordinate gpsCoordinate;
}
