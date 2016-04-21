package lar.wsu.edu.airpact_fire;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private TextView mWelcomeText, mLoginTimeText, mInfoText;
    private Button mTakePictureImageButton, mViewQueuedPostsButton, mViewUserXMLButton;

    private String mUsername, mLoginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");

        // Action bar stuff
//        ActionBar ab = getActionBar();
//        ab.setHomeButtonEnabled(true);
//        ab.setDisplayHomeAsUpEnabled(true);

        // Initialize
        mUsername = UserDataManager.getLastUser();
        mLoginTime = (String) UserDataManager.getUserData(mUsername, "loginTime");

        // Grab UI objects
        mWelcomeText = (TextView) findViewById(R.id.welcome_text);
        mLoginTimeText = (TextView) findViewById(R.id.login_time_text);
        mInfoText = (TextView) findViewById(R.id.info_text);
        mTakePictureImageButton = (Button) findViewById(R.id.take_picture_button);
        mViewQueuedPostsButton = (Button) findViewById(R.id.view_queued_posts_button);
        mViewUserXMLButton = (Button) findViewById(R.id.view_user_xml_button);

        // Add welcome text for user
        mWelcomeText.setText(("Hey, " + mUsername).toUpperCase());
        mLoginTimeText.setText(("Login time:\n".toUpperCase()) + mLoginTime);
        mInfoText.setText("NOTICE:\nYou're currently logged in as a local user. Go ahead and make a post!");

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

        mViewUserXMLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewUserXMLActivity.class);
                startActivity(intent);
            }
        });
    }

    // NOTE: user may have changed!
    @Override
    protected void onResume() {
        super.onResume();

        mUsername = UserDataManager.getLastUser();
        mLoginTime = (String) UserDataManager.getUserData(mUsername, "loginTime");

        mWelcomeText.setText(("Hey, " + mUsername).toUpperCase());
        mLoginTimeText.setText(("Login time:\n".toUpperCase()) + mLoginTime);


    }
}
