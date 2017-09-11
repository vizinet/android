// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.interface_object;

/**
 * Generic app object with fields only the UI will need, nothing more.
 */
public interface AppInterfaceObject extends InterfaceObject {

    /* Field accessors and modifiers */

    boolean getRememberUser();
    void setRememberUser(boolean value);

    UserInterfaceObject getLastUser();
    UserInterfaceObject getUser(String username, String password);

    SessionInterfaceObject getLastSession();
    SessionInterfaceObject getSession(); // Current session

    Object getRaw();
}
