package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddPictureDetailsActivity extends AppCompatActivity {

    private Button mRetakeButton, mViewImageButton, mAddToQueueButton, mSubmitButton;
    private TextView mImageDateTextView, mImageLocationTextView;
    private EditText mVisualRangeInput, mDescriptionInput, mAddTagInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture_details);
        setTitle("Picture Details");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Set post context
        Post.Context = getApplicationContext();

        mRetakeButton = (Button) findViewById(R.id.retake_button);
        mViewImageButton = (Button) findViewById(R.id.view_image_button);
        mAddToQueueButton = (Button) findViewById(R.id.add_to_queue_button);
        mSubmitButton = (Button) findViewById(R.id.submit_button);
        mImageDateTextView = (TextView) findViewById(R.id.date_time_text_view);
        mImageLocationTextView = (TextView) findViewById(R.id.location_text_view);
        mVisualRangeInput = (EditText) findViewById(R.id.visual_range_input);
        mDescriptionInput = (EditText) findViewById(R.id.description_input);
        mAddTagInput = (EditText) findViewById(R.id.add_tag_input);

        // Pre-loaded information
        String lastUser = UserDataManager.getLastUser();
        mImageDateTextView.setText(UserDataManager.getUserData(lastUser, "loginTime"));
        mImageLocationTextView.setText("(" + UserDataManager.getUserData(lastUser, "geoX")
                + ", " + UserDataManager.getUserData(lastUser, "geoY") + ")");
        mAddTagInput.setText(UserDataManager.getUserData(lastUser, "tags"));

        // Event listeners
        mRetakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectContrastActivity.class);
                startActivity(intent);
            }
        });
        mViewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                startActivity(intent);
            }
        });
        mAddToQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                Toast.makeText(getApplicationContext(), "Post added to queue", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect user values for post
                String lastUser = UserDataManager.getLastUser();
                UserDataManager.setUserData(lastUser, "description", mDescriptionInput.getText().toString());
                UserDataManager.setUserData(lastUser, "visualRange", mVisualRangeInput.getText().toString());
                UserDataManager.setUserData(lastUser, "tags", mAddTagInput.getText().toString());

                Toast.makeText(getApplicationContext(), "Submitting post...", Toast.LENGTH_LONG).show();

                // Submit post
                Post.submit(getApplicationContext());

                // Review what was just posted
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
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
