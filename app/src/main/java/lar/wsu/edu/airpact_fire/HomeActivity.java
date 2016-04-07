package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TextView mWelcomeText, mLoginTimeText, mPostKeyText;
    ImageButton mTakePictureImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Home");

        mWelcomeText = (TextView) findViewById(R.id.welcome_text);
        mLoginTimeText = (TextView) findViewById(R.id.login_time_text);
        mPostKeyText = (TextView) findViewById(R.id.post_key_text);
        mTakePictureImageButton = (ImageButton) findViewById(R.id.take_picture_button);

        // Add welcome text for user
        mWelcomeText.setText("Hey, " + User.username);
        mLoginTimeText.setText("Login time:\t" + User.loginTime.toString());
        mPostKeyText.setText("Post key:\t" + User.postKeys.peek());

        // Set image button listener
        mTakePictureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open up activity to take picture and select contrast points
                Intent intent = new Intent(getApplicationContext(), MainActivity.class); // TODO replace with SelectContrastActivity.class);
                startActivity(intent);
            }
        });
    }
}
