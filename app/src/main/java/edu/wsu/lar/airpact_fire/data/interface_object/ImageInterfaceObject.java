// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.interface_object;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;

/**
 * Image representation database object for UI to use.
 */
public interface ImageInterfaceObject extends InterfaceObject {

    double[] getGps();
    void setGps(double[] values);

    TargetInterfaceObject createTargetObject();
    List<TargetInterfaceObject> getTargetObjects();

    Bitmap getBitmap();
    Bitmap getThumbnail();
    File createImageFile();

    void wipeRawImage();

    /**
     * Create image file for "raw" and "thumbnail" image for camera Activity to populate with
     * pixels.
     * @param storageDir directory under which images are created
     * @return "Raw" image file
     */
    File createImageFile(File storageDir);

    File getImageFile();

    /**
     * Allows Activity to edit actual image pixel values. Must update both "raw" and "thumbnail"
     * representations of image.
     * @param value new image pixels
     */
    void setImage(Bitmap raw, Bitmap compressed);

    void delete();

    Object getRaw();
}
