package lar.wsu.edu.airpact_fire;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class QueuedPostsActivity extends AppCompatActivity {

    /* QUEUED POSTS ACTIVITY
     *      - Displays queued posts in SQL database
     *      - User can upload or delete posts
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queued_posts);
    }
}
