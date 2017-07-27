// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.target.manager;

import android.app.Activity;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import edu.wsu.lar.airpact_fire.ui.target.UITarget;

// TODO: Initially place targets in pattern

public class UITargetManager {

    // Standard 100-pixel target diameter
    private static final int sTargetRadius = 50;

    private final Activity mActivity;
    private HashMap<Integer, List<UITarget>> mUITargetFragmentMap;
    private ImageView mCurrentImageView;

    private int mCurrentFragmentId = -1;
    private int mLastId = 0;

    public UITargetManager(Activity activity) {
        mActivity = activity;
        mUITargetFragmentMap = new HashMap<>();
    }

    /**
     * Set context for target manager as current fragment for containing
     * these targets.
     *
     * @param imageView {@link ImageView} where targets are placed
     * @param targetCount number of targets in current fragment
     */
    public void setContext(int fragmentId, ImageView imageView, int targetCount) {

        mCurrentFragmentId = fragmentId;
        mCurrentImageView = imageView;
        List<UITarget> targets;

        // See if we need to create the targets
        if (!mUITargetFragmentMap.containsKey(mCurrentFragmentId)) {
            targets = new ArrayList<>();
            while (targetCount-- > 0) {
                int x = mCurrentImageView.getWidth() / 2, y = mCurrentImageView.getHeight() / 2;
                targets.add(new UITarget(
                        mActivity,
                        mCurrentImageView,
                        mLastId++,
                        sTargetRadius,
                        x,
                        y));
            }
            mUITargetFragmentMap.put(mCurrentFragmentId, targets);
        }

        // Show current targets
        showAll();
    }

    public int getTargetColor(int targetId) {
        List<UITarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        return targets.get(targetId).getColor();
    }

    public void setTargetPosition(int targetId, int x, int y) {
        List<UITarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        targets.get(targetId).setPosition(x, y);
    }

    public void showAll() {
        List<UITarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        for (UITarget target : targets) {
            target.show();
        }
    }

    public void hideAll() {
        List<UITarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        for (UITarget target : targets) {
            target.hide();
        }
    }
}
