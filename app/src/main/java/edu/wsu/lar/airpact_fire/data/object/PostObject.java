// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

import android.graphics.Bitmap;
import android.net.Uri;

import org.json.simple.JSONObject;

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

    UserObject getUser();

    Date getDate();
    void setDate(Date value);

    long getImageServerId();
    void setImageServerId(long value);

    /** @see edu.wsu.lar.airpact_fire.Reference.DistanceMetric */
    int getMetric();
    void setMetric(int value);

    /** @see edu.wsu.lar.airpact_fire.Reference.Algorithm */
    int getAlgorithm();
    void setAlgorithm(int value);

    String getSecretKey();
    void setSecretKey(String value);

    /** @see edu.wsu.lar.airpact_fire.Reference.PostMode */
    int getMode();
    void setMode(int value);

    Bitmap getImageBitmap();
    Uri createImage();
    void setImage(Bitmap value);

    double[] getGPS();
    void setGPS(double[] values);

    float[] getDistances();
    void setDistances(float[] values);

    float getEstimatedVisualRange();
    void setEstimatedVisualRange(float value);

    /** Computed visual range returned from server */
    float getComputedVisualRange();
    void setComputedVisualRange(float value);

    float[][] getTargetsCoordinates();
    void setTargetsCoorindates(float[][] values);

    String getDescription();
    void setDescription(String value);

    String getLocation();
    void setLocation(String value);

    JSONObject toJSON();
}
