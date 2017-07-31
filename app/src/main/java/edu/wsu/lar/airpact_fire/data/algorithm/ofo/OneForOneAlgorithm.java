// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm.ofo;

import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.server.contract.ServerContract;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.ofo.OneForOneAlphaFragment;

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
    public String getDescription() {
        return "Two image captures, one target_background placed on each.";
    }

    @Override
    public String getProcedure() {
        return "Stand still. What we will do is take two pictures at different distances of the " +
                "same Point of Interest (e.g., mountain, hills, tree). You will be asked to " +
                "capture and describe one image at a time.";
    }

    @Override
    public Object getStartFragment() {
        return new OneForOneAlphaFragment();
    }

    @Override
    public ServerContract getServerContract() {
        return new OneForOneServerContract();
    }
}
