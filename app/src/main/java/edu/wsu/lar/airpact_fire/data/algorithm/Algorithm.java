// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Interface for all algorithms known to AIRPACT-Fire to implement with their
 * corresponding user-driven procedures.
 *
 * <p>Note that each algorithm requires a unique set of steps from the user to
 * collect them. This is the reason each {@link Algorithm} is mapped to a
 * fragment, which initiates a set of steps.
 * </p>
 *
 * @author Luke Weber
 * @since 0.9
 */
public abstract class Algorithm {

    /** @return algorithm identifier as integer. */
    abstract int getId();

    /** @return name of algorithm. */
    abstract String getName();

    /** @return abbreviation of name. */
    abstract String getAbbreviation();

    /** @return fragment with predefined collection behavior for this algorithm. */
    abstract Object getFragment();
}
