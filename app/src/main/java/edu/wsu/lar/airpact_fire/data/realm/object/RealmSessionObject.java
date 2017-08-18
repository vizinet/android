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
        return mSession.startDate;
    }

    @Override
    public Date getEndDate() {
        return mSession.endDate;
    }

    @Override
    public void setEndDate(Date value) {
        mRealm.beginTransaction();
        mSession.endDate = value;
        mRealm.commitTransaction();
    }


    @Override
    public Object getRaw() {
        return mSession;
    }
}
