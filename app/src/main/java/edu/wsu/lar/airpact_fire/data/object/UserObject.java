// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

import java.util.Date;

/**
 * User object interface for UI to deal with.
 *
 * <p>Changes made to implementors of this interface will be reflected
 * in the database.</p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public interface UserObject {

    // Readonly: user credentials
    String getUsername();
    String getPassword();

    void setGPS(double[] values);
    double[] getGPS();

    boolean getHasDraftPost();
    void setHasDraftPost(boolean value);

    PostObject[] getPosts();
    PostObject getLastPost();
    PostObject createPost();

    int getDistanceMetric();
    void setDistanceMetric(int value);

    Date getFirstLoginDate();
    void getFirstLoginDate(Date value);
}
