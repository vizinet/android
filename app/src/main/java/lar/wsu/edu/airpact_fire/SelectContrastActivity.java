package lar.wsu.edu.airpact_fire;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class SelectContrastActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    // UI elements
    private ImageView mImageView, mBlackCircleColorView, mWhiteCircleColorView;
    private ImageButton mDoneButton, mRetakeButton, mBlackIndicatorButton, mWhiteIndicatorButton;

    ImageView mWhiteCircle, mBlackCircle, mCurrentCircle;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contrast);
        setTitle("Contrast Points");

        // Create UI elements
        mWhiteCircle = addNewCircle(R.drawable.abc_switch_thumb_material);
        mBlackCircle = addNewCircle(R.drawable.abc_scrubber_control_to_pressed_mtrl_005);
        mCurrentCircle = mWhiteCircle;

        // Set past indicator points, if any
        if (UserDataManager.getUserData(UserDataManager.getLastUser(), "lowIndicatorX") != null) {
            Toast.makeText(getApplicationContext(), "Indicator points remembered and placed.", Toast.LENGTH_SHORT).show();

            float lowIndicatorX = Float.parseFloat((String) UserDataManager.getUserData(UserDataManager.getLastUser(), "lowIndicatorX"));
            float lowIndicatorY = Float.parseFloat((String) UserDataManager.getUserData(UserDataManager.getLastUser(), "lowIndicatorY"));
            float highIndicatorX = Float.parseFloat((String) UserDataManager.getUserData(UserDataManager.getLastUser(), "highIndicatorX"));
            float highIndicatorY = Float.parseFloat((String) UserDataManager.getUserData(UserDataManager.getLastUser(), "highIndicatorY"));
            mWhiteCircle.setX(highIndicatorX);
            mWhiteCircle.setY(highIndicatorY);
            mBlackCircle.setX(lowIndicatorX);
            mBlackCircle.setY(lowIndicatorY);
        }
        // Set post time
        Post.Time = new Date();


        // Grab UI elements
        mImageView = (ImageView) findViewById(R.id.image_view);
        mWhiteCircleColorView = (ImageView) findViewById(R.id.white_circle_color);
        mBlackCircleColorView = (ImageView) findViewById(R.id.black_circle_color);

        mBlackIndicatorButton = (ImageButton) findViewById(R.id.black_indicator_button);
        mWhiteIndicatorButton = (ImageButton) findViewById(R.id.white_indicator_button);
        mDoneButton = (ImageButton) findViewById(R.id.done_button);
        mRetakeButton = (ImageButton) findViewById(R.id.retake_button);

        // Start off with taking picture
        takePicture();
//        Drawable ob = getResources().getDrawable(R.drawable.abc_list_longpressed_holo, getApplicationContext().getTheme());
//        mImageView.setBackground(ob);

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
            }
        });
        mWhiteIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCurrentIndicator = mWhiteIndicator;
                mCurrentCircle = mWhiteCircle;
            }
        });

        // Indicator placement listener
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // If point is within image
                if (Util.isPointInView(mImageView, (int) event.getX(), (int) event.getY())) {

                    // TODO Remove
//                    putPermCircle((int) mImageView.getX(), (int) mImageView.getY());
//                    putPermCircle((int) mImageView.getX() + mImageView.getWidth()
//                            , (int) mImageView.getY() + mImageView.getHeight());

                    // Get pixel we've touched
                    int selectedPixel = 220;//Util.getPixelAtPos(mImageView, (int) event.getX(), (int) event.getY());

                    //Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

                    //Toast.makeText(getApplicationContext(), "" + bitmap.getWidth(), Toast.LENGTH_SHORT).show();

                    //if (mCurrentIndicator == null) return false;

                    // Show current indicator
                    //mCurrentIndicator.setVisibility(View.VISIBLE);

                    // Update position
//                    mCurrentIndicator.setX(event.getX());
//                    mCurrentIndicator.setY(event.getY());

                    mCurrentCircle.setVisibility(View.VISIBLE);

                    mCurrentCircle.setX(event.getX());
                    mCurrentCircle.setY(event.getY());

                    if (mCurrentCircle == mBlackCircle) {

                        Post.LowXY = new float[] {event.getX(), event.getY()};
                        Post.LowColor = selectedPixel;

                        // Update visual patch
                        mBlackCircleColorView.setBackgroundColor(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));
                    }
                    else {

                        Post.HighXY = new float[] {event.getX(), event.getY()};
                        Post.HighColor = selectedPixel;

                        mWhiteCircleColorView.setBackgroundColor(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));
                    }

                    // TODO remove
                    //putPermCircle((int) event.getX(), (int) event.getY());

                    // Set post (x, y) for proper indicator
//                    if (mCurrentIndicator == mBlackIndicator) {
//                        Post.LowXY = new float[] {event.getX(), event.getY()};
//                        Post.LowColor = selectedPixel;
//                    }
//                    else {
//                        Post.HighXY = new float[] {event.getX(), event.getY()};
//                        Post.HighColor = selectedPixel;
//                    }

                    // Set indicator color to pixel color

                    mCurrentCircle.setColorFilter(Color.rgb(Color.red(selectedPixel), Color.green(selectedPixel), Color.blue(selectedPixel)));

                } else { // Hide indicators outside of bounds
//                    mCurrentIndicator.setVisibility(View.INVISIBLE);

                    mCurrentCircle.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        // Update data of last indicator points for user

        // TODO: For some reason, indicators not being placed

        UserDataManager.setUserData(UserDataManager.getLastUser(), "lowIndicatorX", (Float.toString(mBlackCircle.getX())));
        UserDataManager.setUserData(UserDataManager.getLastUser(), "lowIndicatorY", (Float.toString(mBlackCircle.getY())));

        UserDataManager.setUserData(UserDataManager.getLastUser(), "highIndicatorX", (Float.toString(mWhiteCircle.getX())));
        UserDataManager.setUserData(UserDataManager.getLastUser(), "highIndicatorY", (Float.toString(mWhiteCircle.getY())));

        Toast.makeText(getApplicationContext(), "Indicator points saved.", Toast.LENGTH_SHORT).show();

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
        circle.getLayoutParams().width = 25;
        circle.getLayoutParams().height = 25;

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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Add Bitmap to post
            Post.Image = imageBitmap;

            // Scale up the image size
            //int scaleUp = 1;
            //Bitmap resizedImageBitmap = getResizedBitmap(imageBitmap, imageBitmap.getWidth() * scaleUp, imageBitmap.getHeight() * scaleUp);

            // Turn Bitmap into Drawable, and set as ImageView background
            //Drawable ob = new BitmapDrawable(getResources(), imageBitmap);
            //mImageView.setBackground(ob);

            // Tell user to select contrast points afterwards
            Toast.makeText(this, "Select contrast points on image", Toast.LENGTH_LONG).show();

            mImageView.setImageBitmap(imageBitmap);
        }
    }


}
