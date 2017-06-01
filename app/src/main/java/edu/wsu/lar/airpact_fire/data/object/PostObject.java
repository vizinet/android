// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

/**
 * Image post interface for UI to use and change.
 *
 * <p>Changes made to implementors of this interface will be reflected
 * in the database.</p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public interface PostObject {

    String getImage();
    void setImage(String value);

    float[] getGPS();
    void setGPS(float[] values);
}
