package edu.wsu.lar.airpact_fire.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import lar.wsu.edu.airpact_fire.R;

public class ViewUserXMLActivity extends AppCompatActivity {

    private TextView XMLTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_xml);
        setTitle("User XML Viewer");

        XMLTextView = (TextView) findViewById(R.id.xml_text_view);
    }

    @Override
    protected void onResume() {
        /*
        Post.Activity = this;
        Post post = new Post();
        // XML JSON
        XMLTextView.setText(post.toSubmissionJSON().toJSONString());
        */

        // XML itself
        //XMLTextView.append("\n\n" + AppDataManager.getXML());


        super.onResume();
    }
}
