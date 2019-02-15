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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;

import java.io.File;

import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.ui.activity.CaptureActivity;

/**
 * Responsible for the processing of images in the app.
 *
 * This manager, along with {@link CaptureActivity}, handle the obtainment, processing, and
 * normalization of the images for the AIRPACT-Fire platform (ergo, lots of responsibility here!).
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
     * Resize bitmap for display to screen proportions.
     *
     * @param bitmap original bitmap
     * @return scaled-down bitmap
     */
    private static Bitmap scaleToScreen(Bitmap bitmap, Activity activity) {
        int[] screenDimensions = getScreenDimensions(activity);
        double scaleRatio = (screenDimensions[0] / (double)bitmap.getWidth());
        int imageHeight = (int)(bitmap.getHeight() * scaleRatio);
        int imageWidth = screenDimensions[0];
        return Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
    }

    /**
     * Get screen dimensions.
     *
     * @param activity
     * @return screen dimensions (width, height)
     */
    private static int[] getScreenDimensions(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[] { size.x, size.y };
    }

    /**
     * Crop a bitmap to precisely to screen dimension.
     *
     * @param bitmap bitmap to crop
     * @param activity
     * @return cropped image
     */
    private static Bitmap cropToScreen(Bitmap bitmap, Activity activity) {
        int[] screenDimensions = getScreenDimensions(activity);
        Log.d("cropToScreen", String.format("screenDim: %d, %d", screenDimensions[0], screenDimensions[1]));
        Log.d("cropToScreen", String.format("bitDim: %d, %d", bitmap.getWidth(), bitmap.getHeight()));
        return Bitmap.createBitmap(bitmap, 0, 0, screenDimensions[0], screenDimensions[1]);
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
     * @return <adjusted bitmap, scaled bitmap>
     */
    public static Bitmap[] processBitmap(Bitmap bitmap, Activity activity, String imageUri) {
        try {
            Bitmap rotatedBitmap = correctiveRotation(bitmap, imageUri);
            Bitmap scaledBitmap = scaleToScreen(rotatedBitmap, activity);
//            Bitmap croppedBitmap = cropToScreen(scaledBitmap, activity);
            return new Bitmap[] { rotatedBitmap, scaledBitmap };
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
