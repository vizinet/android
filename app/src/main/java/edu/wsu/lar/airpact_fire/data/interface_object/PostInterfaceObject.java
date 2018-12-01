// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.interface_object;

import android.graphics.Bitmap;
import org.json.simple.JSONObject;
import java.util.Date;
import java.util.List;
import edu.wsu.lar.airpact_fire.app.Reference;

/**
 * Image post interface for UI to use and change.
 */
public interface PostInterfaceObject extends InterfaceObject {

    UserInterfaceObject getUser();

    Date getDate();
    void setDate(Date value);

    String getServerId();
    void setServerId(String value);

    /** @see Reference.DistanceMetric */
    int getMetric();
    void setMetric(int value);

    /** @see Reference.Algorithm */
    int getAlgorithm();
    void setAlgorithm(int value);

    String getSecretKey();
    void setSecretKey(String value);

    /** @see Reference.PostMode */
    int getMode();
    void setMode(int value);

    ImageInterfaceObject createImageObject();
    List<ImageInterfaceObject> getImageObjects();
    Bitmap getThumbnail();
    Bitmap getThumbnail(int width);

    /**
     * Remove raw image from memory to save space on client device.
     */
    void wipeRawImages();

    float getEstimatedVisualRange();
    void setEstimatedVisualRange(float value);

    /** Computed visual range returned from server */
    float getComputedVisualRange();
    void setComputedVisualRange(float value);

    String getDescription();
    void setDescription(String value);

    String getLocation();
    void setLocation(String value);

    void delete();

    JSONObject toJSON();

    int getId();
    Object getRaw();
}
