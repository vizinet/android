package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectContrastActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contrast);
        setTitle("Select Contrast Points");

        // Grab UI elements
        mImageView = (ImageView) findViewById(R.id.image_view);

        // Start off with taking picture
        dispatchTakePictureIntent();

        // TODO: Prevent from looping and constantly opening the above function call
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
//                mEditText.setText("Error occurred while creating the file.");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // TODO: For some reason, the below line was causing "data" to be null in our onActivityResult method
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

            // Scale up the image size
            int scaleUp = 4;
            //Bitmap resizedImageBitmap = getResizedBitmap(imageBitmap, imageBitmap.getWidth() * scaleUp, imageBitmap.getHeight() * scaleUp);

            mImageView.setImageBitmap(imageBitmap);

            //mDebugText.setText("Captured and saved image as '" + mCurrentPhotoPath + "'");
        }
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
        //mCurrentPhotoPath = image.getAbsolutePath();//"file:" + image.getAbsolutePath();
        return image;
    }
}
