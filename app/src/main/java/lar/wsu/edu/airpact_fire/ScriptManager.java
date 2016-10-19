package lar.wsu.edu.airpact_fire;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

// Does following:
//      - Downloads image analysis algorithms from server
//      - Run downloaded algorithms
//      - Check for/install QPython (lets us run Python scripts!)
// Algorithms are written as Python scripts
public class ScriptManager {

    // File names will be of form [algorithm name]_script.py
    public static final String SCRIPT_BASE_FILE_NAME = "_script.py";
    public static final String QPYTHON_PACKAGE_NAME = "org.qpython.qpy"; //"com.hipipal.qpyplus"; // changed from "org.qpython.qpy3";
    // TODO remove local apk
    public static final String QPYTHON_APK_FILE_NAME = "qpython3_release.apk";
    public static final int SCRIPT_EXEC_PY = 40001;

    // Return array of choices of scripts
    public static ArrayList<String> getScriptChoices() {
        ArrayList<String> scripts = new ArrayList<>();
        scripts = AppDataManager.getScriptNames();
        return scripts;
    }

    // Update scripts from web server
    public static void update(Activity activity) {
        // Check if QPython is installed before even downloading scripts
        if (!isQPythonInstalled(activity)) return;

        // TODO: remove past algorithm files (as to not consume excess space)
        DebugManager.printToast(activity, "Downloading Python scripts...");

        // Check if any updates
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.execute(activity);
    }

    // Check if QPython app is installed.
    // Let's setup dialog to get user to install it.
    private static boolean isQPythonInstalled(Activity activity) {
        if (true) return true;

        if (Util.isPackageInstalled(QPYTHON_PACKAGE_NAME, activity.getPackageManager()))
            return true;
        // Attempt install of QPython, and return true if user allowed install
        return installQPython(activity);
    }

    // Bring up prompt for user to install .apk file we have
    private static boolean installQPython(Activity activity) {

        DebugManager.printToast(activity, "Must install QPython...");

        // TODO: Use onActivityResult(...)
        // Open marketplace page for QPython
        Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("market://details?id=" + QPYTHON_PACKAGE_NAME));
        activity.startActivity(goToMarket);

        // Once activity done, see if user really installed QPython
        if (Util.isPackageInstalled(QPYTHON_PACKAGE_NAME, activity.getPackageManager())) {
            DebugManager.printToast(activity, "QPython is now installed!");
            return true;
        }

