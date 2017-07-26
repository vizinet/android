// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.util.target.manager;

import android.app.Activity;
import android.widget.ImageView;
import edu.wsu.lar.airpact_fire.util.target.UITarget;

// TODO: Initially place targets in pattern
// TODO: Subscribe to UI imageView touch events, knowing *which* one to move

public class UITargetManager {

    // Standard 100-pixel target_background diameter
    private static final int sTargetRadius = 50;

    private UITarget[] mUITargets;
    private ImageView mImageView;

    public UITargetManager(Activity activity, ImageView imageView, int targetCount) {

        mImageView = imageView;
        mUITargets = new UITarget[targetCount];
        for (int i = 0; i < targetCount; i++) {
            int x = 0, y = 0;
            mUITargets[i] = new UITarget(activity, mImageView, i, sTargetRadius, x, y);
        }
    }

    public int getTargetColor(int targetId) {
        return mUITargets[targetId].getColor();
    }

    public void setTargetPosition(int targetId, int x, int y) {
        mUITargets[targetId].setPosition(x, y);
    }
}
