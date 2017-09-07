// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.interface_object;

import java.util.Date;

/**
 * User usage session object interface for UI to handle.
 */
public interface SessionInterfaceObject extends InterfaceObject {

    UserInterfaceObject getUser();

    Date getStartDate();
    Date getEndDate();
    void setEndDate(Date value);

    Object getRaw();
}
