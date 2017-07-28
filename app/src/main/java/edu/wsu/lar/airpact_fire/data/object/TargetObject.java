// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

public interface TargetObject {

    float getDistance();
    void setDistance(float value);

    float[] getCoordinates();
    void setCoordinates(float[] values);

    void delete();

    Object getRaw();
}
