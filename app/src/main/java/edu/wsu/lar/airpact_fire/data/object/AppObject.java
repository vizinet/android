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

    // Create user and start app usage session
    void startSession(String username, String password);
    void endSession();

    /* Field accessors and modifiers */

    boolean getRememberPassword();
    void setRememberPassword(boolean value);

    UserObject getLastUser();
    UserObject getUser(String username);

    SessionObject getLastSession();
    SessionObject getSession(); // Current session
}
