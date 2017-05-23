package edu.wsu.lar.airpact_fire;

import edu.wsu.lar.airpact_fire.manager.AIRPACTFireAppManager;
import edu.wsu.lar.airpact_fire.manager.AppManager;

/**
 * Single point of reference regarding constants for whole app
 *
 * @author  Luke Weber
 * @since   0.9
 */
public final class Reference {

    // AIRPACT-Fire URL fields
    public static final String SERVER_BASE_URL = "http://airpacfire.eecs.wsu.edu";
    public static final String SERVER_UPLOAD_URL = SERVER_BASE_URL + "/file_upload/upload";
    public static final String SERVER_AUTHENTICATION_URL = SERVER_BASE_URL + "/user/appauth";
    public static final String SERVER_REGISTER_URL = SERVER_BASE_URL + "/user/register";
    public static final String SERVER_INFORMATION_URL = SERVER_BASE_URL + "/";

    public static AppManager getAppManager() {
        return new AIRPACTFireAppManager();
    }
}
