// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm;

/** @see Algorithm */
public class TwoInOneAlgorithm implements Algorithm {

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getName() {
        return "two-in-one";
    }

    @Override
    public String getAbbreviation() {
        return "TIO";
    }

    @Override
    public Object getFragment() {
        return null;
    }
}
