// Copyright © 2018,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.image.manager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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

    private static int sRotationDegree = 90;
    private static final int sRequestTakePhoto = 1;

    public static final int REQUEST_IMAGE_CAPTURE_CODE = 13;

    /**
     * TODO: Deprecate this
     */
    private static class ImageRotateTask extends AsyncTask<Void, Void, Void> {

        private Activity mActivity;
        private ProgressDialog mProgressDialog;
        private Bitmap mBitmap;
        private ImageView mImageView;
        private ImageInterfaceObject mImageInterfaceObject;

        public ImageRotateTask(Activity activity, ImageInterfaceObject imageInterfaceObject,
                               ImageView imageView) {
            mActivity = activity;
            mProgressDialog = ProgressDialog.show(mActivity, "Rotating Image",
                    "Processing. Please wait...", true);
            mImageView = imageView;
            mImageInterfaceObject = imageInterfaceObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
            mBitmap = mImageInterfaceObject.getBitmap();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Rotate bitmap 90 degrees
                Matrix matrix = new Matrix();
                matrix.postRotate(sRotationDegree);
                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                        mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Save rotated bitmap and display
            mImageInterfaceObject.setImage(mBitmap);
            adjustAndDisplayBitmap(mActivity, mImageInterfaceObject, mImageView);

            mProgressDialog.hide();
        }
    }

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

        // Attach data for activity
//        newTakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri.toString());
        newTakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image.getAbsolutePath());
        newTakePictureIntent.putExtra(
                MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        );

        fragment.startActivityForResult(newTakePictureIntent, REQUEST_IMAGE_CAPTURE_CODE);

        /*
        // Ensure that there's a camera activity to handle the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {

            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = imageInterfaceObject.createImageFile(storageDir);

            if (image == null) return;

            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Uri imageUri = null;
            try {
                imageUri = FileProvider.getUriForFile(activity,
                        "com.example.android.fileprovider", image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Make sure we get file back, and enforce PORTRAIT camera mode
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            takePictureIntent.putExtra(
                    MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            );

            fragment.startActivityForResult(takePictureIntent, sRequestTakePhoto);
        }
        */
    }

    /**
     *
     * @param activity
     * @param imageInterfaceObject
     */
    public static void rotate(Activity activity, ImageInterfaceObject imageInterfaceObject,
                              ImageView imageView) {
        new ImageRotateTask(activity, imageInterfaceObject, imageView).execute();
    }

    /**
     * Perform corrective rotation on pixel values to correct for camera manufacturer's defaults,
     * as specified within the EXIF tag in the image file metadata.
     *
     * e.g., a phone manufacturer may save an image captured from the device's camera in landscape,
     * or rotated 90 degrees in either direction.
     *
     * @param   imageInterfaceObject Image to correct
     * @return  Rotation-corrected bitmap
     */
    private static Bitmap correctiveRotation(ImageInterfaceObject imageInterfaceObject) {
        int rotate = 0;
        Bitmap bitmap = imageInterfaceObject.getBitmap();
        try {
            String imageUri = imageInterfaceObject.getImageFile().getAbsolutePath();
            File imageFile = new File(imageUri);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
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
            imageInterfaceObject.setImage(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     *
     * @param activity
     * @param imageInterfaceObject
     * @param mainImageView
     * @return Adjusted Bitmap object
     */
    public static Bitmap adjustAndDisplayBitmap(Activity activity,
                                              ImageInterfaceObject imageInterfaceObject,
                                              ImageView mainImageView) {
        try {
            Bitmap bitmap = correctiveRotation(imageInterfaceObject);

            // Resize bitmap for display (to screen proportions)
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;
            int imageHeight = (int)(bitmap.getHeight() * (screenWidth / (float)bitmap.getWidth()));
            int imageWidth = screenWidth;

            // Log
            Log.d("onActivityResult", String.format("screen width %d", screenWidth));
            Log.d("onActivityResult", String.format("original display width, height: (%d, %d)",
                    bitmap.getWidth(), bitmap.getHeight()));
            Log.d("onActivityResult", String.format("resulting display width, height: (%d, %d)",
                    imageWidth, imageHeight));

            // Update UI
            // TODO: Observe if scaling down the Bitmap does anything
//            bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);
//            mainImageView.setImageBitmap(bitmap);
            mainImageView.setImageBitmap(imageInterfaceObject.getThumbnail());
            return bitmap;

        } catch (Exception e) { }

        return null;
    }
}
