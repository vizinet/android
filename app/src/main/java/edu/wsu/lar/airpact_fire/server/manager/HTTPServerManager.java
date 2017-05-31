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
import java.util.List;

import edu.wsu.lar.airpact_fire.Reference;
import edu.wsu.lar.airpact_fire.data.Post;
import edu.wsu.lar.airpact_fire.data.manager.AppDataManager;
import edu.wsu.lar.airpact_fire.data.manager.PostDataManager;

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
    public void authenticate(Context context, String username, String password, ServerCallback callback) {
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

    // TODO: Refactor this and create interface between a DataManager and ServerManager for posting
    // Takes Post object, converts this into JSON, and submits it
    private class SubmissionManager extends AsyncTask<Post, Void, Void> {

        private Post post;
        // NOTE: Trusting that this activity is non-null
        private ProgressDialog progress = new ProgressDialog(Post.Activity);
        private boolean didPostFail = true;

        @Override
        protected void onPreExecute() {
            // Show loader
            progress.setTitle("Posting Image");
            progress.setMessage("Please wait while your image is being posted to server...");
            progress.show();

            // TODO: When have time, see if we can remove this
            // Make sure it displays before doing work
            while (!progress.isShowing()) try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Post... args) {
            try {
                // Get post  we want to submit
                post = args[0];

                // Authentication URL
                URL authUrl = new URL(Post.SERVER_AUTH_URL);

                // Authentication package to send
                JSONObject authSendJSON = new JSONObject();
                authSendJSON.put("username", AppDataManager.getRecentUser());
                authSendJSON.put("password", AppDataManager.getUserData(AppDataManager.getRecentUser(), "password"));

                // Submission-related variables
                String sendMessage = authSendJSON.toJSONString(),
                        serverResponse,
                        userKey;
                Boolean isUser;
                JSONObject authReceiveJSON;

                // Establish HTTP connection
                HttpURLConnection authConn = (HttpURLConnection) authUrl.openConnection();

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

                // JSON package sent
                OutputStream authOutputStream = new BufferedOutputStream(authConn.getOutputStream());
                authOutputStream.write(sendMessage.getBytes());
                authOutputStream.flush(); // NOTE: Had an error here before because I didn't flush

                // Server reply
                InputStream in;
                try {
                    in = authConn.getInputStream();
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

                // Parse JSON
                authReceiveJSON = (JSONObject) new JSONParser().parse(serverResponse);

                // Get fields and see if server authenticated us
                isUser = Boolean.parseBoolean((String) authReceiveJSON.get("isUser"));
                if (isUser) {
                    userKey = authReceiveJSON.get("secretKey").toString();
                    // TODO: Accompany this the below commented-out code
                    //User.postKeys.add(userKey);
                    Post.isUser = true;
                } else { // Exit if not a user

                    Post.isUser = false;
                    return null;
                }

                authOutputStream.flush();
                authOutputStream.close();


            /* * * * */


                // Post URL
                URL postUrl = new URL(Post.SERVER_UPLOAD_URL);

                // Upload server
                HttpURLConnection postConn = (HttpURLConnection) postUrl.openConnection();

                // Add secret key and convert to JSON
                post.setSecretKey(userKey);
                JSONObject postJSON = post.toSubmissionJSON();

                // Add post key and make JSON string
                String postMessage = postJSON.toString();

                Log.println(Log.DEBUG, "POSTING", postMessage);

                // Connection properties
                postConn.setReadTimeout(30000);
                postConn.setConnectTimeout(15000);
                postConn.setRequestMethod("POST");
                postConn.setDoInput(true);
                postConn.setDoOutput(true);
                postConn.setFixedLengthStreamingMode(postMessage.getBytes().length);
                postConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                postConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                // Connect
                postConn.connect();

                // JSON package send
                OutputStream postOutputStream = new BufferedOutputStream(postConn.getOutputStream());
                Log.println(Log.DEBUG, "POSTING", "postMessage: " + postMessage);
                postOutputStream.write(postMessage.getBytes());
                postOutputStream.flush();

                // Read if post succeeded or failed
                InputStream postStatusInputStream;
                String serverPostResponse;
                try {
                    int ch;
                    StringBuffer sb = new StringBuffer();
                    postStatusInputStream = postConn.getInputStream();
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
                JSONObject postReceiveJSON = (JSONObject) new JSONParser().parse(serverPostResponse);

                // Did post succeed?
                String postStatus = postReceiveJSON.get("status").toString();
                didPostFail = !postStatus.equals("success");

                // Get algorithm result
                String algorithmOutput = postReceiveJSON.get("TwoTargetContrastOutput").toString();
                //DebugManager.printLog("algorithmOutput (from server) = " + algorithmOutput);

                // Image ID, to construct website URL
                String imageID = postReceiveJSON.get("imageID").toString();
                //DebugManager.printLog("imageID = " + imageID);

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

            // Dismiss dialog
            progress.dismiss();

            // See how post did and act accordingly
            if (didPostFail) {
                Post.showPostFailure();
                post.queue(Post.Context);
            } else {
                Post.showPostSuccess();
                post.save(Post.Context);
            }

            // NOTE: RecordManager will return home for us
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
