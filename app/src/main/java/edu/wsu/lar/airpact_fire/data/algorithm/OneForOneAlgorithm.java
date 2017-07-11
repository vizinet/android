// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm;

import edu.wsu.lar.airpact_fire.ui.fragment.algorithm_procedure.OneForOneStartFragment;

/** @see Algorithm */
public class OneForOneAlgorithm implements Algorithm {

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public String getName() {
        return "one-for-one";
    }

    @Override
    public String getAbbreviation() {
        return "OFO";
    }

    @Override
    public Object getFragment() {
        return new OneForOneStartFragment();
    }
}
