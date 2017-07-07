// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.app;

import edu.wsu.lar.airpact_fire.app.manager.AIRPACTFireAppManager;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;

/**
 * Single point of reference regarding constants for whole app
 *
 * @author  Luke Weber
 * @since   0.9
 */
public final class Reference {

    // TODO: How about we define these as Android Values and Enums?

    // AIRPACT-Fire URL fields
    public static final String SERVER_BASE_URL = "http://airpacfire.eecs.wsu.edu";
    public static final String SERVER_UPLOAD_URL = SERVER_BASE_URL + "/file_upload/upload";
    public static final String SERVER_AUTHENTICATION_URL = SERVER_BASE_URL + "/user/appauth";
    public static final String SERVER_REGISTER_URL = SERVER_BASE_URL + "/user/register";
    public static final String SERVER_INFORMATION_URL = SERVER_BASE_URL + "/";

    // Determines how much keyboard tends to occupy on screen
    public static final double KEYPAD_OCCUPATION_RATIO = 0.15;

    // App-wide enums
    public enum Algorithm { TWO_IN_ONE, ONE_IN_ONE };
    public enum DistanceMetric { MILES, KILOMETERS };
    public enum PostMode { DRAFTED, QUEUED, SUBMITTED };

    public static AppManager getAppManager() {
        return new AIRPACTFireAppManager();
    }
}
