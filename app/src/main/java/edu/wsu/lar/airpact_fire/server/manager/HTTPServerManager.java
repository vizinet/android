// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.server.manager;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;

public class HTTPServerManager implements ServerManager {

    private DebugManager mDebugManager;
    private Activity mActivity;

    public HTTPServerManager(DebugManager debugManager, Activity activity) {
        mDebugManager = debugManager;
        mActivity = activity;
    }

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
        AuthenticationManager authenticationManager = new AuthenticationManager(mActivity, callback);
        authenticationManager.execute(username, password);
    }

    @Override
    public void onSubmit(Context context, PostObject postObject, ServerCallback callback) {

        // Do some pre-authentication to get secret key (using dummy callback)
        UserObject userObject = postObject.getUser();
        ArrayList<Object> authenticationObjects;
        String secretKey = "";
        boolean isUser = false;
        AuthenticationManager authenticationManager = new AuthenticationManager(mActivity,
                new ServerCallback() {
            @Override
            public Object onStart(Object... args) { return null; }
            @Override
            public Object onFinish(Object... args) { return null; }
        });
        try {
            // Wait for authentication to occur
            authenticationObjects = authenticationManager.execute(
                    userObject.getUsername(), userObject.getPassword()).get();
            isUser = (Boolean) authenticationObjects.get(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return;
        }
        if (!isUser) { return; }
        secretKey = (String) authenticationObjects.get(1);
        postObject.setSecretKey(secretKey);
        JSONObject jsonObject = postObject.toJSON();

        // Attempt submission
        SubmissionManager submissionManager = new SubmissionManager(mActivity, callback);
        submissionManager.execute(jsonObject);
    }

    // Gets run when new credentials are found that are not in the database
    private class AuthenticationManager extends AsyncTask<String, Void, ArrayList<Object>> {

        private Activity mActivity;
        private ServerCallback mCallback;
        private String mUsername, mPassword;

        public AuthenticationManager(Activity activity, ServerCallback callback) {
            mActivity = activity;
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCallback.onStart(mActivity);
        }

        @Override
        protected ArrayList<Object> doInBackground(String... params) {

            // Retrieve passed credentials
            mUsername = params[0];
            mPassword = params[1];

            // Send resultant array to onPostExecute and anyone who calls AuthenticationManager.get()
            ArrayList resultArrayList = new ArrayList();
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

            resultArrayList.add(isUser);
            resultArrayList.add(secretKey);

            return resultArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> result) {
            boolean isUser = (Boolean) result.get(0);
            String secretKey = (String) result.get(1);
            mCallback.onFinish(isUser, mUsername, mPassword, secretKey);
        }
    }

    // Takes Post object, converts this into JSON, and submits it
    private class SubmissionManager extends AsyncTask<Object, Void, ArrayList<Object>> {

        private Activity mActivity;
        private ServerCallback mCallback;

        public SubmissionManager(Activity activity, ServerCallback callback) {
            mActivity = activity;
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCallback.onStart(mActivity);
        }

        @Override
        protected ArrayList doInBackground(Object... args) {

            ArrayList resultArrayList = new ArrayList();
            boolean didSubmit = false;
            double serverOutput = -1;
            int serverImageId = -1;

            try {

                // Get post to submit
                JSONObject jsonObject = (JSONObject) args[0];

                URL postUrl = new URL(Reference.SERVER_UPLOAD_URL);
                HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();

                // Add secret key and convert to JSON
                String postMessage = jsonObject.toString();
                byte[] postBytes = postMessage.getBytes("UTF-8");

                mDebugManager.printLog("postBytes.length = " + postBytes.length);

                // Connection properties
                postConnection.setReadTimeout(30000);
                postConnection.setConnectTimeout(15000);
                postConnection.setRequestMethod("POST");
                postConnection.setDoInput(true);
                postConnection.setDoOutput(true);
                postConnection.setFixedLengthStreamingMode(postBytes.length);
                postConnection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
                postConnection.setRequestProperty("Accept","*/*");
                postConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                postConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                // Connect
                postConnection.connect();

                // JSON package send
                OutputStream postOutputStream = new BufferedOutputStream(
                        postConnection.getOutputStream());
                postOutputStream.write(postBytes);
                postOutputStream.close(); // TODO: See if it works

                //int status = postConnection.getResponseCode();
                //mDebugManager.printLog("Response code = " + status);

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
                mDebugManager.printLog("postReceiveJSON = " + postReceiveJSON);

                // Did post succeed?
                String postStatus = postReceiveJSON.get("status").toString();
                didSubmit = postStatus.equals("success");

                // Get algorithm result
                serverOutput = Double.parseDouble(postReceiveJSON.get("output").toString());

                // Image ID, to construct website URL
                serverImageId = Integer.parseInt(postReceiveJSON.get("imageID").toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            resultArrayList.add(didSubmit);
            resultArrayList.add(serverOutput);
            resultArrayList.add(serverImageId);

            return resultArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            boolean didSubmit = (boolean) arrayList.get(0);
            double serverOutput = (double) arrayList.get(1);
            int serverImageId = (int) arrayList.get(2);
            mCallback.onFinish(didSubmit, serverOutput, serverImageId);
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
            /*
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
            */
        }
    }
}
