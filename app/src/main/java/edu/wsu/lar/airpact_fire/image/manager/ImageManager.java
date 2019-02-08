// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.image.manager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;

import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.ui.activity.CaptureActivity;

/**
 * Handle image logic in UI.
 */
public class ImageManager {

    public static final int REQUEST_IMAGE_CAPTURE_CODE = 13;

    /**
     * Populate file in {@link ImageInterfaceObject} with pixel values from device camera.
     *
     * @param fragment              Calling fragment
     * @param imageInterfaceObject  Interface object to populate
     */
    public static void captureImage(Fragment fragment, ImageInterfaceObject imageInterfaceObject) {

        Activity activity = fragment.getActivity();

        Intent newTakePictureIntent = new Intent(activity, CaptureActivity.class);
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = imageInterfaceObject.createImageFile(storageDir);
        if (image == null) return;
        Uri imageUri = null;
        try {
            imageUri = FileProvider.getUriForFile(activity,
                    "com.example.android.fileprovider", image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Attach data for activity.
        newTakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image.getAbsolutePath());
        newTakePictureIntent.putExtra(
                MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        );

        fragment.startActivityForResult(newTakePictureIntent, REQUEST_IMAGE_CAPTURE_CODE);
    }

    /**
     * Perform corrective rotation on pixel values to account for camera manufacturer's defaults,
     * as specified within the EXIF tag in the image file metadata.
     *
     * e.g., a phone manufacturer may save an image captured from the device's camera in landscape,
     * or rotated 90 degrees in either direction.
     *
     * @param   imageInterfaceObject Image to correct
     * @return  Rotation-corrected bitmap
     */
    private static Bitmap correctiveRotation(Bitmap bitmap, String imageUri) {
        int rotate = 0;
        try {
            File imageFile = new File(imageUri);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Process provided image (wrapped within {@link ImageInterfaceObject}) by adjusting image
     * rotation.
     *
     * This method is meant to be used in a thread separate from the UI.
     *
     * @param activity
     * @param imageInterfaceObject
     * @param mainImageView
     * @param handler handles updates to UI thread
     * @return Adjusted Bitmap object
     */
    public static Bitmap processAndDisplayBitmap(Bitmap bitmap, String imageUri) {
        try {
            // Rotate image (if necessary).
            Bitmap rotatedBitmap = correctiveRotation(bitmap, imageUri);
            return rotatedBitmap;

//            // Resize bitmap for display (to screen proportions).
//            Display display = activity.getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//            int screenWidth = size.x;
//            int imageHeight = (int)(bitmap.getHeight() * (screenWidth / (float)bitmap.getWidth()));
//            int imageWidth = screenWidth;
//            // TODO?
//
//            Log.d("CameraTimer", ":: Start processing now. 3.");
//
//            // Log
//            Log.d("onActivityResult", String.format("screen width %d", screenWidth));
//            Log.d("onActivityResult", String.format("original display width, height: (%d, %d)",
//                    bitmap.getWidth(), bitmap.getHeight()));
//            Log.d("onActivityResult",
//                    String.format("resulting display width, height: (%d, %d)",
//                    imageWidth, imageHeight));


        } catch (Exception e) { }

        return null;
    }
}
