package edu.wsu.lar.airpact_fire.debug.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

    public void printToast(Context context, String message) {
        if (!mDebugMode) return;
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
