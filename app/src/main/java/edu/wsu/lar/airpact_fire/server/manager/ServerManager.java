/* Server Manager
 * Communication interface between app and AIRPACT-Fire server
 */

package edu.wsu.lar.airpact_fire.server.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import edu.wsu.lar.airpact_fire.activity.SignInActivity;
import edu.wsu.lar.airpact_fire.data.manager.AppDataManager;
import edu.wsu.lar.airpact_fire.data.manager.RealmDataManager;

public class ServerManager {

    // URL fields
    public static final String BASE_URL = "http://airpacfire.eecs.wsu.edu";
    public static final String UPLOAD_URL = BASE_URL + "/file_upload/upload";
    public static final String AUTHENTICATION_URL = BASE_URL + "/user/appauth";
    public static final String REGISTER_URL = BASE_URL + "/user/register";

    // Data standards
    // TODO: Possibly move this to App manager
    public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    public static final double[] GPS_DEFAULT_LOC = {46.73267, -117.163454}; // Pullman, WA

    /**
     * Returns an Image object that can then be painted on the screen.
     * The url argument must specify an absolute {@link URL}. The name
     * argument is a specifier that is relative to the url argument.
     * <p>
     * This method always returns immediately, whether or not the
     * image exists. When this applet attempts to draw the image on
     * the screen, the data will be loaded. The graphics primitives
     * that draw the image will incrementally paint on the screen.
     *
     * @author Luke Weber
     * @param  username  server alias of user
     * @param  password
     * @return true/false depending on server authentication
     */
    public boolean authenticate(Context context, String username, String password) {
        boolean authenticated = false;
        AuthenticationManager authenticationManager = new AuthenticationManager(context);
        try {
            authenticated = authenticationManager.execute(username, password).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return authenticated;
    }

    // Gets run when new credentials are found that are not in the database
    // TODO: Remove UI stuff from here (ProgressDialog)
    private class AuthenticationManager extends AsyncTask<String, Void, Boolean> {

        private Context mContext;
        private ProgressDialog mProgress;

        public AuthenticationManager(Context context) {
            mContext = context;
            mProgress = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Show loading display
            mProgress.setTitle("Signing In...");
            mProgress.setMessage("Please wait while we attempt authentication");
            mProgress.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            // Retrieve passed credentials
            String username = params[0];
            String password = params[1];

            boolean isUser = false;

            try {
                // Send package for server
                JSONObject authenticationSendJSON = new JSONObject();
                authenticationSendJSON.put("username", username);
                authenticationSendJSON.put("password", password);
                String sendMessage = authenticationSendJSON.toJSONString();

                // Establish HTTP connection
                URL authenticationUrl = new URL(AUTHENTICATION_URL);
                HttpURLConnection authConn = (HttpURLConnection) authenticationUrl.openConnection();

                // Set connection properties
                authConn.setReadTimeout(10000);
                authConn.setConnectTimeout(15000);
                authConn.setRequestMethod("POST");
                authConn.setDoInput(true);
                authConn.setDoOutput(true);
                authConn.setFixedLengthStreamingMode(sendMessage.getBytes().length);
                authConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                authConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                // Connect to server
                authConn.connect();

                // Send package
                OutputStream authOutputStream = new BufferedOutputStream(authConn.getOutputStream());
                authOutputStream.write(sendMessage.getBytes());

                // NOTE: Must flush
                authOutputStream.flush();

                // Server reply
                JSONObject authenticationReceiveJSON;
                String serverResponse;
                InputStream in;
                try {
                    int ch;
                    in = authConn.getInputStream();
                    StringBuffer sb = new StringBuffer();
                    while ((ch = in.read()) != -1) { sb.append((char) ch); }
                    serverResponse = sb.toString();
                } catch (IOException e) { throw e; }
                if (in != null) { in.close(); }

                // Parse response
                authenticationReceiveJSON = (JSONObject) new JSONParser().parse(serverResponse);

                // See if credentials were authenticated
                isUser = Boolean.parseBoolean(
                        (String) authenticationReceiveJSON.get("isUser"));
                if (isUser) {
                    (new RealmDataManager(mContext)).createAndAddUser(username, password);
                } // Do nothing if not real user

                authOutputStream.flush();
                authOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return isUser;
        }
/*
        @Override
        protected Void onPostExecute(Void arg) {

            // Dismiss loading dialog
            mProgress.dismiss();

            // TODO: Something will a callback
            // Open up home screen
            if (Boolean.parseBoolean(AppDataManager.getUserData(AppDataManager.getRecentUser(),
                    "isAuth"))) {
                Toast.makeText(mContext, "Authentication successful.\nWelcome!",
                        Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(mContext, "Could not authenticate user.\nPlease try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        */
    }
}
