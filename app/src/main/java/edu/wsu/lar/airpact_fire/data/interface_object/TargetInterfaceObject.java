// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.interface_object;

/**
 * UI object representing a target placed upon an image (which in
 * turn is represented by an {@link ImageInterfaceObject}.
 */
public interface TargetInterfaceObject extends InterfaceObject {

    float getDistance();
    void setDistance(float value);

    float[] getCoordinates();
    void setCoordinates(float[] values);

    void delete();

    Object getRaw();
}
