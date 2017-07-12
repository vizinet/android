// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

public interface ImageObject {

    void setRememberPassword(boolean value);

    UserObject getLastUser();
    UserObject getUser(String username, String password);

    SessionObject getLastSession();
    SessionObject getSession(); // Current session

    Object getRaw();
}
