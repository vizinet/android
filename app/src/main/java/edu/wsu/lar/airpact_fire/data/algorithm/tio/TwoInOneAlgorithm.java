// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm.tio;

import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.server.contract.ServerContract;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.tio.TwoInOneFragment;

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
    public String getDescription() {
        return "One image capture with two targets placed.";
    }

    @Override
    public String getProcedure() {
        return "Keep your place as you take a picture of a Point of Interest. From there, place " +
                "one target on the Point of Interest, and the other on the sky or clouds in the " +
                "background.";
    }

    @Override
    public Object getStartFragment() {
        return new TwoInOneFragment();
    }

    @Override
    public ServerContract getServerContract() {
        return new TwoInOneServerContract();
    }
}
