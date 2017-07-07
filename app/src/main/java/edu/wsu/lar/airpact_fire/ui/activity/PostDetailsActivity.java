package edu.wsu.lar.airpact_fire.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import lar.wsu.edu.airpact_fire.R;
import edu.wsu.lar.airpact_fire.util.Util;

public class PostDetailsActivity extends AppCompatActivity {

    private RelativeLayout mPageLayout;

    private TextView mPostStatusText, mLocationText, mTimeText, mDescriptionText, mVisualRangeText, mGpsText;
    private Button mViewImageButton, mSubmitPostButton, mDeletePostButton;
    private ImageView mLowColorImage, mHighColorImage;

    private String mPostId;
    //private Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // NOTE: Sending extra as string works, but not as int
        // Get post id passed from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mPostId = null;
            } else {
                mPostId = extras.getString("POST_ID");
            }
        } else {
            mPostId = (String) savedInstanceState.getSerializable("POST_ID");
        }

        // Set appbar title
        Util.setupSecondaryNavBar(this, GalleryActivity.class, "POST #" + mPostId);

        // Get post from post id
        /*
        mPost = PostDataManager.getPost(this, Long.parseLong(mPostId));
        Post.Activity = PostDetailsActivity.this;
        Post.Context = getApplicationContext();
        */

        // UI
        mPageLayout = (RelativeLayout) findViewById(R.id.page);
        mPostStatusText = (TextView) findViewById(R.id.post_status_text);
        mDescriptionText = (TextView) findViewById(R.id.description_text);
        mLocationText = (TextView) findViewById(R.id.location_text);
        mVisualRangeText = (TextView) findViewById(R.id.visual_range_text);
        mTimeText = (TextView) findViewById(R.id.date_time_text);
        mGpsText = (TextView) findViewById(R.id.gps_text);
        mViewImageButton = (Button) findViewById(R.id.view_image_button);
        mSubmitPostButton = (Button) findViewById(R.id.submit_post_button);
        mDeletePostButton = (Button) findViewById(R.id.delete_post_button);
        mLowColorImage = (ImageView) findViewById(R.id.low_color_image);
        mHighColorImage = (ImageView) findViewById(R.id.high_color_image);

        // Set background
        /*
        Bitmap image = Util.stringToBitMap(mPost.Image);
        BitmapDrawable background = new BitmapDrawable(image);
        background.setAlpha(95);
        mPageLayout.setBackgroundDrawable(background);
        // UI post fields
        if (mPost.IsPosted.equals("true")) {
            mPostStatusText.setText("POSTED");
            mPostStatusText.setTextColor(Color.parseColor("#669900"));
            // Hide submit and delete buttons
            mSubmitPostButton.setEnabled(false);
            mDeletePostButton.setEnabled(false);
        } else {
            mPostStatusText.setText("QUEUED");
            mPostStatusText.setTextColor(Color.parseColor("#ffbb33"));
        }
        mDescriptionText.setText(mPost.Description);
        mLocationText.setAllCaps(true);
        mLocationText.setText(mPost.Tags);
        // TODO: Distance units
        mVisualRangeText.setText(mPost.VisualRangeOne + " KM");
        // TODO: Insert VisualRangeTwo
        mTimeText.setText(Util.toDisplayDateTime(mPost.Date));
        mGpsText.setText("(" + mPost.GeoX + ", " + mPost.GeoY + ")");
        // Colors
        mLowColorImage.setBackgroundColor(Integer.parseInt(mPost.LowColor));
        mHighColorImage.setBackgroundColor(Integer.parseInt(mPost.HighColor));
        */

        // UI Buttons
        mViewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Store image for next activity to read
                //Util.storeTransactionImage(getApplicationContext(), Util.stringToBitMap(mPost.Image));
                //Toast.makeText(PostDetailsActivity.this, "Stored image of size " + mPost.Image.length(), Toast.LENGTH_SHORT).show();

                // View image
                Intent intent = new Intent(getApplicationContext(), GalleryImageActivity.class);
                //intent.putExtra("IMAGE_STRING", mPost.Image);
                startActivity(intent);
            }
        });
        mDeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete post
                //mPost.delete(getApplicationContext());
            }
        });
        mSubmitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Submit queued post
                //mPost.submit(getApplicationContext());
            }
        });
    }

    @Override
    protected void onResume() {
        // Get post again
        //if (mPost == null) mPost = PostDataManager.getPost(this, Long.parseLong(mPostId));
        super.onResume();
    }
}
