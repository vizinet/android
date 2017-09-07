// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmObject;

/**
 * Realm model for storing simple distance measures
 * for particular image captures.
 *
 * @see Image
 */
public class Distance extends RealmObject {

    public double distance;
}
