package edu.wsu.lar.airpact_fire.debug.manager;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import edu.wsu.lar.airpact_fire.manager.AppManager;

// Simple class to control debugging output
public class DebugManager {

    private static String sDebugTag = "DebugManager";

    private boolean mDebugMode;

    public DebugManager (boolean debugMode) {
        mDebugMode = debugMode;
    }

    public void printLog(String message) {
        if (!mDebugMode) return;
        Log.wtf(sDebugTag, message);
    }

    public void printToast(Activity activity, String message) {
        if (!mDebugMode) return;
        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
