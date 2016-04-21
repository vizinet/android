package lar.wsu.edu.airpact_fire;

import android.annotation.TargetApi;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView mImageView;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        mImageView = (ImageView) findViewById(R.id.image_view);

        Drawable ob = new BitmapDrawable(getResources(), Post.Image);
        mImageView.setBackground(ob);
    }
}
