package lar.wsu.edu.airpact_fire;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Base64;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

// TODO: Find way to compress image and display in very short amount of time. We started having
//  problems with XML reading/writing when we stored whole image.
// TODO: Get x, y coordinates with respect to image and store those

public class SelectContrastActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    public static Uri imageUri;
    ImageView mWhiteCircle, mBlackCircle, mCurrentCircle;
    private boolean isFirstRun = true;
    // UI elements
    private ImageView mImageView; //, mBlackCircleColorView, mWhiteCircleColorView;
    private ImageButton mDoneButton, mRetakeButton;
    //private TextView mLowText, mHighText;
    private ImageView mBlackIndicatorButton, mWhiteIndicatorButton;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contrast);
        setTitle("Contrast Points");

        // Pulse animation
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);

        // Create UI elements
        mWhiteCircle = addNewCircle(R.drawable.white_dot);
        mBlackCircle = addNewCircle(R.drawable.black_dot);
        mCurrentCircle = mWhiteCircle;
        //mCurrentCircle.startAnimation(pulse);

        // Set past indicator points, if any
        if (UserDataManager.getUserData(UserDataManager.getRecentUser(), "lowX") != null) {
            //Toast.makeText(getApplicationContext(), "Indicator points remembered and placed", Toast.LENGTH_SHORT).show();

            float lowIndicatorX = Float.parseFloat(UserDataManager.getUserData(UserDataManager.getRecentUser(), "lowX"));
            float lowIndicatorY = Float.parseFloat(UserDataManager.getUserData(UserDataManager.getRecentUser(), "lowY"));
            float highIndicatorX = Float.parseFloat(UserDataManager.getUserData(UserDataManager.getRecentUser(), "highX"));
            float highIndicatorY = Float.parseFloat(UserDataManager.getUserData(UserDataManager.getRecentUser(), "highY"));
            mWhiteCircle.setX(highIndicatorX);
            mWhiteCircle.setY(highIndicatorY);
            mBlackCircle.setX(lowIndicatorX);
            mBlackCircle.setY(lowIndicatorY);
        }

        // Grab UI elements
        mImageView = (ImageView) findViewById(R.id.image_view);
        //mWhiteCircleColorView = (ImageView) findViewById(R.id.white_circle_color);
        //mBlackCircleColorView = (ImageView) findViewById(R.id.black_circle_color);

        mBlackIndicatorButton = (ImageView) findViewById(R.id.black_indicator_button);
        mWhiteIndicatorButton = (ImageView) findViewById(R.id.white_indicator_button);
        mDoneButton = (ImageButton) findViewById(R.id.done_button);
        mRetakeButton = (ImageButton) findViewById(R.id.retake_button);

        // Let user know which indicator is selected
        //mLowText.setTextColor(Color.GRAY);
        //mHighText.setTextColor(Color.RED);
        // TODO: Set an outline

        // Start off with taking picture
        takeAndSetPicture();

        // Button click listeners
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Now user adds picture details
                Intent intent = new Intent(getApplicationContext(), AddPictureDetailsActivity.class);
                startActivity(intent);
            }
        });
        mRetakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User takes another picture
                takeAndSetPicture();
            }
        });
        mBlackIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentCircle = mBlackCircle;
//                mLowText.setTextColor(Color.RED);
//                mHighText.setTextColor(Color.GRAY);
                // TODO
            }
        });
        mWhiteIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentCircle = mWhiteCircle;
