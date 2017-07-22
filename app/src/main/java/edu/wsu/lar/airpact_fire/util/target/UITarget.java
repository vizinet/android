// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.util.target;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.ContentFrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import edu.wsu.lar.airpact_fire.util.Util;
import lar.wsu.edu.airpact_fire.R;

public class UITarget {

    private final int mId;
    private ImageView mContainerImageView;
    private ImageView mTargetImageView;

    public UITarget(Activity activity, ImageView imageView, int id, int targetRadius, int x, int y) {

        mContainerImageView = imageView;
        mId = id;

        // Create image view
        mTargetImageView = new ImageView(activity);
        mTargetImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mTargetImageView.setImageResource(R.drawable.indicator_point);

        // Add to activity
        ContentFrameLayout parent = (ContentFrameLayout)
                activity.findViewById(android.R.id.content);
        parent.addView(mTargetImageView);

        // Width, height, and position
        mTargetImageView.getLayoutParams().width = targetRadius * 2;
        mTargetImageView.getLayoutParams().height = targetRadius * 2;
        mTargetImageView.setX(x);
        mTargetImageView.setY(y);
    }

    public void setPosition(int x, int y) {
        mTargetImageView.setX(x);
        mTargetImageView.setY(y);
    }

    public int[] getPosition() {
        return new int[] { (int) mTargetImageView.getX(), (int) mTargetImageView.getY() };
    }

    public int getColor() {
        Drawable imageDrawable = mContainerImageView.getDrawable();
        Bitmap image = Util.drawableToBitmap(imageDrawable);
        int pixel = image.getPixel((int) mTargetImageView.getX(), (int) mTargetImageView.getY());
        return pixel;
    }
}
