// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.AppObject;
import edu.wsu.lar.airpact_fire.data.object.SessionObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.App;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/** @see AppObject */
public class RealmAppObject implements AppObject {

    private Realm mRealm;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmAppObject(Realm realm, DataManager dataManager, DebugManager debugManager) {
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
    public UserObject getLastUser() {

        SessionObject lastSession = getLastSession();
        return new RealmUserObject(mRealm, lastSession.getUser().getUsername(),
                mDataManager, mDebugManager);
    }

    @Override
    public UserObject getUser(String username, String password) {

        // Ensure username and password match-up in DB
        User matchUser = mRealm.where(User.class)
                .equalTo("username", username)
                .equalTo("password", password)
                .findFirst();
        return (matchUser == null)
                ? null
                : new RealmUserObject(mRealm, matchUser, mDataManager, mDebugManager);
    }

    @Override
    public SessionObject getLastSession() {

        // Get sessions ordered by date
        RealmResults<Session> orderedSessions = mRealm.where(Session.class).findAllSorted(
                "startDate", Sort.DESCENDING);
        if (orderedSessions.isEmpty()) { return null; }

        // Get user of most recent session
        Session lastSession = orderedSessions.first();
        return new RealmSessionObject(mRealm, lastSession, mDataManager, mDebugManager);
    }

    @Override
    public SessionObject getSession() {
        return null;
    }

    @Override
    public Object getRaw() {
        return null;
    }
}
