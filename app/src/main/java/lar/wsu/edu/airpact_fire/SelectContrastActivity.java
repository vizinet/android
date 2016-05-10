package lar.wsu.edu.airpact_fire;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class SelectContrastActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    public static Uri imageUri;

    // UI elements
    private ImageView mImageView, mBlackCircleColorView, mWhiteCircleColorView;
    private ImageButton mDoneButton, mRetakeButton, mBlackIndicatorButton, mWhiteIndicatorButton;
    private TextView mLowText, mHighText;

    ImageView mWhiteCircle, mBlackCircle, mCurrentCircle;

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
        if (UserDataManager.getUserData(UserDataManager.getLastUser(), "lowX") != null) {
            //Toast.makeText(getApplicationContext(), "Indicator points remembered and placed", Toast.LENGTH_SHORT).show();

            float lowIndicatorX = Float.parseFloat(UserDataManager.getUserData(UserDataManager.getLastUser(), "lowX"));
            float lowIndicatorY = Float.parseFloat(UserDataManager.getUserData(UserDataManager.getLastUser(), "lowY"));
            float highIndicatorX = Float.parseFloat(UserDataManager.getUserData(UserDataManager.getLastUser(), "highX"));
            float highIndicatorY = Float.parseFloat(UserDataManager.getUserData(UserDataManager.getLastUser(), "highY"));
            mWhiteCircle.setX(highIndicatorX);
            mWhiteCircle.setY(highIndicatorY);
            mBlackCircle.setX(lowIndicatorX);
            mBlackCircle.setY(lowIndicatorY);
        }

        // Grab UI elements
        mImageView = (ImageView) findViewById(R.id.image_view);
        mWhiteCircleColorView = (ImageView) findViewById(R.id.white_circle_color);
        mBlackCircleColorView = (ImageView) findViewById(R.id.black_circle_color);

        mBlackIndicatorButton = (ImageButton) findViewById(R.id.black_indicator_button);
        mWhiteIndicatorButton = (ImageButton) findViewById(R.id.white_indicator_button);
        mDoneButton = (ImageButton) findViewById(R.id.done_button);
        mRetakeButton = (ImageButton) findViewById(R.id.retake_button);

        mLowText = (TextView) findViewById(R.id.low_text);
        mHighText = (TextView) findViewById(R.id.high_text);

        // Let user know which indicator is selected
        mLowText.setTextColor(Color.GRAY);
        mHighText.setTextColor(Color.RED);

        // Start off with taking picture
        takePicture();

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
                takePicture();
            }
        });
        mBlackIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCurrentIndicator = mBlackIndicator
                mCurrentCircle = mBlackCircle;
                mLowText.setTextColor(Color.RED);
                mHighText.setTextColor(Color.GRAY);
            }
        });
        mWhiteIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCurrentIndicator = mWhiteIndicator;
                mCurrentCircle = mWhiteCircle;
                mLowText.setTextColor(Color.GRAY);
                mHighText.setTextColor(Color.RED);
            }
        });

        // Indicator placement listener
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // If point is within image
                if (Util.isPointInView(mImageView, (int) event.getX(), (int) event.getY())) {
                    // Get pixel we've touched
                    int selectedPixel = 230; //Util.getPixelAtPos(mImageView, (int) event.getX(), (int) event.getY());

                    // TODO: Problem seems to be not with invalid x and y coordinates, but for bitmap itself? Can't call .getWidth and stuff...

                    // Toast.makeText(getApplicationContext(), ((BitmapDrawable) mImageView.getDrawable()).getBitmap().getPixel(10, 10), Toast.LENGTH_LONG).show();

                    mCurrentCircle.setVisibility(View.VISIBLE);

                    mCurrentCircle.setX(event.getX());
                    mCurrentCircle.setY(event.getY());

                    if (mCurrentCircle == mBlackCircle) {

                        // Update visual patch
                        mBlackCircleColorView.setBackgroundColor(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));
                    } else {

                        mWhiteCircleColorView.setBackgroundColor(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));
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
        // Update data of last indicator points for user
        UserDataManager.setUserData(UserDataManager.getLastUser(), "lowX", (Float.toString(mBlackCircle.getX())));
        UserDataManager.setUserData(UserDataManager.getLastUser(), "lowY", (Float.toString(mBlackCircle.getY())));
        UserDataManager.setUserData(UserDataManager.getLastUser(), "highX", (Float.toString(mWhiteCircle.getX())));
        UserDataManager.setUserData(UserDataManager.getLastUser(), "highY", (Float.toString(mWhiteCircle.getY())));

        // Get color
        UserDataManager.setUserData(UserDataManager.getLastUser(), "highColor",
                Integer.toString(((ColorDrawable) mWhiteCircleColorView.getBackground()).getColor()));
        UserDataManager.setUserData(UserDataManager.getLastUser(), "lowColor",
                Integer.toString(((ColorDrawable) mBlackCircleColorView.getBackground()).getColor()));

        //Toast.makeText(getApplicationContext(), "Indicator points and colors saved", Toast.LENGTH_SHORT).show();

        super.onPause();
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
    private void takePicture() {
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

            //Toast.makeText(this, "Saved at " + imageLocation, Toast.LENGTH_LONG).show();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Receives taken picture as thumbnail
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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

            // Scale it downnn
            bitmap = Bitmap.createScaledBitmap(bitmap, 175, 200, true);

            // Add Bitmap to post in XML
            String imageString = Base64.encodeToString(Util.getBytesFromBitmap(bitmap), Base64.DEFAULT);
            UserDataManager.setUserData(UserDataManager.getLastUser(), "image", imageString);

            // Add placeholder geolocation
            UserDataManager.setUserData(UserDataManager.getLastUser(), "geoX", "-1");
            UserDataManager.setUserData(UserDataManager.getLastUser(), "geoY", "-1");
            // Check for real deal
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Get last location
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // Set lat and long
                UserDataManager.setUserData(UserDataManager.getLastUser(), "geoX", String.valueOf(loc.getLatitude()));
                UserDataManager.setUserData(UserDataManager.getLastUser(), "geoY", String.valueOf(loc.getLongitude()));
            }

            /*
            UserDataManager.setUserData(UserDataManager.getLastUser(), "geoX", "0");
            UserDataManager.setUserData(UserDataManager.getLastUser(), "geoY", "0");
            try {
                ExifInterface exifInterface = new ExifInterface(imageUri.toString());
                if (exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) != null) {
                    UserDataManager.setUserData(UserDataManager.getLastUser(), "geoX",
                            exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                    UserDataManager.setUserData(UserDataManager.getLastUser(), "geoY",
                            exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            */

            // Tell user to select contrast points afterwards
            Toast.makeText(this, "Select high and low contrast points", Toast.LENGTH_LONG).show();

            // Set image view
            mImageView.setImageBitmap(bitmap);
        }
    }


}
