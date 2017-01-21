package lar.wsu.edu.airpact_fire;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Object for handling our picture post for current user, along with all the metadata
public class Post {

    // TODO: Refactor whole project to accompany recently changed instance methods
    // TODO: Add Date (to SQL) and PostNum
    // TODO: Fix problem with toSubmissionJSON() getting XML data
    // TODO: Make it so if a field here gets changed, change the SQL or XML?
    // TODO: If post already exists, don't create another one. Rather, update the post somehow
    // TODO: How about this: when a post is created in the constructor, create the post in SQL (if it doesn't already exist)
    // TODO: Split up methods of SubmissionManager to increase readability
    // TODO: Use getters and setters for post fields
    // TODO: Possibly create a separate class for testing all of these methods

    // Constant server vars
    public static final String[] USER_AUTH_FIELDS = {
            "username",
            "password"
    };
    public static final String[] POST_SUBMIT_FIELDS = {
            "user",             // String
            "description",      // String
            "image",            // String
            "secretKey",        // String
            "highColor",        // int
            "highX", "highY",   // float
            "lowColor",         // int
            "lowX", "lowY",     // float
            "visualRange",      // float
            "geoX", "geoY",     // float
            "tags"              // String
    };
    public static final String[] POST_LOCAL_FIELDS = {
            "isPosted",
            "date",
            "secretKey",
            "postId",
            "userPostId"
    };
    public static final String SERVER_BASE_URL = "http://airpacfire.eecs.wsu.edu";//"https://dry-harbor-33710.herokuapp.com";
    public static final String SERVER_UPLOAD_URL = SERVER_BASE_URL + "/file_upload/upload";
    public static final String SERVER_AUTH_URL = SERVER_BASE_URL + "/user/appauth";
    public static final String SERVER_REGISTER_URL = SERVER_BASE_URL + "/user/register";
    public static final String SERVER_SCRIPT_URL = SERVER_BASE_URL + "/getPythonScripts/";
    // Date format
    public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    // Geo constants
    public static final double[] GPS_DEFAULT_LOC = {46.73267, -117.163454}; // Pullman, WA
    // For submission
    public static Context Context;
    public static AppCompatActivity Activity;
    // Debug
    public static boolean isUser;
    public static String debugOut;
    public static JSONObject debugJSON;
    // Mapping member fields to their names for efficiency
    public final Map<String, String> SubmitFieldMap;
    // Instance post fields
    public String
            User,
            Description,
            Image,
            SecretKey,
            HighColor,
            HighX, HighY,
            LowColor,
            LowX, LowY,
            VisualRangeOne, VisualRangeTwo,
            GeoX, GeoY,
            Tags;
    // Array of all post field references
    public final String[] SubmitFieldVars = {
            User,
            Description,
            Image,
            SecretKey,
            HighColor,
            HighX, HighY,
            LowColor,
            LowX, LowY,
            VisualRangeOne, VisualRangeTwo,
            GeoX, GeoY,
            Tags
    };
    public String IsPosted;
    public Calendar Date;
    public long SqlId, PostNum;
    {
        // Initialize hash-map
        SubmitFieldMap = new HashMap<>();

        // Map each string to a reference of its corresponding variable
        for (int i = 0; i < Post.POST_SUBMIT_FIELDS.length; i++) {
            SubmitFieldMap.put(Post.POST_SUBMIT_FIELDS[i], SubmitFieldVars[i]);
        }
    }

    // Create new post from XML values
    Post() {
        // TODO Fix all of these brute force tactics
        // Post XML => Post JSON
        String lastUser = AppDataManager.getRecentUser();
        User = lastUser;
        Description = AppDataManager.getUserData(User, "description");
        Image = AppDataManager.getUserData(User, "image");
        SecretKey = AppDataManager.getUserData(User, "secretKey");
        HighColor = AppDataManager.getUserData(User, "highColor");
        HighX = AppDataManager.getUserData(User, "highX");
        HighY = AppDataManager.getUserData(User, "highY");
        LowColor = AppDataManager.getUserData(User, "lowColor");
        LowX = AppDataManager.getUserData(User, "lowX");
        LowY = AppDataManager.getUserData(User, "lowY");
        VisualRangeOne = AppDataManager.getUserData(User, "visualRangeOne");
        VisualRangeTwo = AppDataManager.getUserData(User, "visualRangeTwo");
        GeoX = AppDataManager.getUserData(User, "geoX");
        GeoY = AppDataManager.getUserData(User, "geoY");
        Tags = AppDataManager.getUserData(User, "tags");

        // Now for extra fields...

        // Creation time is now
        Date = Calendar.getInstance();
        Date.setTime(new Date());

        // Values to be set by SubmissionManager
        IsPosted = "false";
        setSecretKey(null);
        SqlId = PostNum = -1;

        // Save new post to SQL
        store(Post.Context);
    }

