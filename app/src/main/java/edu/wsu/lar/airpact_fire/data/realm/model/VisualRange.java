// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmObject;

/**
 * Simple Realm description of a visual range entity for
 * some particular {@link Image}.
 */
public class VisualRange extends RealmObject {
    public float value;
}
