package edu.wsu.lar.airpact_fire.activity;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import edu.wsu.lar.airpact_fire.data.manager.AppDataManager;
import edu.wsu.lar.airpact_fire.data.Post;
import lar.wsu.edu.airpact_fire.R;
import edu.wsu.lar.airpact_fire.util.Util;

// TODO: Find way to compress image and display in very short amount of time. We started having
//  problems with XML reading/writing when we stored whole image.
// TODO: Get x, y coordinates with respect to image and store those

public class TargetSelectActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    public static Uri imageUri;

    private FrameLayout mNavBar;
    private LinearLayout mButtonPanel;
    private FrameLayout mIndicatorPanel;
    private LinearLayout mRightButtonPanel;
    private LinearLayout mSelectionPanel;
    private LinearLayout mTabLabelPanel;

    private TextView mVisualRangeInput;
    private TextView mSelectionPanelText;
    private ImageView mWhiteCircle, mBlackCircle, mCurrentCircle;
    private ImageView mImageView;
    private Button mDoneButton, mRetakeButton;
    private ImageView mBlackIndicatorButton, mWhiteIndicatorButton;
    private Spinner mMetricSelectSpinner;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_target);
        Util.setupSecondaryNavBar(this, HomeActivity.class, "SELECT TARGETS");

        // Create UI elements
        mWhiteCircle = addNewIndicator(R.color.schemeWhite);
        mBlackCircle = addNewIndicator(R.color.schemeDark);
        mCurrentCircle = mWhiteCircle;

        // Grab UI elements
        mNavBar = (FrameLayout) findViewById(R.id.navbar);
        mButtonPanel = (LinearLayout) findViewById(R.id.button_panel);
        mIndicatorPanel = (FrameLayout) findViewById(R.id.indicator_panel);
        mRightButtonPanel = (LinearLayout) findViewById(R.id.right_button_panel);
        mSelectionPanel = (LinearLayout) findViewById(R.id.selection_panel);
        mTabLabelPanel = (LinearLayout) findViewById(R.id.tab_label_panel);
        // ...
        mSelectionPanelText = (TextView) findViewById(R.id.selection_panel_text);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mBlackIndicatorButton = (ImageView) findViewById(R.id.black_indicator_button);
        mWhiteIndicatorButton = (ImageView) findViewById(R.id.white_indicator_button);
        mDoneButton = (Button) findViewById(R.id.done_button);
        mRetakeButton = (Button) findViewById(R.id.retake_button);
        mMetricSelectSpinner = (Spinner) findViewById(R.id.metric_select_spinner);
        mVisualRangeInput = (TextView) findViewById(R.id.visual_range_input);

        // Distance metric input (spinner stuff)
        final List<String> metricOptions = new ArrayList<>();
        metricOptions.add("Miles");
        metricOptions.add("Kilometers");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_text, metricOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMetricSelectSpinner.setAdapter(adapter);
        // TODO: Grab user preference on metric and correspond to integer
        mMetricSelectSpinner.setSelection(0);

        // Set current button (white)
        // TODO: Create setupIndicatorSwatches(...)
        mWhiteIndicatorButton.setBackground(getResources().getDrawable(R.drawable.indicator_border));

        // Verify camera permissions and take picture
        Util.verifyStoragePermissions(TargetSelectActivity.this);
        takeAndSetPicture();

        // Navigation buttons
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if distance field is complete
                if (!areFieldsCompleted()) {

                    // Message and highlight error
                    Toast.makeText(TargetSelectActivity.this, "Please enter distance from low-color point in the captured scene.", Toast.LENGTH_LONG).show();

                    int colorFrom = ContextCompat.getColor(TargetSelectActivity.this, R.color.schemeTransparent);
                    int colorTo = ContextCompat.getColor(TargetSelectActivity.this, R.color.schemeFailure);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorTo, colorFrom);
                    colorAnimation.setDuration(1000); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            mRightButtonPanel.setBackgroundColor((int) animator.getAnimatedValue());
                        }
                    });
                    colorAnimation.start();

                    // Break
                    return;
                }

                // Save distance and metric choice
                // TODO: Update visual ranges
                AppDataManager.setUserData("visualRangeOne", mVisualRangeInput.getText().toString());
                AppDataManager.setUserData("visualRangeTwo", mVisualRangeInput.getText().toString());
                String selectedMetric = metricOptions.get(mMetricSelectSpinner.getSelectedItemPosition()).toLowerCase();
                Toast.makeText(TargetSelectActivity.this, "Selected metric = " + selectedMetric, Toast.LENGTH_LONG).show();
                AppDataManager.setUserData("distanceUnits", selectedMetric);

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
        // Indicator panel buttons
        mBlackIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear past circle
                mWhiteIndicatorButton.setBackgroundResource(0);
                mCurrentCircle = mBlackCircle;
                // Set new circle background
                mBlackIndicatorButton.setBackground(getResources().getDrawable(R.drawable.indicator_border));

                animateIndicator(mCurrentCircle);
            }
        });
        mWhiteIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlackIndicatorButton.setBackgroundResource(0);
                mCurrentCircle = mWhiteCircle;
                mWhiteIndicatorButton.setBackground(getResources().getDrawable(R.drawable.indicator_border));

                // Animate indicator
                animateIndicator(mCurrentCircle);
            }
        });
        // Metric selection (i.e. "kilometers" or "miles?")
        mMetricSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mMetricSelectSpinner.setSelection(position);
                // TODO: Update metric preference
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Don't update
            }

        });

        // Indicator placement listener
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Handle displays
                if (event.getAction() == MotionEvent.ACTION_UP) showDisplays();
                else if (event.getAction() == MotionEvent.ACTION_DOWN) hideDisplays();

                // Update UI to indicator movement
                onIndicatorMoved(mCurrentCircle, (int) event.getX(), (int) event.getY());

                return true;
            }
        });
    }

    // Check if user has filled out fields necessary
    // TODO: Same thing with next activity
    private boolean areFieldsCompleted() {
        String visualRangeText = mVisualRangeInput.getText().toString();
        if (visualRangeText == null || visualRangeText.length() == 0) return false;
        return true;
    }

    // Checks if coordinates are valid. If so, set them
    // Gets color at that point
    // Updates color panels
    private void onIndicatorMoved(ImageView indicator, int x, int y) {
        // If point is within image
        if (Util.isPointInView(mImageView, x, y)) {
            // Get pixel we've touched
            int selectedPixel = Util.getPixelAtPos(mImageView, x, y);

            // Display and set circle position
            int circleX = Math.round(x) - (indicator.getWidth() / 2);
            int circleY = Math.round(y) - (indicator.getHeight() / 2);
            indicator.setX(circleX);
            indicator.setY(circleY);

            // Update indicator and selection panels
            if (indicator == mBlackCircle) {
                mBlackIndicatorButton.setColorFilter(selectedPixel);
                mSelectionPanelText.setText("Selecting low target...");
            } else {
                mWhiteIndicatorButton.setColorFilter(selectedPixel);
                mSelectionPanelText.setText("Selecting high target...");
            }
            mSelectionPanel.setBackgroundColor(selectedPixel);
            // Set text according to its background
            double luminance = 0.2126 * Color.red(selectedPixel) +
                    0.7152 * Color.green(selectedPixel) +
                    0.0722 * Color.blue(selectedPixel);
            int textColor = luminance < 128 ? Color.WHITE : Color.BLACK;
            mSelectionPanelText.setTextColor(textColor);

            // Set indicator color to pixel color
            // NOTE: When I do .setTint(...), it seems to affect the drawable itself and thus any View which uses it
            indicator.getBackground().setColorFilter(selectedPixel, PorterDuff.Mode.MULTIPLY);

        } else {
            // Do nothing
        }
    }

    // Save fields
    @Override
    protected void onPause() {

        // Only call when we have a drawable
        if (mImageView.getDrawable() != null) {

            // Get actual image dimensions
            Drawable imageDrawable = mImageView.getDrawable();
            Rect imageBounds = imageDrawable.getBounds();
            int imageWidth = imageBounds.width(), imageHeight = imageBounds.height();
            float lowRelativeX = imageWidth * (mBlackCircle.getX() / Util.getScreenWidth(this));
            float lowRelativeY = imageHeight * (mBlackCircle.getY() / Util.getScreenHeight(this));
            float highRelativeX = imageWidth * (mWhiteCircle.getX() / Util.getScreenWidth(this));
            float highRelativeY = imageHeight * (mWhiteCircle.getY() / Util.getScreenHeight(this));

            // Update data of last indicator points for user
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "lowX", (Float.toString(lowRelativeX)));
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "lowY", (Float.toString(lowRelativeY)));
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "highX", (Float.toString(highRelativeX)));
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "highY", (Float.toString(highRelativeY)));

            Log.println(Log.DEBUG, "POSITIONING", "lowX: " + lowRelativeX + "lowY: " + lowRelativeY);

            // Get color
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "highColor",
                    Integer.toString((Util.getPixelAtPos(mImageView, Math.round(mWhiteCircle.getX()), Math.round(mWhiteCircle.getY())))));
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "lowColor",
                    Integer.toString((Util.getPixelAtPos(mImageView, Math.round(mBlackCircle.getX()), Math.round(mBlackCircle.getY())))));

        }
        super.onPause();
    }

    // Called when activity has focus
    // NOTE: We use this to set the bitmap to full dimensions of the image view, which we only
    //  have access to after onCreate is called.
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            // Only setup indicators when window has focus, because that's when ImageView gets inflated
            setupIndicators();

            Log.println(Log.ERROR, "onWindowFocusChanged", "mImageView.getWidth(): " + mImageView.getWidth());
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
            if (bitmap == null) {
                handleImageFailure();
                return;
            }

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
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "image", imageString);

            // Add placeholder geolocation (Pullman, WA)
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "geoX", String.valueOf(Post.GPS_DEFAULT_LOC[0]));
            AppDataManager.setUserData(AppDataManager.getRecentUser(), "geoY", String.valueOf(Post.GPS_DEFAULT_LOC[1]));
            // Check for real deal
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Get last location
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // Set lat and long
                AppDataManager.setUserData(AppDataManager.getRecentUser(), "geoX", String.valueOf(loc.getLatitude()));
                AppDataManager.setUserData(AppDataManager.getRecentUser(), "geoY", String.valueOf(loc.getLongitude()));
            }

            // Set image view
            mImageView.setImageBitmap(bitmap);

        } else {
            // If no image take, go home
            Util.goHome(this);
        }
    }

    // Make indicator get bigger and smaller smoothly
    private void animateIndicator(final ImageView indicator) {
        final int sizeFrom = indicator.getWidth();
        final int sizeTo = sizeFrom + 10;
        final int originalX = (int) indicator.getX();
        final int originalY = (int) indicator.getY();
        ValueAnimator sizeAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), sizeFrom, sizeTo, sizeFrom);
        sizeAnimation.setDuration(500); // milliseconds
        sizeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedValue = (int) animator.getAnimatedValue();
                int animateDisplacement = (int) (0.5 * (animatedValue - sizeFrom));

                indicator.setX(originalX - animateDisplacement);
                indicator.setY(originalY - animateDisplacement);
                indicator.getLayoutParams().height = indicator.getLayoutParams().width = animatedValue;
                indicator.requestLayout(); // Makes layout do changes instantly
            }
        });
        sizeAnimation.start();
    }

    // Hide displays from view while image is being touched
    public void hideDisplays() {
        mNavBar.setVisibility(View.INVISIBLE);
        mButtonPanel.setVisibility(View.INVISIBLE);
        mIndicatorPanel.setVisibility(View.INVISIBLE);
        mTabLabelPanel.setVisibility(View.INVISIBLE);

        // But show selection panel!
        mSelectionPanel.setVisibility(View.VISIBLE);
    }

    public void showDisplays() {
        mNavBar.setVisibility(View.VISIBLE);
        mButtonPanel.setVisibility(View.VISIBLE);
        mIndicatorPanel.setVisibility(View.VISIBLE);
        mTabLabelPanel.setVisibility(View.VISIBLE);

        // Flash visual range patch if low indicator was just set
        if (mCurrentCircle == mBlackCircle) {
            // Use animation
            int colorFrom = ContextCompat.getColor(this, R.color.schemeTransparent);
            int colorTo = ContextCompat.getColor(this, R.color.schemeWhite);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorTo, colorFrom);
            colorAnimation.setDuration(2000); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mRightButtonPanel.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        }
        // ...
        mSelectionPanel.setVisibility(View.INVISIBLE);
    }

    // Create, add, and return new circle
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageView addNewIndicator(int colorId) {
        ImageView circle = new ImageView(this);

        // Layout
        circle.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        // Setting image resource
        //circle.setImageResource(id);
        circle.setImageResource(R.drawable.indicator_point);

        Drawable outline = ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_border);
        outline.setTint(colorId);
        circle.setBackground(outline);

        // Set background resource that changes with color of selected
        circle.setBackground(getResources().getDrawable(R.drawable.indicator_border));

        // Width and height
        circle.getLayoutParams().width = 50;
        circle.getLayoutParams().height = 50;

        // Make below panels but above image
        circle.setTranslationZ(15);

        // Position
        circle.setX(0);
        circle.setY(0);

        // Add to activity view
        ContentFrameLayout parent = (ContentFrameLayout) findViewById(android.R.id.content);
        parent.addView(circle);

        return circle;
    }

    // Set initial indicator positions and colors
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setupIndicators() {

        float lowIndicatorX;
        float lowIndicatorY;
        float highIndicatorX;
        float highIndicatorY;

        // Set indicator coordinates
        if (AppDataManager.getUserData(AppDataManager.getRecentUser(), "lowX") != null) {

            // Get past coordinates
            lowIndicatorX = Float.parseFloat(AppDataManager.getUserData(AppDataManager.getRecentUser(), "lowX"));
            lowIndicatorX = Float.parseFloat(AppDataManager.getUserData(AppDataManager.getRecentUser(), "lowX"));
            lowIndicatorY = Float.parseFloat(AppDataManager.getUserData(AppDataManager.getRecentUser(), "lowY"));
            highIndicatorX = Float.parseFloat(AppDataManager.getUserData(AppDataManager.getRecentUser(), "highX"));
            highIndicatorY = Float.parseFloat(AppDataManager.getUserData(AppDataManager.getRecentUser(), "highY"));

        } else {

            // Place indicators at center of image (in upper- and lower-quadrant)
            lowIndicatorX = (mImageView.getWidth() / 2);
            lowIndicatorY = (mImageView.getHeight() / 2) + (mImageView.getHeight() / 8);
            highIndicatorX = (mImageView.getWidth() / 2);
            highIndicatorY = (mImageView.getWidth() / 2) - (mImageView.getHeight() / 8);
        }

        // Initial indicator update
        onIndicatorMoved(mWhiteCircle, (int) highIndicatorX, (int) highIndicatorY);
        onIndicatorMoved(mBlackCircle, (int) lowIndicatorX, (int) lowIndicatorY);
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
                // Make sure we get file back, and enforce PORTRAIT camera mode
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Display message and go home
    private void handleImageFailure() {
        Toast.makeText(TargetSelectActivity.this, "Failure retrieving image.", Toast.LENGTH_SHORT).show();
        Util.goHome(this);
    }
}
