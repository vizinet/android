package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* @Todo ---------------------------------------
 *      - Setup local server
 *      - Connect server with dummy credentials
 *      - Send text to server
 *      - Server stores text
 *      - Send image data as bytes
  ---------------------------------------------*/

// Acting as the ImageCaptureActivity
public class MainActivity extends AppCompatActivity {
    ImageView mImageView;
    Button mCameraButton, mUploadButton;
    EditText mEditText;
    TextView mDebugText;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);

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
        packageDataToJSON();
        // TODO
        mDebugText.append("\nPackaging up data in JSON and uploading to server.");
    }

    // Packages up all data into JSON object
    private void packageDataToJSON()
    {
        // TODO
    }
}
