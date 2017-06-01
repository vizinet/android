// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import edu.wsu.lar.airpact_fire.data.object.AppObject;
import edu.wsu.lar.airpact_fire.data.object.SessionObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.App;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.util.Util;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * @see AppObject
 */
public class RealmAppObject implements AppObject {

    private Realm mRealm;
    private DebugManager mDebugManager;

    public RealmAppObject(Realm realm, DebugManager debugManager) {
        mRealm = realm;
        mDebugManager = debugManager;
    }

    // TODO: See if this truly returns the last user
    public UserObject getUser() {
        final RealmResults<User> results = mRealm.where(User.class).findAll();
        return new RealmUserObject(mRealm, results.first().username, mDebugManager);
    }

    @Override
    public void startSession(String username, String password) {

        mRealm.beginTransaction();

        // Get user or create one if nonexistent
        // TODO: Use Realm
        UserObject userObject = getUser(username);
        if (userObject == null) {
            User userModel = mRealm.createObject(User.class, username); // Primary key
            userModel.password = password;
        }

        // Start session
        Session session = mRealm.createObject(Session.class);
        session.startTime = Util.getCurrentDate();
        user.sessions.add(session);

        // TODO: Trigger app.lastUser = user when a Session is created
        mDebugManager.printLog("Created new session and set last user of app");

        mRealm.commitTransaction();
    }

    @Override
    public void endSession() {

    }

    @Override
    public void createUser(String username, String password) {

    }

    @Override
    public boolean getRememberPassword() {
        return false;
    }

    @Override
    public void setRememberPassword(boolean value) {
        final App app = mRealm.where(App.class).findFirst();
    }

    @Override
    public UserObject getLastUser() {
        return null;
    }

    public UserObject getUser(String username) { return null; }

    @Override
    public SessionObject getLastSession() {
        return null;
    }

    @Override
    public SessionObject getSession() {
        return null;
    }
}
