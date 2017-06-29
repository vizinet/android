package edu.wsu.lar.airpact_fire.server.manager;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.wsu.lar.airpact_fire.Reference;
import edu.wsu.lar.airpact_fire.data.Post;
import edu.wsu.lar.airpact_fire.data.manager.AppDataManager;
import edu.wsu.lar.airpact_fire.data.manager.PostDataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;

public class HTTPServerManager implements ServerManager {

    @Override
    public void onAppStart(Object... args) {

    }

    @Override
    public void onAppEnd(Object... args) {

    }

    @Override
    public void onActivityStart(Object... args) {

    }

    @Override
    public void onActivityEnd(Object... args) {

    }

    @Override
    public void onLogin(Object... args) {

    }

    @Override
    public void onLogout(Object... args) {

    }

    @Override
    public void onAuthenticate(Context context, String username, String password,
                               ServerCallback callback) {
        AuthenticationManager authenticationManager = new AuthenticationManager(context, callback);
        authenticationManager.execute(username, password);
    }

    @Override
    public void onSubmit(Context context, PostObject postObject, ServerCallback callback) {

        // TODO: See if we'll have a problem accessing Realm from AsyncTask thread (maybe pass in hard values)

        // Do some pre-authentication to get secret key (using dummy callback)
        UserObject userObject = postObject.getUser();
        ArrayList<Object> authenticationObjects;

        // Attempt authentication, obtain secretKey for posting
        AuthenticationManager authenticationManager = new AuthenticationManager(context,
                new ServerCallback() {
            @Override
            public Object onStart(Object... args) { return null; }
            @Override
            public Object onFinish(Object... args) { return null; }
        });
        authenticationManager.execute(userObject.getUsername(), userObject.getPassword());

        boolean isUser = false;
        String secretKey = "";
        try {
            authenticationObjects = authenticationManager.get();
            isUser = (Boolean) authenticationObjects.get(0);
            if (!isUser) { return; }
            secretKey = (String) authenticationObjects.get(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Attempt submission
        SubmissionManager submissionManager = new SubmissionManager(context, callback);
        submissionManager.execute(postObject, secretKey);
    }

    // Gets run when new credentials are found that are not in the database
    private class AuthenticationManager extends AsyncTask<String, Void, ArrayList<Object>> {

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
        protected ArrayList<Object> doInBackground(String... params) {

            // Retrieve passed credentials
            mUsername = params[0];
            mPassword = params[1];

            boolean isUser = false;
            String secretKey = "";

            try {

                // Send package for server
                JSONObject authenticationSendJSON = new JSONObject();
                authenticationSendJSON.put("username", mUsername);
                authenticationSendJSON.put("password", mPassword);
                String sendMessage = authenticationSendJSON.toJSONString();

                // Establish HTTP connection
                URL authenticationUrl = new URL(Reference.SERVER_AUTHENTICATION_URL);
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
                if (isUser) {
                    secretKey = authenticationReceiveJSON.get("secretKey").toString();
                }

                authOutputStream.flush();
                authOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Send resultant array to onPostExecute and anyone who calls AuthenticationManager.get()
            ArrayList resultArrayList = new ArrayList();
            resultArrayList.add(isUser);
            resultArrayList.add(secretKey);

            return resultArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            boolean isUser = (Boolean) result.get(0);
            String secretKey = (String) result.get(1);
            mCallback.onFinish(isUser, mUsername, mPassword, secretKey);
        }
    }

    // TODO: Refactor this and create interface between a DataManager and ServerManager for posting
    // Takes Post object, converts this into JSON, and submits it
    private class SubmissionManager extends AsyncTask<Object, Void, Void> {

        private Context mContext;
        private ServerCallback mCallback;
        private PostObject mPostObject;
        private boolean mDidSubmit;
        private String mSecretKey;
        private double mServerOutput;
        private long mImageServerId;

        public SubmissionManager(Context context, ServerCallback callback) {
            mContext = context;
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCallback.onStart(mContext);
        }

        @Override
        protected Void doInBackground(Object... args) {
            try {

                // Get post to submit
                mPostObject = (PostObject) args[0];
                mSecretKey = (String) args[1];

                URL postUrl = new URL(Reference.SERVER_UPLOAD_URL);
                HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();

                // Add secret key and convert to JSON
                mPostObject.setSecretKey(mSecretKey);
                String postMessage = mPostObject.toJSON().toString();

                // Connection properties
                postConnection.setReadTimeout(30000);
                postConnection.setConnectTimeout(15000);
                postConnection.setRequestMethod("POST");
                postConnection.setDoInput(true);
                postConnection.setDoOutput(true);
                postConnection.setFixedLengthStreamingMode(postMessage.getBytes().length);
                postConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                postConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                // Connect
                postConnection.connect();

                // JSON package send
                OutputStream postOutputStream = new BufferedOutputStream(postConnection.getOutputStream());
                postOutputStream.write(postMessage.getBytes());
                postOutputStream.flush();

                // Read if post succeeded or failed
                InputStream postStatusInputStream;
                String serverPostResponse;
                try {
                    int ch;
                    StringBuffer sb = new StringBuffer();
                    postStatusInputStream = postConnection.getInputStream();
                    while ((ch = postStatusInputStream.read()) != -1) {
                        sb.append((char) ch);
                    }
                    serverPostResponse = sb.toString();
                } catch (IOException e) {
                    throw e;
                }
                // Close input stream
                if (postStatusInputStream != null) postStatusInputStream.close();

                // Parse response
                JSONObject postReceiveJSON = (JSONObject) new JSONParser()
                        .parse(serverPostResponse);

                // Did post succeed?
                String postStatus = postReceiveJSON.get("status").toString();
                mDidSubmit = postStatus.equals("success");

                // Get algorithm result
                // TODO: Have this naming adapted on server side
                mServerOutput = Double.parseDouble(
                        postReceiveJSON.get("output").toString());

                // Image ID, to construct website URL
                mImageServerId = Integer.parseInt(postReceiveJSON.get("imageID").toString());

                // Clean up
                postOutputStream.flush();
                postOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCallback.onFinish(mDidSubmit, mServerOutput, mImageServerId);
        }
    }

    // Does background posting
    // TODO: Actually implement this
    // Note:
    //      - doInBackground(...) runs even if app is destroyed, but pre and post are on UI thread
    //      - Never wait around within a service, as it uses lots of system resources
    //      - Using AlarmManager to kickoff this class periodically
    class BackgroundPostService extends IntentService {

        private boolean isBackgroundPostingEnabled;

        private void checkIfBackgroundPostsEnabled() {
            // TODO
            isBackgroundPostingEnabled = true;
        }

        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param name Used to name the worker thread, important only for debugging.
         */
        public BackgroundPostService(String name) {
            super(name);
        }

        // Where the work gets done
        @Override
        protected void onHandleIntent(Intent intent) {
            // Run check/posts every hour

            // Check xml if user allows background posting
            checkIfBackgroundPostsEnabled();

            // Only continue if enabled
            if (!isBackgroundPostingEnabled) return;

            // TODO check un-posted posts and act on the that
            // Get posts
            List<Post> posts = PostDataManager.getPosts(this.getApplicationContext());
            //Queue<Post> unposted = <Post>();
            for (Post p : posts) {
                // If Post.Status is unposted
                if (!Boolean.getBoolean(p.IsPosted)) {

                }
                // If post not current in process
                // Attempt to post
                // Else
                // Wait in queue

            }
        }
    }
}
