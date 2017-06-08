// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import java.util.Date;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// Stores start and end times of usages session, including the last entered preferences
public class Session extends RealmObject {

    @PrimaryKey
    public int sessionId;
    public User user;
    public int mode;
    public Date startDate, endDate;
    public int selectedAlgorithm;
    public RealmList<Target> targets;
    public String location;
    public String description;
    public RealmList<Distance> estimatedDistances; // TODO: Make list

    public double getDuration() {
        // TODO
        return 0; // i.e., endTime - startTime;
    }
}