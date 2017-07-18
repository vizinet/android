// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.util.target;

import android.app.Activity;
import android.support.v7.widget.ContentFrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import lar.wsu.edu.airpact_fire.R;

public class UITarget {

    private int mId;
    private ImageView mImageView;

    public UITarget(Activity activity, int id, int targetRadius, int x, int y) {

        // Target identifier
        mId = id;

        // Create image view
        mImageView = new ImageView(activity);
        mImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mImageView.setImageResource(R.drawable.indicator_point);

        // Add to activity
        ContentFrameLayout parent = (ContentFrameLayout)
                activity.findViewById(android.R.id.content);
        parent.addView(mImageView);

        // Width, height, and position
        mImageView.getLayoutParams().width = targetRadius * 2;
        mImageView.getLayoutParams().height = targetRadius * 2;
        mImageView.setX(x);
        mImageView.setY(y);
    }

    public void setPosition(int x, int y) {
        mImageView.setX(x);
        mImageView.setY(y);
    }

    public int[] getPosition() {
        return new int[] { (int) mImageView.getX(), (int) mImageView.getY() };
    }

    void getColor() {}
}
