package lar.wsu.edu.airpact_fire;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

// TODO: Find more efficient way of displaying image

public class ViewImageActivity extends AppCompatActivity {

    private ImageView mImageView;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        // Canvas for image
        mImageView = (ImageView) findViewById(R.id.image_view);

        // Get bitmap from preceding activity
//        String imageString;
//        if (savedInstanceState == null) {
//            Bundle extras = getIntent().getExtras();
//            if(extras == null) {
//                imageString = null;
//            } else {
//                imageString = extras.getString("IMAGE_STRING");
//            }
//        } else {
//            imageString = (String) savedInstanceState.getSerializable("IMAGE_STRING");
//        }

        // Create and add bitmap to image view
        Bitmap bitmap = Util.getTransactionImage(this);
        //Drawable ob = new BitmapDrawable(getResources(), bitmap);
        mImageView.setImageBitmap(bitmap);
    }
}
