package edu.wsu.lar.airpact_fire.debug.manager;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import edu.wsu.lar.airpact_fire.manager.AppManager;

// Simple class to control debugging output
public class DebugManager {
    private static String sDebugTag = "DebugManager";

    public static void printLog(String message) {
        if (!AppManager.IS_DEBUGGING) return;
        Log.wtf(sDebugTag, message);
    }

    public static void printToast(Activity activity, String message) {
        if (!AppManager.IS_DEBUGGING) return;
        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
