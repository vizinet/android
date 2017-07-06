package edu.wsu.lar.airpact_fire.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;
import java.util.List;

import lar.wsu.edu.airpact_fire.R;
import edu.wsu.lar.airpact_fire.util.Util;

public class GalleryActivity extends AppCompatActivity {

    /* QUEUED POSTS ACTIVITY
     *      - Displays queued posts in SQL database
     *      - User can upload or delete posts
     */

    // TODO: Find custom font(s) for whole app

    //private List<Post> mPostList;

    private LinearLayout mPage;
    private TableLayout mUserPostTable;
    private TableLayout mPostTable;
    private TextView mPostText;

    private View containerView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queued_posts);
        Util.setupSecondaryNavBar(this, HomeActivity.class, "PICTURE GALLERY");

        // Get and populate post table
        mUserPostTable = (TableLayout) findViewById(R.id.user_post_table);
        mPostText = (TextView) findViewById(R.id.posts_text);

        mPage = (LinearLayout) findViewById(R.id.page);
        mPostTable = (TableLayout) findViewById(R.id.post_table);
        containerView = findViewById(R.id.post_table);//findViewById(R.id.view);

        // Set background gradient
//        GradientDrawable gd = new GradientDrawable(
//                GradientDrawable.Orientation.TOP_BOTTOM,
//                //new int[] {0xFF616261,0xFF131313});
//                new int[]{
//                        getResources().getColor(R.color.schemeTransparentLight),
//                        getResources().getColor(R.color.schemeTransparentDark)
//                });
//        gd.setCornerRadius(0f);
//        mPage.setBackgroundDrawable(gd);

        // Stats
        /*
        String username = AppDataManager.getRecentUser();
        int numPosted = PostDataManager.getNumSubmitted(getApplicationContext(), username);
        int numQueued = PostDataManager.getNumQueued(getApplicationContext(), username);
        mPostText.setText(numPosted + " Posted\n" + numQueued + " Queued\n" + "0 Drafted");
        //mPostText.setAllCaps(true);
        */

        // Get user's posts
        PostReadManager postReadManager = new PostReadManager();
        postReadManager.execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Populates table with posts from user
    @TargetApi(Build.VERSION_CODES.M)
    private void generateTable() {

        // No posts status
        /*
        if (mPostList == null) return;
        if (mPostList.size() == 0) {
            mPostText.setText("No posts to display");
            mUserPostTable.removeAllViews();

            return;
        }
        */

        // Add background as last post image
        // Forgo cool background
//        Bitmap mBackgroundImageBitmap = Util.stringToBitMap(mPostList.get(mPostList.size() - 1).Image);
//        mBackgroundImageBitmap = Util.doBlur(getApplicationContext(), mBackgroundImageBitmap);
//        Drawable mBackgroundImage = new BitmapDrawable(getResources(), mBackgroundImageBitmap);
//        getWindow().setBackgroundDrawable(mBackgroundImage);

        // Image gallery
        // TODO: Image caching
        int numImagesPerRow = 3;
        int imageWidth = Math.round(containerView.getWidth() / (float) numImagesPerRow); // dp

        // First week title
        LinearLayout weekTitle = new LinearLayout(this);
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        //titleLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        weekTitle.setLayoutParams(titleLayoutParams);
        weekTitle.setPadding(0, 40, 0, 20);
        // " " Text
        TextView weekTitleText = new TextView(this);
        weekTitleText.setText("THIS WEEK");
        weekTitleText.setPadding(0, 0, 20, 0);
        weekTitleText.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        weekTitleText.setTextSize(30);
        weekTitleText.setTypeface(null, Typeface.BOLD);
        weekTitle.addView(weekTitleText);
        // " " horizontal line
        FrameLayout lineWrapper = new FrameLayout(this);
        FrameLayout.LayoutParams wrapperParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        //wrapperParams.gravity = Gravity.CENTER_VERTICAL;
        lineWrapper.setLayoutParams(wrapperParams);
        LinearLayout horizontalLine = new LinearLayout(this);
        //horizontalLine.setPadding(0, lineWrapper.getHeight() / 2, 0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.height = 2;
        layoutParams.weight = 1.0f;
        layoutParams.gravity = Gravity.BOTTOM;
        horizontalLine.setLayoutParams(layoutParams);
        horizontalLine.setBackgroundColor(getResources().getColor(R.color.schemeTransparentNothingLight));
        lineWrapper.addView(horizontalLine);
        //weekTitle.addView(lineWrapper);

        mPostTable.addView(weekTitle);

        // Beginning row
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(
                new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                )
        );
        /*
        for (int i = (mPostList.size() - 1); i >= 0; i--) {
            // Get post
            final Post post = mPostList.get(i);

            // Make wrapper for image
            LinearLayout imageWrapper = new LinearLayout(this);
            imageWrapper.setLayoutParams(new TableRow.LayoutParams(
                            //TableRow.LayoutParams.MATCH_PARENT,
                            imageWidth,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            imageWrapper.setOrientation(LinearLayout.VERTICAL);

            // Put post # at top
            TextView postNumberText = new TextView(this);
            postNumberText.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT
            ));
            postNumberText.setText("#" + post.PostNum + " - " + Util.toDisplayDateTime(post.Date));
            postNumberText.setBackground(getDrawable(R.color.schemeTransparentDark));
            postNumberText.setMaxWidth(imageWidth);
            postNumberText.setMaxLines(1);
            postNumberText.setTextColor(getResources().getColor(R.color.schemeWhite));
            postNumberText.setPadding(10, 10, 10, 10);
            imageWrapper.addView(postNumberText);

            // Post status indicator
            LinearLayout postStatus = new LinearLayout(this);
            postStatus.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    5
            ));
            if (post.IsPosted.equals("true"))
                postStatus.setBackground(getDrawable(R.color.schemeSuccess));
            else postStatus.setBackground(getDrawable(R.color.schemeQueued));
            imageWrapper.addView(postStatus);

            // Place image in wrapper
            ImageView postImageView = new ImageView(this);
            Bitmap postImageBitmap = Util.createScaledBitmap(Util.stringToBitMap(post.Image), imageWidth);
            postImageView.setImageBitmap(postImageBitmap);
            //postImageView.setBackground(getDrawable(R.drawable.border));
            postImageView.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            // Add touch listener: gives more info on selected post. Also, allows us to post queued posts
            postImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                    intent.putExtra("POST_ID", String.valueOf(post.SqlId));

                    startActivity(intent);
                }
            });
            imageWrapper.addView(postImageView);

            tableRow.addView(imageWrapper);

            // Add past one & create new row
            //  - When row is filled up, or
            //  - When now more images
            if (i % numImagesPerRow == 0
                    || (i == 0)) {
                mPostTable.addView(tableRow);

                tableRow = new TableRow(this);
                tableRow.setLayoutParams(
                        new TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT
                        )
                );
            }
        }
        */


        // Post with most recent at the top
        /*
        for (int i = (mPostList.size() - 1); i >= 0; i--) {
            // Get post
            final Post post = mPostList.get(i);

            // Create row
            TableRow postRow = new TableRow(this);
            postRow.setLayoutParams(
                    new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            // #
            String postNum = String.valueOf(post.PostNum);
            TextView PostNumTextView = new TextView(this);
            PostNumTextView.setText(postNum);
            PostNumTextView.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            postRow.addView(PostNumTextView);
            // Status
            TextView statusTextView = new TextView(this);
            statusTextView.setAllCaps(true); // caps
            statusTextView.setTypeface(null, Typeface.BOLD); // bold
            if (post.IsPosted.equals("true")) { // posted
                statusTextView.setText("Posted");
                statusTextView.setTextColor(Color.parseColor("#669900"));
            } else { // queued
                statusTextView.setText("Queued");
                statusTextView.setTextColor(Color.parseColor("#ffbb33"));
                // Set whole tableRow as a color
                //postRow.setBackgroundColor(Color.LTGRAY);
            }
            statusTextView.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            postRow.addView(statusTextView);
            // Date
            // TODO: Turn into util function
            String meridiem = (post.Date.get(Calendar.AM_PM) == 1) ? "PM" : "AM";
            TextView DateTextView = new TextView(this);
            DateTextView.setText(post.Date.get(Calendar.MONTH)
                    + "/" + post.Date.get(Calendar.DATE)
                    + "/" + post.Date.get(Calendar.YEAR)
                    + "\n"
                    + post.Date.get(Calendar.HOUR)
                    + ":" + post.Date.get(Calendar.MINUTE)
                    + " " + meridiem);
            DateTextView.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            postRow.addView(DateTextView);
            // Location
            TextView LocationTextView = new TextView(this);
            LocationTextView.setMaxLines(3);
            LocationTextView.setText(post.Tags);
            LocationTextView.setLayoutParams(new TableRow.LayoutParams(
                            40,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            postRow.addView(LocationTextView);
            // More
            Button moreButton = new Button(this);
            moreButton.setText("...");
            moreButton.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            postRow.addView(moreButton);
            // Add row to table
            mUserPostTable.addView(postRow,
                    new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT
                    )
            );

            // Gives more info on selected post. Also, allows us to post queued posts
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Open up page for specific post, using index to get post, which has SQL
                    // TODO: Pass param to the post detail activity
                    Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                    intent.putExtra("POST_ID", String.valueOf(post.SqlId));

                    startActivity(intent);
                }
            });
        }
        */
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "QueuedPosts Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://lar.wsu.edu.airpact_fire/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "QueuedPosts Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://lar.wsu.edu.airpact_fire/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    class PostReadManager extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = new ProgressDialog(GalleryActivity.this);
        private long postId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Show loader
            progress.setTitle("Loading Posts");
            progress.setMessage("This will take only a moment...");
            progress.show();
            while (!progress.isShowing()) try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // mPostList = PostDataManager.getPosts(getApplicationContext(), AppDataManager.getRecentUser());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Hide progress
            progress.dismiss();

            // Now populate table with results
            generateTable();
        }
    }
}
