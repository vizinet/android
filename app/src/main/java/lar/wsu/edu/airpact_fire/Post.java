package lar.wsu.edu.airpact_fire;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

// Object for handling our picture post for current user, along with all the metadata
public class Post {
    // Universal vars
    public static final String[] POST_FIELDS = {"user", "description", "image", "secretKey", "highColor", "highX"
            , "highY", "lowColor", "lowX", "lowY", "visualRange", "geoX", "geoY", "tags"};
    public static final String SERVER_UPLOAD_URL = "http://76.178.152.115:8000/file_upload/upload";
    public static final String SERVER_AUTH_URL = "http://76.178.152.115:8000/user/appauth";

    // Instance vars
    public static String ImageLocation, Description;
    public static String Tags = "Mount Baker,Cloudy,Funky smell,Hey a squirrel!"; // CSV
    public static float VisualRange;
    public static float[] LatitudeLongitude = {0, 0};
    public static Bitmap Image;
    public static Date Time;
    public static int HighColor, LowColor;
    public static float LowXY[], HighXY[];

    public static boolean DidFail = true;
    public static Context Context;

    // Debug
    public static boolean isUser;
    public static String debugOut;
    public static JSONObject debugJSON;

    // Create JSON object to send to server
    public static JSONObject toJSON() {
        // TODO Get real file of JPEG
        // Bitmap to string
        Bitmap bitmap = null;
        String imageString = "";
//        if (Post.ImageLocation != null)
//        {
//            // Convert full image to Bitmap to String
//            File f = new File(Post.ImageLocation);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            try {
//                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            if (bitmap != null) {
//                imageString = Base64.encodeToString(Util.getBytesFromBitmap(bitmap), Base64.DEFAULT);
//            }
//        }
        // TODO Replace with actual file
        imageString = Base64.encodeToString(Util.getBytesFromBitmap(Post.Image), Base64.DEFAULT);

        // Root
        JSONObject root = new JSONObject();

        // Fields
        root.put("user", User.username);
        root.put("description", Post.Description);
        root.put("image", imageString);
        // Note: secretKey is added through NetworkManager (after authentication)
        root.put("highColor", Post.HighColor);
        root.put("highX", Post.HighXY[0]);
        root.put("highY", Post.HighXY[1]);
        root.put("lowColor", Post.LowColor);
        root.put("lowX", Post.LowXY[0]);
        root.put("lowY", Post.LowXY[1]);
        root.put("visualRange", Post.VisualRange);
        root.put("geoX", Post.LatitudeLongitude[0]);
        root.put("geoY", Post.LatitudeLongitude[1]);
        root.put("tags", Post.Tags);

        return root;
    }

    // Post data to server
    public static void submit(Context context) {
        Post.debugOut += "ImageLocation: " + Post.ImageLocation + "\n";
        Post.debugOut += "Image byte count: " + Post.Image.getByteCount() + "\n\n";

        if (!Util.isInternetAvailable()) {
            Toast.makeText(context, "Internet is not available. Adding post to queue with id = " + 0 + " .", Toast.LENGTH_LONG).show();

            // TODO: Queue the post

            return;
        }

        // Creates our async network manager and sends the data off to be packaged
        NetworkManager networkManager = new NetworkManager();
        JSONObject postJSON = Post.toJSON();

        networkManager.execute(postJSON);
    }
}

// Deals with server
class NetworkManager extends AsyncTask<JSONObject, Void, Void> {

    protected Void doHandshake(JSONObject postJSON) {
        // TODO

        return null;
    }

    protected Void doPost() {
        // TODO

        return null;
    }

    @Override
    protected Void doInBackground(JSONObject... args) {
        try {
            JSONObject postJSON = args[0];

            // Authentication URL
            URL authUrl = new URL(Post.SERVER_AUTH_URL);

            // JSON authentication (send) package
            JSONObject authSendJSON = new JSONObject();
            authSendJSON.put("username", User.username);
            authSendJSON.put("password", User.password);
            String sendMessage = authSendJSON.toJSONString(),
                    serverResponse,
                    userKey;
            Boolean isUser;

            // JSON receive package
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
            // NOTE: Had an error here before because I didn't flush
            authOutputStream.flush();

            Post.debugOut += "AUTHENTICATION JSON: \n" + sendMessage;

            // Server reply
            InputStream in = null; // TODO Error here
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

            Post.debugOut += "\nsecretKey: " + User.postKeys.peek() + "\n";

            // Add post key and make JSON string
            postJSON.put("secretKey", User.postKeys.remove());
            String postMessage = postJSON.toString();

            // Connection properties
            postConn.setReadTimeout(10000);
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
            postOutputStream.write(postMessage.getBytes());
            postOutputStream.flush();

            // TODO Remove
            Post.debugJSON = postJSON;
            Post.debugOut += "POST MESSAGE: \n" + postJSON.toJSONString();

            // Read if post succeeded or failed
            InputStream postStatusInputStream;
            String serverPostResponse;
            try {
                postStatusInputStream = postConn.getInputStream();
                int ch;
                StringBuffer sb = new StringBuffer();
                while ((ch = postStatusInputStream.read()) != -1) {
                    sb.append((char) ch);
                }

                serverPostResponse = sb.toString();
            } catch (IOException e) {
                throw e;
            }
            if (postStatusInputStream != null) {
                postStatusInputStream.close();
            }

            // Parse JSON
            JSONObject postReceiveJSON = (JSONObject) new JSONParser().parse(serverPostResponse);

            // Did post succeed?
            String postStatus =  postReceiveJSON.get("status").toString();
            Post.debugOut += "\n\npostStatus: '" + postStatus + "'\n\n";
            if (postStatus.equals("success")) { // TODO There's a problem here
                Post.DidFail = false;
            } else {
                Post.DidFail = true;
            }

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
        if (Post.DidFail) {
            Toast.makeText(Post.Context, "Failed to post :(", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Post.Context, "Post was successful :)", Toast.LENGTH_SHORT).show();
        }
    }
}
