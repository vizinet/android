package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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

/* @Todo ---------------------------------------
 *      - Save data as JSON (let web team know structure)
 *      - Send data to server (web team )
  ---------------------------------------------*/

// Acting as the ImageCaptureActivity
public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    ImageView mImageView;
    Button mCameraButton, mUploadButton;
    EditText mEditText;
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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Attach views to variables
        mImageView = (ImageView) findViewById(R.id.captured_image_thumbnail);
        mCameraButton = (Button) findViewById(R.id.capture_image_button);
        mUploadButton = (Button) findViewById(R.id.upload_data_button);
        mEditText = (EditText) findViewById(R.id.description_edit_text);
        mDebugText = (TextView) findViewById(R.id.debug_text_display);

        // Create button event listeners
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadData();
            }
        });
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
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
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

    // Receives taken picture as thumbnail
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);

            mDebugText.setText("Captured and saved image as '" + mCurrentPhotoPath + "'");
        }
    }

    // Upload JSON data to server
    private void uploadData()
    {
        //lets get our picture again because that's fun
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

        // now convert the bitmap into a string
        String i = "";
        if(bitmap != null) {
            i = Base64.encodeToString(getBytesFromBitmap(bitmap), Base64.DEFAULT);
        }

        // creates our async network manager and sends the data off to be packaged
        NetworkManager nwork = new NetworkManager();
        nwork.execute(mServerURL, mUser,mEditText.getText().toString(), i);

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