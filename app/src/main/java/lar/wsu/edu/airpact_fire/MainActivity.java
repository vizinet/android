package lar.wsu.edu.airpact_fire;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

// Acting as the ImageCaptureActivity
public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    // Stores the information about the current post
    private static Post mCurrentPost;

    // false = black, true = white
    boolean colorMode;

    ImageView mImageView, mBlackDotView, mWhiteDotView, mHighColorPatchView, mLowColorPatchView;
    ImageButton mDotModeImageButton;
    Button mCameraButton, mUploadButton;
    EditText mEditText;
    TextView mDebugText;
    TextView mWelcomeText;

    File mPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set color select mode
        colorMode = false;

        // Create new post
        mCurrentPost = new Post();

        // Attach views to variables
        mImageView = (ImageView) findViewById(R.id.captured_image_thumbnail);
        mHighColorPatchView = (ImageView) findViewById(R.id.high_color_patch_view);
        mLowColorPatchView = (ImageView) findViewById(R.id.low_color_patch_view);
        mDotModeImageButton = (ImageButton) findViewById(R.id.dot_mode_image_button);
        mCameraButton = (Button) findViewById(R.id.capture_image_button);
        mUploadButton = (Button) findViewById(R.id.upload_data_button);
        mEditText = (EditText) findViewById(R.id.description_edit_text);
        mDebugText = (TextView) findViewById(R.id.debug_text_display);
        mWelcomeText = (TextView) findViewById(R.id.welcome_text);

        // Add welcome text for user
        mWelcomeText.setText("Hey, " + User.username);

        // Dynamic black dot stuff
        mBlackDotView = new ImageView(this);
        mBlackDotView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        // Setting image resource
        mBlackDotView.setImageResource(R.drawable.black_dot);
        // Width and height
        mBlackDotView.getLayoutParams().width = 50;
        mBlackDotView.getLayoutParams().height = 50;
        // Set ID, so we can refer to it later
        View.generateViewId();
        mBlackDotView.setId(R.id.black_dot_view);

        // Same white dot stuff
        mWhiteDotView = new ImageView(this);
        mWhiteDotView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mWhiteDotView.setImageResource(R.drawable.white_dot);
        mWhiteDotView.getLayoutParams().width = 50;
        mWhiteDotView.getLayoutParams().height = 50;
        View.generateViewId();
        mWhiteDotView.setId(R.id.white_dot_view);

        // Disable button at start
        mUploadButton.setEnabled(false);

        // Create button event listeners
        mDotModeImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Invert the modes
                if (colorMode) {
                    mDotModeImageButton.setImageResource(R.drawable.black_dot);
                    colorMode = false;
                } else {
                    mDotModeImageButton.setImageResource(R.drawable.white_dot);
                    colorMode = true;
                }
            }
        });
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
                // Update UI controls
                mCameraButton.setText("Recapture");
               // mCameraButton.refreshDrawableState();
                mUploadButton.setEnabled(true);
            }
        });
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadData();
            }
        });
        // We can know when user touches image view
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO: Better thumbnail view
                // TODO: Don't let dots outside of Bitmap area
                // TODO: Fix black dot placement
                // TODO: Fix this terrible inefficient, last-minute code

                // NOTE: ImageView is set to wrap_content, so the width of ImageView is the
                // width of the encapsulated BitMap object.

                // Don't go any further, unless we've taken a picture
                //if (mCurrentPost.ImageLocation == null) { return true; }

                // Debug display
                mDebugText.setText("Touch coordinates : " +
                        String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));

                // Debug, stuff about ImageView
                //putPermCircle((int) event.getX(), (int) event.getY());
                //putPermCircle((int) mImageView.getX(), (int) mImageView.getY());

                // Add black dot to screen, if it's not already there
                if (colorMode) {
                    if (findViewById(mWhiteDotView.getId()) == null) {
                        ContentFrameLayout parent = (ContentFrameLayout) findViewById(android.R.id.content);
                        parent.addView(mWhiteDotView);
                    }
                } else {
                    if (findViewById(mBlackDotView.getId()) == null) {
                        ContentFrameLayout parent = (ContentFrameLayout) findViewById(android.R.id.content);
                        parent.addView(mBlackDotView);
                    }
                }

                // Display mBlackDotView if within ImageView bounds
                if (Util.isPointInView(mImageView, (int) event.getX(), (int) event.getY())) {

                    // Set color patch color, so we can see which color we've touched on ImageView
                    int selectedPixel = getPixelAtPos((int) event.getX(), (int) event.getY());

                    if (colorMode) {
                        mWhiteDotView.setVisibility(View.VISIBLE);

                        mLowColorPatchView.setBackgroundColor(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));
                        mWhiteDotView.setX(event.getX());
                        mWhiteDotView.setY(event.getY());
                    } else {
                        mBlackDotView.setVisibility(View.VISIBLE);

                        mHighColorPatchView.setBackgroundColor(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));
                        // Setting dot position
                        mBlackDotView.setX(event.getX());
                        mBlackDotView.setY(event.getY());
                    }
                // Otherwise, hide it
                } else {
                    if (colorMode) mWhiteDotView.setVisibility(View.INVISIBLE);
                    else mBlackDotView.setVisibility(View.INVISIBLE);
                }

                return true;
            }
        });
    }

    // Mainly a debug function to put black circle where I see fit
    public void putPermCircle(int x, int y) {
        ImageView circle = new ImageView(this);
        circle.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        // Setting image resource
        circle.setImageResource(R.drawable.black_dot);
        // Width and height
        circle.getLayoutParams().width = 25;
        circle.getLayoutParams().height = 25;

        circle.setX(x);
        circle.setY(y);

        ContentFrameLayout parent = (ContentFrameLayout) findViewById(android.R.id.content);
        parent.addView(circle);
    }

    // Gets pixel color inside mImageView given x and y coordinates

    // TODO Get analysis of circular area around x and y point
    public int getPixelAtPos(int x, int y) {
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        int pixel = bitmap.getPixel(x, y);

        return pixel;
    }

    // Converts image to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Create an image file with collision resistant title to public directory
    private File createImageFile() throws IOException {
        // Create our directory
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/AIRPACT-Fire");
        myDir.mkdirs();

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        mPhotoFile = new File(storageDir, imageFileName + ".jpg");

        /* FORMERLY
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        */

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPost.ImageLocation = mPhotoFile.getAbsolutePath();//"file:" + image.getAbsolutePath();

        return mPhotoFile;
    }

    // Takes picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                mEditText.setText("Error occurred while creating the file.");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // TODO: For some reason, the below line was causing "data" to be null in our onActivityResult method
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Resize the given Bitmap
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();

        return resizedBitmap;
    }

    // Receives taken picture as thumbnail
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Scale up the image size
            int scaleUp = 4;
            Bitmap resizedImageBitmap = getResizedBitmap(imageBitmap, imageBitmap.getWidth() * scaleUp,
                    imageBitmap.getHeight() * scaleUp);

            // Set thumbnail
            mImageView.setImageBitmap(resizedImageBitmap);

            // Write stuff to memory
            try {
                FileOutputStream out = new FileOutputStream(mPhotoFile);
                resizedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Debug text
//            mDebugText.setText("Captured and saved image as '" +  mCurrentPost.ImageLocation + "'");
        }
    }

    // Get latitude and longitude from our image file,
    // store in post
    private void getLatitudeLongitude()
    {
//        if (mCurrentPost.ImageLocation == null) return;

//        try {
////            ExifInterface exifInterface = new ExifInterface(mCurrentPost.ImageLocation);
////            exifInterface.getLatLong(mCurrentPost.LatitudeLongitude);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    // Upload JSON data to server
    private void uploadData()
    {
        //lets get our picture again because that's fun
        /*
        Bitmap bitmap;
        if(mImageView.getDrawable() instanceof BitmapDrawable)
        {
            bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        }
        else
        {
            Drawable d = mImageView.getDrawable();
            bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(),d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        */

        // TODO Get real file of JPEG
        Bitmap bitmap = null;
        String i = "";
//        if (mCurrentPost.ImageLocation != null)
//        {
//            // Convert full image to Bitmap to String
//            File f = new File(mCurrentPost.ImageLocation);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            try {
//                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            if (bitmap != null) {
//                i = Base64.encodeToString(getBytesFromBitmap(bitmap), Base64.DEFAULT);
//            }
//        }

        // creates our async network manager and sends the data off to be packaged
        NetworkManager nwork = new NetworkManager();
        // TODO: Update below line to make better
        nwork.execute(Post.SERVER_UPLOAD_URL, User.username, mEditText.getText().toString(), i);
    }

    class NetworkManager extends AsyncTask <String,Void,Void> {
        // TODO: Do network handshake to get key and see if user is validated
        private void doHandshake() {
            // 1. Send {username : "...", password : "..."} to .../user/appauth
            // 2. Receive {isUser : "true/false", key : "..."}
            //      a. If isUser, send key in JSON when posting image
            //      b. Else, there is no key. User is not authenticated. Break.
            // 3. Send stuff to server with key to .../file_upload/upload
            //      a. All keys valid for one picture upload
        }

        @Override
        protected Void doInBackground(String... args) {
            try {
                // Attempt authentication
                // Create upload URL
                URL url = new URL(Post.SERVER_AUTH_URL);

                // Create JSON send package
                org.json.simple.JSONObject sendJSON = new org.json.simple.JSONObject();
                sendJSON.put("username", User.username);
                sendJSON.put("password", User.password);
                String
                        sendMessage = sendJSON.toString(),
                        serverResponse,
                        userKey;
                Boolean isUser;

                // Create JSON receive package
                org.json.simple.JSONObject receiveJSON;
                byte[] receiveMessage = new byte[] {};

                // Establish HTTP connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set connection properties
                conn.setReadTimeout(10000); // ms
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(sendMessage.getBytes().length);
                // Make HTTP headers
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                // Connect to server
                conn.connect();

                // Send JSON package over connection
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(sendMessage.getBytes());

                // Get server reply
                InputStream in = null;
                try {
                    in = conn.getInputStream();
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
                receiveJSON = (org.json.simple.JSONObject) new JSONParser().parse(serverResponse);

                // Get fields and see if server authenticated us
                isUser = Boolean.parseBoolean((String) receiveJSON.get("isUser"));
                if (isUser) {
                    userKey = receiveJSON.get("secretKey").toString();
                    User.postKeys.add(userKey);
                } else { // Exit if not a user
                    return null;
                }

                // Now post to server
                JSONObject J = new JSONObject();
                for (int i = 0; i < args.length; i++) {
                    J.put(Post.POST_FIELDS[i], args[i + 1]);
                }
                // TODO
                J.put("secretKey", User.postKeys.remove());

//                J.put("description", args[2]);
//                J.put("image", args[3]);
//                J.put("secretKey", args[4]);
                String message = J.toString();

//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(10000 /*milliseconds*/);
//                conn.setConnectTimeout(15000 /* milliseconds */);
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setFixedLengthStreamingMode(message.getBytes().length);
//
//                //make some HTTP headers
//                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
//                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                //open
                //conn.connect();

                //setup send
                //OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(message.getBytes());
                // Clean up
                os.flush();
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }










    // SQL stuff








    // TODO: Check if internet available after post. If not, store post in local SQL database

    // Instantiate SQLLiteOpenHelper
    QueuedPostDbHelper mDbHelper = new QueuedPostDbHelper(this);

    // TODO: Replace params with legit shit
    private long storePost(int image, int distance, int description, int time) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(QueuedPostContract.PostEntry.COLUMN_NAME_IMAGE, -1);
        values.put(QueuedPostContract.PostEntry.COLUMN_NAME_DISTANCE, -1);
        values.put(QueuedPostContract.PostEntry.COLUMN_NAME_DESCRIPTION, -1);
        values.put(QueuedPostContract.PostEntry.COLUMN_NAME_TIME, -1);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(
                QueuedPostContract.PostEntry.TABLE_NAME,
                null,
                values);

        return newRowId;
    }

    // TODO: Check if it works
    private long readPost(int image, int distance, int description, int time) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                QueuedPostContract.PostEntry._ID,
                QueuedPostContract.PostEntry.COLUMN_NAME_IMAGE,
                QueuedPostContract.PostEntry.COLUMN_NAME_DESCRIPTION,
                QueuedPostContract.PostEntry.COLUMN_NAME_DISTANCE,
                QueuedPostContract.PostEntry.COLUMN_NAME_TIME
        };

        // How you want the results sorted in the resulting Cursor
        // By time
        String sortOrder =
                QueuedPostContract.PostEntry.COLUMN_NAME_TIME + " DESC";

        String selection = "",
                selectionArgs[] = {""};

        Cursor c = db.query(
                QueuedPostContract.PostEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                     // The columns for the WHERE clause
                selectionArgs,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        // Start reading the rows
        c.moveToFirst();
        long itemId = c.getLong(
                c.getColumnIndexOrThrow(QueuedPostContract.PostEntry._ID)
        );

        return itemId;
    }

    // TODO
    private void deletePost(int image, int distance, int description) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // TODO
        int rowId = 0;

        // Define 'where' part of query.
        String selection = QueuedPostContract.PostEntry.COLUMN_NAME_DISTANCE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(rowId)};
        // Issue SQL statement.
        db.delete( QueuedPostContract.PostEntry.TABLE_NAME, selection, selectionArgs);
    }



}
