package lar.wsu.edu.airpact_fire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddPictureDetailsActivity extends AppCompatActivity {

    private Button mRetakeButton, mViewImageButton, mAddToQueueButton, mSubmitButton;
    private TextView mImageDateTextView, mImageLocationTextView;
    private EditText mVisualRangeInput, mDescriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture_details);
        setTitle("Picture Details");

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

        mImageDateTextView.setText(Post.Time.toString());
        mImageLocationTextView.setText("(" + Post.LatitudeLongitude[0] + ", " + Post.LatitudeLongitude[1] + ")");

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
                Post.Description = mDescriptionInput.getText().toString();
                Post.VisualRange = Float.parseFloat(mVisualRangeInput.getText().toString());

                // Submit post
                Post.submit(getApplicationContext());

                // Review what was just posted
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);//PostReviewActivity.class);
                startActivity(intent);
            }
        });
    }
}
