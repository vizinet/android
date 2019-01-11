// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm;

import edu.wsu.lar.airpact_fire.server.contract.ServerContract;

/**
 * Interface for all algorithms known to AIRPACT-Fire to implement with their
 * corresponding user-driven procedures.
 *
 * <p>Note that each algorithm requires a unique set of steps from the user to
 * collect them. This is the reason each {@link Algorithm} is mapped to a
 * fragment, which initiates a set of steps.
 * </p>
 */
public interface Algorithm {

    /** @return algorithm identifier as integer. */
    int getId();

    /** @return name of algorithm. */
    String getName();

    /** @return abbreviation of name. */
    String getAbbreviation();

    /** @return description of algorithm and how it works. */
    String getDescription();

    /** @return standard usage procedure for laymen to understand. */
    String getProcedure();

    /** @return thumbnail resource identifier for visualization of algorithm. */
    int getThumbnailResource();

    /** @return fragment with predefined collection behavior for this algorithm. */
    Object getStartFragment();

    /** @return server submission contract for this particular algorithm. */
    ServerContract getServerContract();
}
