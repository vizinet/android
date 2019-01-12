// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm.tio;

import edu.wsu.lar.airpact_fire.R;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.server.contract.ServerContract;
import edu.wsu.lar.airpact_fire.ui.fragment.image_lab.tio.TwoInOneFragment;

/**
 * Implementation of the two-in-one algorithm, a procedure defined
 * as taking a single image of a Point of Interest and estimating
 * the distance and Visual Range in that single view.
 */
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
        return "One image captured with two targets placed from the user.";
    }

    @Override
    public String getProcedure() {
        return "Keep your place as you take a picture of a two similar landmarks" +
                " (e.g., mountains, hills, buildings). From there, place one target on a nearer " +
                "landmark and the other on a further one. Estimate your distance from each target.";
    }

    @Override
    public int getThumbnailResource() {
        return R.drawable.tio_thumbnail;
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
