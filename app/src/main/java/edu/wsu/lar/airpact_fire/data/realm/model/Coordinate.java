// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmObject;

/**
 * Realm data model for storing xy-coordinates for GPS,
 * image pixel positioning, etc.
 */
public class Coordinate extends RealmObject {
    public double x;
    public double y;
}