//                mLowText.setTextColor(Color.GRAY);
//                mHighText.setTextColor(Color.RED);
                // TODO
            }
        });

        // Indicator placement listener
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // If point is within image
                if (Util.isPointInView(mImageView, (int) event.getX(), (int) event.getY())) {
                    // Get pixel we've touched
                    int selectedPixel = Util.getPixelAtPos(mImageView, (int) event.getX(), (int) event.getY()); // 230;

                    // Display and set circle position
                    mCurrentCircle.setVisibility(View.VISIBLE);
                    mCurrentCircle.setX(event.getX());
                    mCurrentCircle.setY(event.getY());

                    // Update visual patch
                    if (mCurrentCircle == mBlackCircle) {
                        //mBlackCircleColorView.setBackgroundColor(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));
                        mBlackIndicatorButton.setColorFilter(selectedPixel);
                    } else {
                        //mWhiteCircleColorView.setBackgroundColor(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));
                        // TODO
                        mWhiteIndicatorButton.setColorFilter(selectedPixel);
                    }

                    // Set indicator color to pixel color
                    mCurrentCircle.setColorFilter(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));

                } else { // Hide indicators outside of bounds
                    mCurrentCircle.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }
    @Override
    protected void onPause() {

//        // Don't do anything on first run
//        if (isFirstRun) {
//            isFirstRun = false;
//
//        } else {
        // Only call when we have a drawable
        if (mImageView.getDrawable() != null) {

            // Update data of last indicator points for user
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "lowX", (Float.toString(mBlackCircle.getX())));
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "lowY", (Float.toString(mBlackCircle.getY())));
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "highX", (Float.toString(mWhiteCircle.getX())));
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "highY", (Float.toString(mWhiteCircle.getY())));

            // Get color
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "highColor",
                    Integer.toString((Util.getPixelAtPos(mImageView, Math.round(mWhiteCircle.getX()), Math.round(mWhiteCircle.getY())))));
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "lowColor",
                    Integer.toString((Util.getPixelAtPos(mImageView, Math.round(mBlackCircle.getX()), Math.round(mBlackCircle.getY())))));

            //Toast.makeText(getApplicationContext(), "Indicator points and colors saved", Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }

    // Go home if back button pressed
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Util.goHome(this);
    }

    // Called when activity has focus
    // NOTE: We use this to set the bitmap to full dimensions of the image view, which we only
    //  have access to after onCreate is called.
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            Toast.makeText(SelectContrastActivity.this, "imageview width: " + mImageView.getWidth(), Toast.LENGTH_LONG).show();
            //ImageView img = (ImageView) findViewById(R.id.img);
        }

    }

    // Receives taken picture as thumbnail
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Call garbage collection
        Runtime.getRuntime().gc();

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Abort mission
            if (bitmap == null) handleImageFailure();

            // Resize bitmap to screen proportions
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;
            int imageHeight = (int) (bitmap.getHeight() * (screenWidth / (float) bitmap.getWidth()));
            int imageWidth = screenWidth;
            bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);

            // Add Bitmap to post in XML
            String imageString = Base64.encodeToString(Util.compressBitmap(bitmap), Base64.DEFAULT);
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "image", imageString);

            // Add placeholder geolocation
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "geoX", "-1");
            UserDataManager.setUserData(UserDataManager.getRecentUser(), "geoY", "-1");
            // Check for real deal
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Get last location
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // Set lat and long
                UserDataManager.setUserData(UserDataManager.getRecentUser(), "geoX", String.valueOf(loc.getLatitude()));
                UserDataManager.setUserData(UserDataManager.getRecentUser(), "geoY", String.valueOf(loc.getLongitude()));
            }

            // Tell user to select contrast points afterwards
            Toast.makeText(this, "Select high and low contrast points", Toast.LENGTH_LONG).show();

            // Set image view
            mImageView.setImageBitmap(bitmap);

        } else {
            // If no image take, go home
            Util.goHome(this);
        }
    }

    // Create, add, and return new circle
    public ImageView addNewCircle(int id) {
        ImageView circle = new ImageView(this);

        // Layout
        circle.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        // Setting image resource
        circle.setImageResource(id);

        // Width and height
        circle.getLayoutParams().width = 50;
        circle.getLayoutParams().height = 50;

        // Position
        circle.setX(0);
        circle.setY(0);

        // Add to activity view
        ContentFrameLayout parent = (ContentFrameLayout) findViewById(android.R.id.content);
        parent.addView(circle);

        return circle;
    }

    // Mainly a debug function to put black circle where I see fit
    public void putPermCircle(int x, int y) {
        ImageView circle = new ImageView(this);
        circle.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        // Setting image resource
        circle.setImageResource(R.drawable.logo);
        // Width and height
        circle.getLayoutParams().width = 25;
        circle.getLayoutParams().height = 25;

        circle.setX(x - circle.getWidth() / 2);
        circle.setY(y - circle.getHeight() / 2);

        ContentFrameLayout parent = (ContentFrameLayout) findViewById(android.R.id.content);
        parent.addView(circle);
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

    // Takes picture
    private void takeAndSetPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Util.createImageFile();
            } catch (IOException ex) {
                return;
            }

            imageUri = Uri.fromFile(photoFile);

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Display message and go home
    private void handleImageFailure() {
        Toast.makeText(SelectContrastActivity.this, "Failure retrieving image.", Toast.LENGTH_SHORT).show();
        Util.goHome(this);
    }
}
