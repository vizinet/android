package edu.wsu.lar.airpact_fire.data.model;

import java.util.Date;
import java.util.List;
import io.realm.RealmObject;

public class Server extends RealmObject {

    // TODO: Move a lot of the server data in here
    public Date lastConnected;

    // TODO: Have a change in any Post update these below fields (like an SQL TRIGGER)
    public int numSubmitted;
    public int numQueued;
}
