// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.interface_object;

import edu.wsu.lar.airpact_fire.data.interface_object.SessionInterfaceObject;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.AppInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.data.realm.model.App;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Realm implementation of the {@link AppInterfaceObject}.
 */
public class RealmAppInterfaceObject implements AppInterfaceObject {

    private Realm mRealm;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmAppInterfaceObject(Realm realm, DataManager dataManager, DebugManager debugManager) {
        mRealm = realm;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public boolean getRememberUser() {
        final App app = mRealm.where(App.class).findFirst();
        return app.rememberUser;
    }

    @Override
    public void setRememberUser(boolean value) {
        final App app = mRealm.where(App.class).findFirst();
        mRealm.beginTransaction();
        app.rememberUser = value;
        mRealm.commitTransaction();
    }

    @Override
    public UserInterfaceObject getLastUser() {
        SessionInterfaceObject lastSession = getLastSession();
        if (lastSession == null) { return null; }
        return new RealmUserInterfaceObject(mRealm, lastSession.getUser().getUsername(),
                mDataManager, mDebugManager);
    }

    @Override
    public UserInterfaceObject getUser(String username, String password) {

        // Ensure username and password match-up in DB
        User matchUser = mRealm.where(User.class)
                .equalTo("username", username)
                .equalTo("password", password)
                .findFirst();
        return (matchUser == null)
                ? null
                : new RealmUserInterfaceObject(mRealm, matchUser, mDataManager, mDebugManager);
    }

    @Override
    public SessionInterfaceObject getLastSession() {

        // Get sessions ordered by date
        RealmResults<Session> orderedSessions = mRealm.where(Session.class).findAllSorted(
                "startDate", Sort.DESCENDING);
        if (orderedSessions.isEmpty()) { return null; }

        // Get user of most recent session
        Session lastSession = orderedSessions.first();
        return new RealmSessionInterfaceObject(mRealm, lastSession, mDataManager, mDebugManager);
    }

    @Override
    public SessionInterfaceObject getSession() {
        return null;
    }

    @Override
    public Object getRaw() {
        return null;
    }
}
