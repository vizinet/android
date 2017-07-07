// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import java.util.Date;

import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.SessionObject;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;

/**
 * @see PostObject
 */
public class RealmSessionObject implements SessionObject {

    private Realm mRealm;
    private String mUsername;
    private DebugManager mDebugManager;

    public RealmSessionObject(Realm realm, String username, DebugManager debugManager) {
        mRealm = realm;
        mUsername = username;
        mDebugManager = debugManager;
    }

    @Override
    public Date getStartDate() {
        return null;
    }

    @Override
    public void getEndDate() {

    }

    @Override
    public Date getLastLoginDate() {
        return null;
    }

    @Override
    public void setLastLoginDate(Date value) {

    }

    @Override
    public float getEstimatedDistance() {
        return 0;
    }

    @Override
    public void setEstimatedDistance(float value) {

    }

    @Override
    public Reference.Algorithm getSelectedAlgorithm() {
        return null;
    }

    @Override
    public void setSelectedAlgorithm(Reference.Algorithm value) {

    }
}
