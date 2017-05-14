package edu.wsu.lar.airpact_fire.data.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Session extends RealmObject {

    @PrimaryKey
    public String id;
    public Date startTime, endTime;
    // TODO: Last entered fields in app

    public double getDuration() {
        // TODO
        return 0.0;
    }
}