package lar.wsu.edu.airpact_fire;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private TextView mWelcomeText, mLoginTimeText, mInfoText, mPostKeyText;
    private Button mTakePictureImageButton, mViewQueuedPostsButton;

    private String mUsername, mLoginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Grab UI objects
        mWelcomeText = (TextView) findViewById(R.id.welcome_text);
        mLoginTimeText = (TextView) findViewById(R.id.login_time_text);
        mInfoText = (TextView) findViewById(R.id.info_text);
        mPostKeyText = (TextView) findViewById(R.id.post_key_text);
        mTakePictureImageButton = (Button) findViewById(R.id.take_picture_button);
        mViewQueuedPostsButton = (Button) findViewById(R.id.view_queued_posts_button);

        // Update home variables
        updateHome();
        mInfoText.setText("You're currently logged in as a local user. Go ahead and make a post!");

        // Set image button listener
        mTakePictureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User will now take picture and select contrast points
                Intent intent = new Intent(getApplicationContext(), SelectContrastActivity.class);
                startActivity(intent);
            }
        });

        // Set image button listener
        mViewQueuedPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User can view queued posts
                Intent intent = new Intent(getApplicationContext(), QueuedPostsActivity.class);
                startActivity(intent);
            }
        });
    }

    // NOTE: user may have changed!
    @Override
    protected void onResume() {
        super.onResume();

        updateHome();
    }

    private void updateHome() {
        mUsername = UserDataManager.getLastUser();
        mLoginTime = (String) UserDataManager.getUserData(mUsername, "loginTime");

        mWelcomeText.setText((mUsername).toUpperCase());
        mLoginTimeText.setText(mLoginTime);
        mPostKeyText.setText(UserDataManager.getUserData(mUsername, "secretKey"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_tutorial:
                intent = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_capture:
                intent = new Intent(getApplicationContext(), SelectContrastActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_queue:
                intent = new Intent(getApplicationContext(), QueuedPostsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_debug:
                intent = new Intent(getApplicationContext(), ViewUserXMLActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
