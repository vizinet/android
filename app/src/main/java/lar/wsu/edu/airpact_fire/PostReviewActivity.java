package lar.wsu.edu.airpact_fire;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

public class PostReviewActivity extends AppCompatActivity {

    private TextView mTextView;
    private Button mRefreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);
        setTitle("Review Post");

        mTextView = (TextView) findViewById(R.id.text_view);
        mRefreshButton = (Button) findViewById(R.id.refresh_button);

        if (Post.isUser) {
            JSONObject postJSON = Post.debugJSON; //Post.toJSON();
            mTextView.append(postJSON.toJSONString());
        }

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.append(Post.debugOut);
            }
        });
    }
}
