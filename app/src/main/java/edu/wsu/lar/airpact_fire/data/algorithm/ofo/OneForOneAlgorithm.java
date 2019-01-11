// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm.ofo;

import edu.wsu.lar.airpact_fire.R;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.server.contract.ServerContract;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.ofo.OneForOneAlphaFragment;

/**
 * Definition and specification for the one-for-one algorithm.
 *
 * <p>The procedure for this algorithm is to take two separate photos,
 * each of which is at a varying distance away from a central Point of
 * Interest (e.g. mountain).</p>
 */
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
        return "Two image captures, one target placed on each.";
    }

    @Override
    public String getProcedure() {
        return "Stand still. Take two pictures at different distances of the " +
                "same Point of Interest (e.g., mountain, hills, tree).";
    }

    @Override
    public int getThumbnailResource() {
        return R.drawable.ofo_thumbnail;
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
