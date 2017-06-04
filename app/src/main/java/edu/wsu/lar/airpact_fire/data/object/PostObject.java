// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

import java.util.Date;

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

    Date getDate();

    int getMode();
    void setMode(int value);

    String getImage();
    void setImage(String value);

    float[] getGPS();
    void setGPS(float[] values);

    float getVisualRange();
    void setVisualRange(float value);

    boolean getIsSubmitted();
    void setIsSubmitted(boolean value);

    float[][] getTargets();
    void setTargets(float[][] values);

    String getDescription();
    void setDescription(String value);

    String getTag();
    void setTag(String value);

    double[] getGPSCoordinates();
    void setGPSCoordinates(double[] values);
}
