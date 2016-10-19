package lar.wsu.edu.airpact_fire;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

// Simple class to control debugging output
public class DebugManager {
    private static boolean IS_DEBUGGING = true;

    public static void printLog(String message) {
        if (!IS_DEBUGGING) return;
        Log.println(Log.DEBUG, "DebugManager", message);
    }

    public static void printToast(Activity activity, String message) {
        if (!IS_DEBUGGING) return;
        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
