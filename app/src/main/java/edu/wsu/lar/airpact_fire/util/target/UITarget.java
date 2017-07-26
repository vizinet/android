// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.util.target;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.ContentFrameLayout;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.wsu.lar.airpact_fire.util.Util;
import lar.wsu.edu.airpact_fire.R;

public class UITarget {

    private final int mId;
    private final int mRadius;

    private ImageView mContainerImageView;
    private TextView mTargetTextView;

    public UITarget(Activity activity, ImageView imageView, int id, int targetRadius, int x, int y) {

        mContainerImageView = imageView;
        mId = id;
        mRadius = targetRadius;

        // Layout stuff
        mTargetTextView = new TextView(activity);
        mTargetTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mTargetTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mTargetTextView.setWidth(mRadius * 2);
        mTargetTextView.setHeight(mRadius * 2);
        setPosition(x, y);

        // Display stuff
        mTargetTextView.setBackgroundResource(R.drawable.target_background);
        mTargetTextView.setTextSize(20);
        mTargetTextView.setText("" + (id + 1));
        mTargetTextView.setTextColor(Color.parseColor("#000000"));
        mTargetTextView.setTypeface(null, Typeface.BOLD);

        // Add to activity
        ContentFrameLayout parent = (ContentFrameLayout)
                activity.findViewById(android.R.id.content);
        parent.addView(mTargetTextView);
    }

    public void setPosition(int x, int y) {
        mTargetTextView.setX(x - mRadius);
        mTargetTextView.setY(y - mRadius);
    }

    public int[] getPosition() {
        return new int[] { (int) mTargetTextView.getX(), (int) mTargetTextView.getY() };
    }

    public int getColor() {
        Drawable imageDrawable = mContainerImageView.getDrawable();
        Bitmap image = Util.drawableToBitmap(imageDrawable);
        int pixel = image.getPixel((int) mTargetTextView.getX(), (int) mTargetTextView.getY());
        return pixel;
    }
}
