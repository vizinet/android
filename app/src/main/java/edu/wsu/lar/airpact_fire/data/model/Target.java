package edu.wsu.lar.airpact_fire.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Target extends RealmObject {

    public RealmList<Coordinate> coordinates; // [x, y]
    public int color;
}
