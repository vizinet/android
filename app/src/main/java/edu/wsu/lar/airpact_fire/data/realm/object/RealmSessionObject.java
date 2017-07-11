// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import java.util.Date;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.SessionObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;

/** @see SessionObject */
public class RealmSessionObject implements SessionObject {

    private Realm mRealm;
    private Session mSession;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmSessionObject(Realm realm, Session session, DataManager dataManager,
                              DebugManager debugManager) {
        mRealm = realm;
        mSession = session;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public UserObject getUser() {
        return new RealmUserObject(mRealm, mSession.user, mDataManager, mDebugManager);
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
    public int getSelectedAlgorithm() {
        return mSession.selectedAlgorithm;
    }

    @Override
    public void setSelectedAlgorithm(int value) {
        mRealm.beginTransaction();
        mSession.selectedAlgorithm = value;
        mRealm.commitTransaction();
    }
}
