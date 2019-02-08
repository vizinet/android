// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.interface_object;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

/**
 * User object interface for UI to handle and edit.
 */
public interface UserInterfaceObject extends InterfaceObject {

    // Readonly: user credentials
    String getUsername();
    String getPassword();

    boolean getRememberAlgorithmChoice();
    void setRememberAlgorithmChoice(boolean value);

    boolean getHasDraftPost();
    void setHasDraftPost(boolean value);

    List<SessionInterfaceObject> getSessions();

    List<PostInterfaceObject> getPosts();
    List<PostInterfaceObject> getPosts(int start, int end);
    PostInterfaceObject getPost(int id);
    PostInterfaceObject getLastPost();
    PostInterfaceObject createPost();

    LatLng getRecentLatLng();

    int getDistanceMetric();
    void setDistanceMetric(int value);

    String getFirstLoginDate();
    void getFirstLoginDate(Date value);

    Object getRaw();
}
