// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.target;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.ContentFrameLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.wsu.lar.airpact_fire.util.Util;
import lar.wsu.edu.airpact_fire.R;

/**
 * Defines an object which represents the logical state of a Point of
 * Interest target in the interface.
 *
 * <p>All targets are controlled by the
 * {@link edu.wsu.lar.airpact_fire.ui.target.manager.UiTargetManager},
 * which in turn is controlled by a {@link android.support.v4.app.Fragment}
 * in the {@link edu.wsu.lar.airpact_fire.ui.activity.ImageLabActivity}.</p>
 */
public class UiTarget {

    private final int mId;
    private final int mRadius;

    private ImageView mContainerImageView;
    private TextView mTargetTextView;

    public UiTarget(Activity activity, ImageView imageView, int id, int targetRadius, int x, int y) {

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
        mTargetTextView.setText("" + (id + 1)); // Display as one-based ID
        mTargetTextView.setTextColor(Color.parseColor("#000000"));
        mTargetTextView.setTypeface(null, Typeface.BOLD);

        // Add to activity
        ContentFrameLayout parent = (ContentFrameLayout)
                activity.findViewById(android.R.id.content);
        parent.addView(mTargetTextView);
    }

    public void setPosition(int x, int y) {

        // Keep target in bounds: check bottom, up, right, and left bounds (respectively)
        if ((y + mRadius) >= (mContainerImageView.getY() + mContainerImageView.getHeight())) {
            mTargetTextView.setX(x - mRadius);
            return;
        } else if ((y - mRadius) <= mContainerImageView.getY())  {
            mTargetTextView.setX(x - mRadius);
            return;
        } else if ((x + mRadius) >= (mContainerImageView.getX() + mContainerImageView.getWidth())) {
            mTargetTextView.setY(y - mRadius);
            return;
        } else if ((x - mRadius) <= (mContainerImageView.getX())) {
            mTargetTextView.setY(y - mRadius);
            return;
        }

        // Ok: set position
        mTargetTextView.setX(x - mRadius);
        mTargetTextView.setY(y - mRadius);
    }

    public int[] getPosition() {
        return new int[] {
                (int) mTargetTextView.getX() + mRadius,
                (int) mTargetTextView.getY() + mRadius
        };
    }

    public float[] getImagePosition() {
        int[] position = getPosition();
        return new float[] {
                position[0],
                position[1] + getZoomOffset()
        };
    }

    /** Get height offset from center zoom on y-axis */
    public int getZoomOffset() {
        Drawable imageDrawable = mContainerImageView.getDrawable();
        Bitmap image = Util.drawableToBitmap(imageDrawable);
        int imageViewHeight = mContainerImageView.getHeight();
        int bitmapHeight = image.getHeight();
        int heightTouchOffset = (bitmapHeight - imageViewHeight) / 2;
        return heightTouchOffset;
    }

    public int getColor() {

        Bitmap image = Util.drawableToBitmap(mContainerImageView.getDrawable());
        int offset = getZoomOffset();

        // Get pixel color at real position
        int[] position = getPosition();
        int pixel = image.getPixel(
                position[0],
                position[1] + offset
        );
        return pixel;
    }

    public void show() {
       mTargetTextView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mTargetTextView.setVisibility(View.GONE);
    }

    /** @return zero-based target ID number */
    public int getId() {
        return mId;
    }
}
