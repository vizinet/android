// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.target.manager;

import android.app.Activity;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.wsu.lar.airpact_fire.ui.target.UiTarget;

/**
 * Manager/engine of image targets in the interface.
 *
 * <p>This target manager is reminiscent of the observer pattern, where each target move causes the
 * corresponding {@link UiTarget} to update its internal values.</p>
 */
public class UiTargetManager {

    // Standard 100-pixel target diameter
    private static final int sTargetRadius = 50;

    private final Activity mActivity;
    private ImageView mCurrentImageView;
    private HashMap<Integer, List<UiTarget>> mUITargetFragmentMap;

    private int mCurrentFragmentId = -1;
    private int mLastTargetId = 0;

    public UiTargetManager(Activity activity) {
        mActivity = activity;
        mUITargetFragmentMap = new HashMap<>();
    }

    /**
     * Set context for target manager as current fragment for containing these targets.
     *
     * @param fragmentId integer uniquely identifying a fragment
     * @param imageView {@link ImageView} upon which targets are placed
     * @param targetCount number of targets in current fragment
     */
    public void setContext(int fragmentId, ImageView imageView, int targetCount) {

        mCurrentFragmentId = fragmentId;
        mCurrentImageView = imageView;
        List<UiTarget> targets;

        // See if we need to create the targets
        if (!mUITargetFragmentMap.containsKey(mCurrentFragmentId)) {
            targets = new ArrayList<>();
            int canvasHeight = mCurrentImageView.getHeight();
            int canvasWidth = mCurrentImageView.getWidth();

            // Space each target equally in the vertical axis
            int targetPadding = canvasHeight / (targetCount + 1);

            for (int i = 1; i <= targetCount; i++) {
                int x = canvasWidth / 2;
                int y = i * targetPadding;
                targets.add(new UiTarget(
                        mActivity,
                        mCurrentImageView,
                        mLastTargetId++,
                        sTargetRadius,
                        x,
                        y));
            }
            mUITargetFragmentMap.put(mCurrentFragmentId, targets);
        }

        // Show current targets.
        showAll();
    }

    public float[] getTargetImagePosition(int targetId) {
        List<UiTarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        return targets.get(targetId).getImagePosition();
    }

    public int getTargetColor(int targetId) {
        List<UiTarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        return targets.get(targetId).getColor();
    }

    public void setTargetPosition(int targetId, int x, int y) {
        List<UiTarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        targets.get(targetId).setPosition(x, y);
    }

    public void showAll() {
        List<UiTarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        for (UiTarget target : targets) {
            target.show();
        }
    }

    public void hideAll() {
        List<UiTarget> targets = mUITargetFragmentMap.get(mCurrentFragmentId);
        if (targets == null) return;
        for (UiTarget target : targets) {
            target.hide();
        }
    }
}