    // Create post from SQL value
    // (ergo, get a post that is queued and already in database)
    Post(long sqlId) {

        // Pass in this empty post to be populated
        PostDataManager.populatePost(Post.Context, sqlId, this);

        store(Post.Context);
    }

    // Big-ass constructor
    // (for PostDataManager)
    Post(String user, String description, String image, String secretKey, String highColor,
         String highX, String highY, String lowColor, String lowX, String lowY, String visualRange,
         String geoX, String geoY, String tags, String isPosted, long sqlId, Calendar date, long postNum) {

        // Submission values
        User = user;
        Description = description;
        Image = image;
        SecretKey = secretKey;
        HighColor = highColor;
        HighX = highX;
        HighY = highY;
        LowColor = lowColor;
        LowX = lowX;
        LowY = lowY;
        VisualRangeOne = visualRange;
        // TODO
        VisualRangeTwo = visualRange;
        GeoX = geoX;
        GeoY = geoY;
        Tags = tags;

        // Additional
        IsPosted = isPosted;
        SqlId = sqlId;
        PostNum = postNum;
        Date = date;
    }

    // Result displays
    public static void showPostSuccess() {
        Toast.makeText(Post.Context, "Post was successful :)", Toast.LENGTH_SHORT).show();
    }

    public static void showPostFailure() {
        Toast.makeText(Post.Context, "Failed to post :(", Toast.LENGTH_SHORT).show();
    }

    public static void showServerDownAndQueue() {
        Toast.makeText(Post.Context, "Server is unavailable. Queueing post...", Toast.LENGTH_LONG).show();
    }

    public String getSecretKey() {
        return SecretKey;
    }

    // Getters and setters (so we can do things when variables get updated)
    public void setSecretKey(String value) {
        // Set private variable
        SecretKey = value;

        // Set XML
        AppDataManager.setUserData(AppDataManager.getRecentUser(), "secretKey", SecretKey);
    }

    // Create JSON object from instance fields
    public JSONObject toSubmissionJSON() {
        // Root
        JSONObject root = new JSONObject();

        /* OLD STUFF
        root.put("user", User);
        root.put("description", Description);
        root.put("image", Image);
        root.put("secretKey", getSecretKey());
        root.put("highColor", HighColor);
        root.put("highX", HighX);
        root.put("highY", HighY);
        root.put("lowColor", LowColor);
        root.put("lowX", LowX);
        root.put("lowY", LowY);
        root.put("visualRange", VisualRange);
        root.put("geoX", GeoX);
        root.put("geoY", GeoY);
        root.put("tags", Tags);
        */

        // TODO: Grab these from user preferences in SQL
        String AlgorithmType = "object_sky"; // or "near_far"
        String DistanceUnits = "kilometers"; // or "miles"
        String Time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        // Post submission field vars => JSON
        root.put("user", User);
        root.put("description", Description);
        root.put("image", Image);
        root.put("secretKey", getSecretKey());
        // For "far object" or "sky"
        root.put("highColor", HighColor);
        root.put("highX", HighX);
        root.put("highY", HighY);
        // For "near object"
        root.put("lowColor", LowColor);
        root.put("lowX", LowX);
        root.put("lowY", LowY);
        // Can (currently) be "near_far" or "object_sky"
        root.put("algorithmType", AlgorithmType);
        // For "near object"
        root.put("visualRangeOne", VisualRangeOne);
        // For "far object" or "sky," depending on algorithm type
        root.put("visualRangeTwo", VisualRangeTwo);
        root.put("geoX", GeoX);
        root.put("geoY", GeoY);
        // User-defined location
        root.put("tags", Tags);
        // Could be "kilometers" or "miles"
        root.put("distanceUnits", DistanceUnits);
        // In the form "yyyy.MM.dd.HH.mm.ss"
        root.put("time", Time);

        return root;
    }

