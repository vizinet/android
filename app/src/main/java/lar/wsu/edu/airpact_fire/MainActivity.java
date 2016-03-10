package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

// Acting as the ImageCaptureActivity
public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    // false = black, true = white
    boolean colorMode;

    ImageView mImageView, mBlackDotView, mWhiteDotView, mHighColorPatchView, mLowColorPatchView;
    ImageButton mDotModeImageButton;
    Button mCameraButton, mUploadButton;
    EditText mEditText, mUserEditText;
    TextView mDebugText;
    String mCurrentPhotoPath;
    // [Not real server URL]
    String mServerURL = "http://76.178.152.115:8000/file_upload/upload";
    String mUser = "root";

    // Temp method for sending http post request to server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set action bar stuff
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.logo);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Set color select mode
        colorMode = false;

        // Attach views to variables
        mImageView = (ImageView) findViewById(R.id.captured_image_thumbnail);
        mHighColorPatchView = (ImageView) findViewById(R.id.high_color_patch_view);
        mLowColorPatchView = (ImageView) findViewById(R.id.low_color_patch_view);
        mDotModeImageButton = (ImageButton) findViewById(R.id.dot_mode_image_button);
        mCameraButton = (Button) findViewById(R.id.capture_image_button);
        mUploadButton = (Button) findViewById(R.id.upload_data_button);
        mEditText = (EditText) findViewById(R.id.description_edit_text);
        mUserEditText = (EditText) findViewById(R.id.user_edit_text);
        mDebugText = (TextView) findViewById(R.id.debug_text_display);

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
        mBlackDotView.generateViewId();
        mBlackDotView.setId(R.id.black_dot_view);

        // Same white dot stuff
        mWhiteDotView = new ImageView(this);
        mWhiteDotView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mWhiteDotView.setImageResource(R.drawable.white_dot);
        mWhiteDotView.getLayoutParams().width = 50;
        mWhiteDotView.getLayoutParams().height = 50;
        mWhiteDotView.generateViewId();
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
                // TODO: Don't let screen rotate
                // TODO: Better thumbnail view
                // TODO: Don't let dots outside of Bitmap area
                // TODO: Fix black dot placement
                // TODO: Fix this terrible inefficient, last-minute code

                // NOTE: ImageView is set to wrap_content, so the width of ImageView is the
                // width of the encapsulated BitMap object.

                // Don't go any further, unless we've taken a picture
                if (mCurrentPhotoPath == null) { return true; }

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
                if (isInView(mImageView, (int) event.getX(), (int) event.getY())) {

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

    // Let us know if we are within a view
    public boolean isInView(View view, int x, int y) {
        return (x > view.getX())
                && (x <  (view.getX() + view.getWidth()))
                && (y > view.getY())
                && (y < (view.getY() + view.getHeight()));
    }

    // Gets pixel color inside mImageView given x and y coordinates
    // TODO Might not be so easy as these simple x's and y's -- absolute or relative?
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

    // Create an image file with collision resistant title to public directory
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = new File(storageDir, imageFileName + ".jpg");

        /*
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        */

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();//"file:" + image.getAbsolutePath();
        return image;
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
                // TODO For some reason, the below line was causing "data" to be null in our onActivityResult method
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
            Bitmap resizedImageBitmap = getResizedBitmap(imageBitmap, imageBitmap.getWidth() * scaleUp, imageBitmap.getHeight() * scaleUp);

            mImageView.setImageBitmap(resizedImageBitmap);

            mDebugText.setText("Captured and saved image as '" + mCurrentPhotoPath + "'");
        }
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

        Bitmap bitmap = null;
        String i = "";
        if (mCurrentPhotoPath != null)
        {
            // Convert full image to Bitmap to String
            File f = new File(mCurrentPhotoPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(bitmap != null) {
                i = Base64.encodeToString(getBytesFromBitmap(bitmap), Base64.DEFAULT);
            }
        }

        // creates our async network manager and sends the data off to be packaged
        NetworkManager nwork = new NetworkManager();
        // TODO: Update below line to make better
        String user = mUserEditText.getText().toString();
        nwork.execute(mServerURL, user, mEditText.getText().toString(), i);

    }

    class NetworkManager extends AsyncTask <String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... args )
        {
            try {
                //constants
                URL url = new URL(args[0]);
                JSONObject J = new JSONObject();
                J.put("user", args[1]);
                J.put("description", args[2]);
                J.put("image", args[3]);
                String message = J.toString();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /*milliseconds*/);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(message.getBytes().length);

                //make some HTTP headers
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                //open
                conn.connect();

                //setup send
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(message.getBytes());
                //clean up
                os.flush();

                // if server needs to talk back...put it here as an input stream from the conn

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }
}