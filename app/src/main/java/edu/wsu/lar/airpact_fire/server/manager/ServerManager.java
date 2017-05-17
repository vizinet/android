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

// NOTE: This manager should never touch a bit of UI or DB code
public class ServerManager {

    // URL fields
    public static final String BASE_URL = "http://airpacfire.eecs.wsu.edu";
    public static final String UPLOAD_URL = BASE_URL + "/file_upload/upload";
    public static final String AUTHENTICATION_URL = BASE_URL + "/user/appauth";
    public static final String REGISTER_URL = BASE_URL + "/user/register";
    public static final String INFO_URL = BASE_URL + "/";

    // Data standards
    // TODO: Possibly move this to AppManager or DataManager
    public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    public static final double[] GPS_DEFAULT_LOC = {46.73267, -117.163454}; // Pullman, WA

    public interface ServerCallback {
        void onStart(Object... args);
        void onFinish(Object... args);
    }

    /**
     * Async method for initializing authentication of user credentials with server
     *
     * @author  Luke Weber
     * @param   username    server alias of user
     * @param   password
     * @param   callback    interface of callback functions
     */
    public void authenticate(Context context, String username, String password, ServerCallback callback) {
        boolean authenticated = false;
        AuthenticationManager authenticationManager = new AuthenticationManager(context, callback);
        authenticationManager.execute(username, password);
    }

    // Gets run when new credentials are found that are not in the database
    private class AuthenticationManager extends AsyncTask<String, Void, Boolean> {

        private Context mContext;
        private ServerCallback mCallback;
        private String mUsername, mPassword;

        public AuthenticationManager(Context context, ServerCallback callback) {
            mContext = context;
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCallback.onStart(mContext);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            // Retrieve passed credentials
            mUsername = params[0];
            mPassword = params[1];

            boolean isUser = false;

            try {
                // Send package for server
                JSONObject authenticationSendJSON = new JSONObject();
                authenticationSendJSON.put("username", mUsername);
                authenticationSendJSON.put("password", mPassword);
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
                isUser = Boolean.parseBoolean((String) authenticationReceiveJSON.get("isUser"));

                authOutputStream.flush();
                authOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return isUser;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mCallback.onFinish(result, mUsername, mPassword);
        }
    }
}
