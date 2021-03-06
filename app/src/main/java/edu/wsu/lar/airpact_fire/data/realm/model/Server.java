// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import java.util.Date;
import io.realm.RealmObject;

/**
 * Realm object representing all data about the server we know.
 */
public class Server extends RealmObject {

    // TODO: Move a lot of the server data in here
    public Date lastConnected;

    // TODO: Have a change in any Post update these below fields (like an SQL TRIGGER)
    public int numSubmitted;
    public int numQueued;
}
