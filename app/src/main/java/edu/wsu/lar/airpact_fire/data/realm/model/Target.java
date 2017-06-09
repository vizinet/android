// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Target extends RealmObject {

    public RealmList<Coordinate> coordinates; // [x, y]
    public int color;
}
