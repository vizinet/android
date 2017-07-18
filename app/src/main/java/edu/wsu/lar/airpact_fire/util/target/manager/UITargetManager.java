// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.util.target.manager;

import android.app.Activity;
import edu.wsu.lar.airpact_fire.util.target.UITarget;

// TODO: Initially place targets in pattern
// TODO: Subscribe to UI imageView touch events, knowing *which* one to move

public class UITargetManager {

    private static final int sTargetRadius = 30;

    private UITarget[] mUITargets;

    public UITargetManager(Activity activity, int targetCount) {

        mUITargets = new UITarget[targetCount];
        for (int i = 0; i < targetCount; i++) {
            int x = 0, y = 0;
            mUITargets[i] = new UITarget(activity, i, sTargetRadius, x, y);
        }
    }

    public void setTargetPosition(int targetId, int x, int y) {
        mUITargets[targetId].setPosition(x, y);
    }
}
