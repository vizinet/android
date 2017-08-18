// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.server.callback;

/**
 * Communication interface between app and some authenticating and submission-accepting entity
 *
 * <p>This manager should never touch a bit of user interface or database code</p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public interface ServerCallback {

    Object onStart();
    Object onFinish(Object... args);
}