        DebugManager.printToast(activity, "QPython is still not installed :<");
        return false;

    }

    // Run script given its name
    // NOTE: Outside world must have some sort of knowledge about algorithms available and be
    // able to call those specifically
    public static void run(Activity activity, String name) {
        // We don't run unless we got this
        if (!isQPythonInstalled(activity)) return;
        // Check script existence
        if (!AppDataManager.getScriptNames().contains(name)) return;

        // Import file and run it
        try {
            // Read Python file into string
            String scriptFileName = name + SCRIPT_BASE_FILE_NAME;
            FileInputStream fis = activity.getApplicationContext().openFileInput(scriptFileName);
            StringBuilder builder = new StringBuilder();
            int ch;
            while ((ch = fis.read()) != -1) {
                builder.append((char) ch);
            }
            fis.close();
            String scriptString = builder.toString();

            // TODO: remove
            scriptString = "import androidhelper\n" +
                    "droid = androidhelper.Android()\n" +
                    "droid.makeToast('Hello, Username!')";

            DebugManager.printLog("scriptString = " + scriptString);

            // Run these scripts with QPython!
            // -------------------------------------

            scriptString = "#qpy:console\n" + scriptString;

            Intent intent = new Intent();
            //intent.setPackage(QPYTHON_PACKAGE_NAME); // TODO: possibly remove; I added this
            intent.setClassName(QPYTHON_PACKAGE_NAME, QPYTHON_PACKAGE_NAME + ".MPyApi");
            intent.setAction(QPYTHON_PACKAGE_NAME + ".action.MPyApi");

            Bundle mBundle = new Bundle();
            mBundle.putString("app", activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName); // changed this from "myappid"
            mBundle.putString("act", "onPyApi");
            mBundle.putString("flag", "onQPyExec");     // any String flag you may use in your context
            mBundle.putString("param", "");             // param String param you may use in your context
            mBundle.putString("pycode", scriptString);

            DebugManager.printToast(activity, "Running following script on QPython:\n" + scriptString);

            intent.putExtras(mBundle);
            activity.startActivityForResult(intent, SCRIPT_EXEC_PY);

            // To be received by activity's OnActivityResult (SignInActivity)

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Downloads scripts from server if necessary
    // Scripts are stored in local files on device
    private static class DownloadManager extends AsyncTask<Activity, Void, Void> {

        Activity activity;

        @Override
        protected Void doInBackground(Activity... args) {
            try {
                // Get activity
                activity = args[0];

                // Script API URL
                URL scriptUrl = new URL(Post.SERVER_SCRIPT_URL);

                // Download-related variables
                String serverResponse;
                JSONObject scriptReceiveJSON;

                // Establish HTTP connection with properties
                HttpURLConnection downloadConn = (HttpURLConnection) scriptUrl.openConnection();
                downloadConn.setReadTimeout(10000);
                downloadConn.setConnectTimeout(15000);
                downloadConn.setRequestMethod("GET");
                downloadConn.setDoInput(true);
                downloadConn.setDoOutput(true);
                downloadConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                downloadConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                // Connect to server
                downloadConn.connect();

                // Read from server
                InputStream in;
                try {
                    in = downloadConn.getInputStream();
                    int ch;
                    StringBuffer sb = new StringBuffer();
                    while ((ch = in.read()) != -1) {
                        sb.append((char) ch);
                    }
                    serverResponse = sb.toString();
                } catch (IOException e) {
                    throw e;
                }
                if (in != null) {
                    in.close();
                }
                downloadConn.disconnect();

                // Parse all algorithms in JSON and save to file
                scriptReceiveJSON = (JSONObject) new JSONParser().parse(serverResponse);
                Set keySet = scriptReceiveJSON.keySet();
                Iterator keys = keySet.iterator();

                Log.println(Log.DEBUG, "SCRIPTNAMES", scriptReceiveJSON.toString());

                // TODO: Fix this not working
                AppDataManager.clearScriptNames();

                HashMap<String, String> scripts = new HashMap<>();
                while (keys.hasNext()) {
                    String key = (String) keys.next();

                    Log.println(Log.DEBUG, "SCRIPTNAMES", "Made it here! key = " + key);

                    String value = scriptReceiveJSON.get(key).toString();

                    // Add algorithm key and code string to dictionary
                    scripts.put(key, value);

                    Log.println(Log.DEBUG, "SCRIPTNAMES", "key = " + key + ", value = " + value);

                    // Store algorithms as Python files
                    try {
                        // Attempt to open python file with unique
                        FileOutputStream fos;
                        Context context = activity.getApplicationContext();
                        String scriptFileName = key + SCRIPT_BASE_FILE_NAME;
                        fos = context.getApplicationContext().openFileOutput(scriptFileName, Context.MODE_PRIVATE);
                        fos.getChannel().truncate(0);
                        fos.write(value.getBytes());
                        fos.flush();
                        fos.close();

                        Log.println(Log.DEBUG, "SCRIPTNAMES", "wrote script name to file = " + scriptFileName);

                        // Add to index of algorithms app is aware of
                        // NOTE: We only store algorithm name if file creation is success
                        AppDataManager.addScriptName(key);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.println(Log.DEBUG, "SCRIPTNAMES", "FileNotFoundException");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.println(Log.DEBUG, "SCRIPTNAMES", "IOException");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.println(Log.DEBUG, "SCRIPTNAMES", "Exception");
            }

            ArrayList<String> scriptNames = AppDataManager.getScriptNames();
            for (String name : scriptNames) {
                Log.println(Log.DEBUG, "SCRIPTNAMES", name);
            }
            Log.println(Log.DEBUG, "SCRIPTNAMES", "# names = " + scriptNames.size() + " ----------");

            Log.println(Log.DEBUG, "SCRIPTNAMES", AppDataManager.getXML());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            run(activity, "alg1");
            //run(activity, "alg2");

            super.onPostExecute(aVoid);
        }
    }
}