    // Post data to server
    // For submitting new post from XML
    public void submit(Context context) {
        // Reassuring
        Context = context;

        // Queue post if no internet is available
        if (!Util.isServerAvailable(Post.Activity)) {
            showServerDownAndQueue();
            queue(context);
            return;
        }

        // Creates our async manager and send the data off
        SubmissionManager submissionManager = new SubmissionManager();
        submissionManager.execute(this);
    }

    // Queue post in SQL database
    public void queue(Context context) {
        // Reassuring
        Context = context;
        // Flag post and save it
        IsPosted = "false";

        // Run queueing of post in asynchronous task
        RecordManager recordManager = new RecordManager();
        recordManager.execute(this);
    }

    /* CONSTRUCTORS */

    // Like queueing, but "isPosted" is true
    public void save(Context context) {
        Context = context;
        IsPosted = "true";

        // Run queueing of post in asynchronous task
        RecordManager recordManager = new RecordManager();
        recordManager.execute(this);
    }

    public void draft(Context context) {
        // TODO
    }

    // Put post in SQL
    public void store(Context context) {
        // TODO
    }

    // Remove post from SQL
    public void delete(Context context) {
        PostDataManager.deletePost(context, this);

        // TODO: Improve the flow of all these methods
        Toast.makeText(context, "Post deleted.", Toast.LENGTH_SHORT).show();

        // Go to all posts
        Intent intent = new Intent(context, QueuedPostsActivity.class);
        Post.Activity.startActivity(intent);
    }

    // TODO Find better placement
    // Where to kickoff posting? Make sure it only gets done once
    public void kickoffBackgroundPosting(Context context) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BackgroundPostService.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + (1000 * 60 * 5)); // Start 5 mins after
        // Note: hoping that by not setting the time and hour of day the alarm will start asap
        //calendar.set(Calendar.HOUR_OF_DAY, 8);
        //calendar.set(Calendar.MINUTE, 30);

        // Set repeat interval for 1 hour
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 60, alarmIntent);
    }
}


/* MANAGERS */

// Does background posting
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

// Deals with server
class SubmissionManager extends AsyncTask<Post, Void, Void> {

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
                User.postKeys.add(userKey);
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
            DebugManager.printLog("algorithmOutput (from server) = " + algorithmOutput);

            // Image ID, to construct website URL
            String imageID = postReceiveJSON.get("imageID").toString();
            DebugManager.printLog("imageID = " + imageID);

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

// Takes post and creates/updates row in SQL corresponding to said post
class RecordManager extends AsyncTask<Post, Void, Void> {

    private ProgressDialog progress = new ProgressDialog(Post.Activity);
    private long postId;

    private Post post;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Show loader
        progress.setTitle("Saving Post");
        progress.setMessage("Please wait while your post is stored locally...");
        progress.show();
        while (!progress.isShowing()) try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Post... params) {

        // Get post
        post = params[0];

//        // Create queued post from XML
//        String isPosted = params[0];
//        String user = AppDataManager.getRecentUser();
//        Calendar date = Calendar.getInstance();
//        date.setTime(new Date());
//        Post post = new Post(
//                user,
//                AppDataManager.getUserData(user, "description"),
//                AppDataManager.getUserData(user, "image"),
//                AppDataManager.getUserData(user, "secretKey"),
//                AppDataManager.getUserData(user, "highColor"),
//                AppDataManager.getUserData(user, "highX"),
//                AppDataManager.getUserData(user, "highY"),
//                AppDataManager.getUserData(user, "lowColor"),
//                AppDataManager.getUserData(user, "lowX"),
//                AppDataManager.getUserData(user, "lowY"),
//                AppDataManager.getUserData(user, "visualRange"),
//                AppDataManager.getUserData(user, "geoX"),
//                AppDataManager.getUserData(user, "geoY"),
//                AppDataManager.getUserData(user, "tags"),
//                isPosted,
//                -1,
//                date
//
//        );

        // Add post (create new or update previous)
        postId = PostDataManager.addPost(Post.Context, post);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Hide progress
        progress.dismiss();

        // Display result
        Toast.makeText(Post.Context, "Post stored as #" + post.PostNum + ".\nIt will be available" +
                " in your local post gallery.", Toast.LENGTH_LONG).show();

        // Go home
        Util.goHome(Post.Activity);
    }
}