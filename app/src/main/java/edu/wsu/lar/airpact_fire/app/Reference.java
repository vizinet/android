// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.app;

import edu.wsu.lar.airpact_fire.app.manager.MainAppManager;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;

/**
 * Single point of reference regarding constant values for whole app.
 */
public final class Reference {

    // TODO: How about we define these as Android Values and Enums?

    // AIRPACT-Fire URL fields
    public static final String SERVER_BASE_URL = "https://airpactfire.eecs.wsu.edu";
    public static final String SERVER_UPLOAD_URL = SERVER_BASE_URL + "/file_upload/upload";
    public static final String SERVER_AUTHENTICATION_URL = SERVER_BASE_URL + "/user/appauth";
    public static final String SERVER_REGISTER_URL = SERVER_BASE_URL + "/user/register";
    public static final String SERVER_INFORMATION_URL = SERVER_BASE_URL + "/";
    public static final String SERVER_IMAGE_BASE_URL = SERVER_BASE_URL + "/picture/view/";
    public static final String SERVER_PROFILE_BASE_URL = SERVER_BASE_URL + "/user/profile/";

    // AIRPACT-Fire agreed date-time format between server and app
    public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

    // Determines how much keyboard tends to occupy on screen
    public static final double KEYPAD_OCCUPATION_RATIO = 0.15;

    // Defaults (always the first element in array)
    public static final double[] DEFAULT_GPS_LOCATION = { 46.73267, -117.163454 }; // Pullman, WA

    /**
     * Array of distance metrics agreed upon by server and app.
     *
     * <p>We refer to these metrics on a 1-based index.</p>
     */
    public static final String[] DISTANCE_METRICS = {
            "kilometers",               // 1
            "miles"                     // 2
    };

    public static AppManager getAppManager() {
        return new MainAppManager();
    }
}
