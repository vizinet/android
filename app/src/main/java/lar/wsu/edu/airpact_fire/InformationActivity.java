package lar.wsu.edu.airpact_fire;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    private TextView mLoginTimeText,
            mInternetConnectionText, mPostTexts;
    private String mUsername, mLoginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        // Grab UI objects
        mLoginTimeText = (TextView) findViewById(R.id.login_time_text);
        mInternetConnectionText = (TextView) findViewById(R.id.server_status_text);
        mPostTexts = (TextView) findViewById(R.id.posts_text);
    }

    private void updateInfo() {
        // Welcome text
        mUsername = UserDataManager.getRecentUser();

        // Login
        mLoginTime = UserDataManager.getUserData(mUsername, "loginTime");
        mLoginTimeText.setText(mLoginTime);

        // Posts info
        int numPosted = PostDataManager.getNumSubmitted(getApplicationContext(), mUsername);
        int numQueued = PostDataManager.getNumQueued(getApplicationContext(), mUsername);
        mPostTexts.setText(numPosted + " posted, " + numQueued + " queued");
        mPostTexts.setAllCaps(true);

        // Internet connection display
        mInternetConnectionText.setAllCaps(true);
        //mInternetConnectionText.setTextColor(Color.parseColor("#cc0000"));
        mInternetConnectionText.setText("Disconnected");
        if (Util.isServerAvailable(InformationActivity.this)) {//if (Util.isNetworkAvailable(this)) {
            mInternetConnectionText.setTextColor(Color.parseColor("#669900"));
            mInternetConnectionText.setText("Connected");
        }
    }
}
