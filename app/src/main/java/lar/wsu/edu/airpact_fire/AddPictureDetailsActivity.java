package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// User provides picture details before posting/queueing
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

        // Set post context and activity
        Post.Context = getApplicationContext();
        Post.Activity = this;

        // Get UI elements
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
        populateFormData();

        // Event listeners
        mRetakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectContrastActivity.class);
                startActivity(intent);
            }
        });
        // View image with new activity
        mViewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.storeTransactionImage(getApplicationContext(),
                        Util.stringToBitMap(
                                String.valueOf(UserDataManager.getUserData(UserDataManager.getRecentUser(), "image")
                                )));
                Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                // Pass in image to new activity
                //intent.putExtra("IMAGE_STRING", String.valueOf(UserDataManager.getUserData(UserDataManager.getRecentUser(), "image")));
                startActivity(intent);
            }
        });
        mAddToQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectFormData();

                // Queue post
                Post post = new Post();
                post.queue(getApplicationContext());
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data
                collectFormData();

                // Submit post
                Post post = new Post();
                post.submit(getApplicationContext());
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

    // Collect user values for post
    private void collectFormData() {
        String lastUser = UserDataManager.getRecentUser();
        UserDataManager.setUserData(lastUser, "description", mDescriptionInput.getText().toString());
        UserDataManager.setUserData(lastUser, "visualRange", mVisualRangeInput.getText().toString());
        UserDataManager.setUserData(lastUser, "tags", mAddTagInput.getText().toString());
    }

    // Add past user values to form
    private void populateFormData() {
        String lastUser = UserDataManager.getRecentUser();
        mImageDateTextView.setText(UserDataManager.getUserData(lastUser, "loginTime"));
        mImageLocationTextView.setText("(" + UserDataManager.getUserData(lastUser, "geoX")
                + ", " + UserDataManager.getUserData(lastUser, "geoY") + ")");
        mAddTagInput.setText(UserDataManager.getUserData(lastUser, "tags"));
        mVisualRangeInput.setText(UserDataManager.getUserData(lastUser, "visualRange"));
        mDescriptionInput.setText(UserDataManager.getUserData(lastUser, "description"));
    }
}
