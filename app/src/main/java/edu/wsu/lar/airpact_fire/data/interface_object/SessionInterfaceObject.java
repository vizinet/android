// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.interface_object;

/**
 * User usage session object interface for UI to handle.
 */
public interface SessionInterfaceObject extends InterfaceObject {

    UserInterfaceObject getUser();

    String getStartDate();
    String getEndDate();
    void setEndDate(String value);

    Object getRaw();
}
