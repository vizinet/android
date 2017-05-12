package edu.wsu.lar.airpact_fire.data.model;

import io.realm.RealmObject;

public class Target extends RealmObject {

    public int[] coordinates; // [x, y]
    public int color;
}
