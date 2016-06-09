package lar.wsu.edu.airpact_fire;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;
import java.util.List;

public class QueuedPostsActivity extends AppCompatActivity {

    /* QUEUED POSTS ACTIVITY
     *      - Displays queued posts in SQL database
     *      - User can upload or delete posts
     */

    // TODO: Find custom font(s) for whole app

    private List<Post> mPostList;
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

        // Get and populate post table
        mUserPostTable = (TableLayout) findViewById(R.id.user_post_table);
        mPostText = (TextView) findViewById(R.id.posts_text);

        mPostTable = (TableLayout) findViewById(R.id.post_table);
        containerView = findViewById(R.id.view);

        // Stats
        String username = UserDataManager.getRecentUser();
        int numPosted = PostDataManager.getNumSubmitted(getApplicationContext(), username);
        int numQueued = PostDataManager.getNumQueued(getApplicationContext(), username);
        mPostText.setText(numPosted + " posted, " + numQueued + " queued");
        mPostText.setAllCaps(true);

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

        if (mPostList == null) return;

//        Toast.makeText(QueuedPostsActivity.this, "generateTable() with mPostList.size() = "
//                + mPostList.size(), Toast.LENGTH_SHORT).show();

        // No posts status
        if (mPostList.size() == 0) {
            mPostText.setText("No posts to display");
            mUserPostTable.removeAllViews();
        }

        // Image gallery
        // TODO: Image caching
        int numImagesPerRow = 3;
        int imageWidth = Math.round(containerView.getWidth() / (float) numImagesPerRow); // dp

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(
                new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                )
        );
        for (int i = (mPostList.size() - 1); i >= 0; i--) {
            // Get post
            final Post post = mPostList.get(i);

            // Put image in row
            ImageView postImageView = new ImageView(this);
            Bitmap postImageBitmap = Util.createScaledBitmap(Util.stringToBitMap(post.Image), imageWidth);
            postImageView.setImageBitmap(postImageBitmap);
            postImageView.setBackground(getDrawable(R.drawable.border));
            postImageView.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            // Gives more info on selected post. Also, allows us to post queued posts
            postImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                    intent.putExtra("POST_ID", String.valueOf(post.SqlId));

                    startActivity(intent);
                }
            });
            tableRow.addView(postImageView);

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


        // Post with most recent at the top
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

        private ProgressDialog progress = new ProgressDialog(QueuedPostsActivity.this);
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
            mPostList = PostDataManager.getPosts(getApplicationContext(), UserDataManager.getRecentUser());

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
