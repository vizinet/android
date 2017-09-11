// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.server.contract;

import org.json.simple.JSONObject;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;

/**
 * Defines methods for implementors to turn {@link PostInterfaceObject}
 * objects into a server-readable format (i.e. JSON).
 */
public interface ServerContract {

    String DATE_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    JSONObject toJSON(PostInterfaceObject postInterfaceObject);
}
