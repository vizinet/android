// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

/**
 * Generic app object with fields only the UI will need, nothing more.
 *
 * <p>Changes made to implementors of this interface will be reflected
 * in the database.</p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public interface AppObject {

    /* Field accessors and modifiers */

    boolean getRememberUser();
    void setRememberUser(boolean value);

    UserObject getLastUser();
    UserObject getUser(String username, String password);

    SessionObject getLastSession();
    SessionObject getSession(); // Current session

    Object getRaw();
}
