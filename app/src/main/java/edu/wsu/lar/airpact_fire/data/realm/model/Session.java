package edu.wsu.lar.airpact_fire.data.realm.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

// Stores start and end times of usages session, including the last entered preferences
public class Session extends RealmObject {

    public User user;
    public Date startTime, endTime;
    public String selectedAlgorithm;
    public RealmList<Target> targets;
    public String location;
    public String description;
    public RealmList<Distance> estimatedDistances; // TODO: Make list

    public double getDuration() {
        // TODO
        return 0; //endTime - startTime;
    }
}