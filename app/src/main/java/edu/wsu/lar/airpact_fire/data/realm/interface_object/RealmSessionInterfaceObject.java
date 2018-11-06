// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.interface_object;

import edu.wsu.lar.airpact_fire.data.interface_object.SessionInterfaceObject;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;

/**
 * Realm implementation of the {@link SessionInterfaceObject}.
 */
public class RealmSessionInterfaceObject implements SessionInterfaceObject {

    private Realm mRealm;
    private Session mSession;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmSessionInterfaceObject(Realm realm, Session session, DataManager dataManager,
                                       DebugManager debugManager) {
        mRealm = realm;
        mSession = session;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public UserInterfaceObject getUser() {
        return new RealmUserInterfaceObject(mRealm, mSession.user, mDataManager, mDebugManager);
    }

    @Override
    public String getStartDate() {
        return mSession.startDate;
    }

    @Override
    public String getEndDate() {
        return mSession.endDate;
    }

    @Override
    public void setEndDate(String value) {
        mRealm.beginTransaction();
        mSession.endDate = value;
        mRealm.commitTransaction();
    }


    @Override
    public Object getRaw() {
        return mSession;
    }
}
