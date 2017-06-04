// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm;

import android.content.Context;
import java.util.Date;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.AppObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.App;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.data.realm.object.RealmAppObject;
import edu.wsu.lar.airpact_fire.data.realm.object.RealmUserObject;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.util.Util;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmDataManager implements DataManager {

    private Realm mRealm;
    private DebugManager mDebugManager;

    public RealmDataManager(DebugManager debugManager) {
        mDebugManager = debugManager;
    }

    /* Activity lifecycle methods */

    @Override
    public void onAppFirstRun(Object... args) {

        // Create app model with default fields at app's conception
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                App app = mRealm.createObject(App.class);
                app.lastUser = null;
                app.rememberPassword = false;
            }
        });

        // Setup Realm object notifications
        setupRealmObjectNotifications();
    }

    @Override
    public void onActivityStart(Object... args) {

        final Context context = (Context) args[0];

        // Initialize Realm for this activity
        Realm.init(context);

        // Get a Realm instance for this thread
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onActivityEnd(Object... args) {
        // TODO: Close triggers/subscriptions
    }

    @Override
    public void onLogin(Object... args) {

        final String username = (String) args[0];
        final String password = (String) args[1];

        createOrReturnUser(username, password);
        startSession(username);
    }

    @Override
    public void onLogout(Object... args) {
        endSession();
    }

    @Override
    public void onAppStart(Object... args) {
        mDebugManager.printLog("App started!");
    }

    @Override
    public void onAppEnd(Object... args) {

    }

    /* Data field manipulation methods */

    /* TODO: Move these methods to the correct DataObject methods
    private boolean appRememberPassword(Object... args) {
        App app = getApp();
        if (args.length == 0) { return app.rememberPassword; }
        mRealm.beginTransaction();
        app.rememberPassword = (boolean) args[0];
        mRealm.commitTransaction();
        return false;
    }

    private User appLastUser(Object... args) {
        App app = getApp();
        if (args.length == 0) { return app.lastUser; }
        mRealm.beginTransaction();
        app.lastUser = (User) args[0];
        mRealm.commitTransaction();
        return null;
    }

    private String userUsername(Object... args) {
        User user = (User) fieldAccess(DataField.APP_LAST_USER);
        if (args.length == 0) { return user.username; }
        mRealm.beginTransaction();
        user.username = (String) args[0];
        mRealm.commitTransaction();
        return null;
    }

    private String userPassword(Object... args) {
        User user = (User) fieldAccess(DataField.APP_LAST_USER);
        if (args.length == 0) { return user.password; }
        mRealm.beginTransaction();
        user.password = (String) args[0];
        mRealm.commitTransaction();
        return null;
    }

    private Date userFirstLoginDate(Object... args) {
        User user = (User) fieldAccess(DataField.APP_LAST_USER);
        if (args.length == 0) { return user.firstLoginDate; }
        mRealm.beginTransaction();
        user.firstLoginDate = (Date) args[0];
        mRealm.commitTransaction();
        return null;
    }

    private Date userLastLoginDate(Object... args) {
        User user = (User) fieldAccess(DataField.APP_LAST_USER);
        if (args.length == 0) { return user.lastLoginDate; }
        mRealm.beginTransaction();
        user.lastLoginDate = (Date) args[0];
        mRealm.commitTransaction();
        return null;
    }

    private String userDistanceMetric(Object... args) {
        User user = (User) fieldAccess(DataField.APP_LAST_USER);
        if (args.length == 0) { return user.distanceMetric; }
        mRealm.beginTransaction();
        user.distanceMetric = (String) args[0];
        mRealm.commitTransaction();
        return null;
    }
    */

    /* Utilities */

    // Get user or create one if nonexistent
    private UserObject createOrReturnUser(String username, String password) {

        mDebugManager.printLog("Create user if none");
        User userModel = getUser(username);
        mDebugManager.printLog("userModel = " + userModel);

        if (userModel == null) {
            mRealm.beginTransaction();
            userModel = mRealm.createObject(User.class, username); // Primary key
            userModel.password = password;
            mRealm.commitTransaction();
            mDebugManager.printLog("User created!");
        }

        return new RealmUserObject(mRealm, userModel, mDebugManager);
    }

    // Start new session with given user
    private void startSession(String username) {

        mDebugManager.printLog("Start session");

        // Start session
        mRealm.beginTransaction();
        User userModel = getUser(username);
        Session session = mRealm.createObject(Session.class, generateSessionId());
        session.startDate = Util.getCurrentDate();
        session.user = userModel;
        userModel.sessions.add(session);
        mRealm.commitTransaction();

        // TODO: Trigger app.lastUser = user when a Session is created
        mDebugManager.printLog("Created new session");
    }

    // End current session
    private void endSession() {
        mRealm.beginTransaction();
        Session session = getLastSession();
        session.endDate = new Date(DATE_FORMAT);
        mRealm.commitTransaction();

        mDebugManager.printLog("Ended the session");
    }

    // Internal get-user method
    private User getUser(String username) {
        final RealmResults<User> results = mRealm.where(User.class).findAll();
        if (results.isEmpty()) { return null; }
        return results.first();
    }

    private Session getLastSession() {
        // TODO: See if right order
        return mRealm.where(Session.class).findAllSorted("startDate", Sort.DESCENDING).first();
    }

    private void setupRealmObjectNotifications() {

        RealmObjectChangeListener<Session> listener = new RealmObjectChangeListener<Session>() {
            @Override
            public void onChange(Session dog, ObjectChangeSet changeSet) {
                if (changeSet.isDeleted()) {
                    mDebugManager.printLog("The dog was deleted");
                    return;
                }

                for (String fieldName : changeSet.getChangedFields()) {
                    mDebugManager.printLog("Field " + fieldName + " was changed.");
                }
            }
        };
    }

    @Override
    public AppObject getApp() {
        return new RealmAppObject(mRealm, mDebugManager);
    }

    @Override
    public int generateSessionId() {
        Number currentSessionId = mRealm.where(Session.class).max("sessionId");
        return (currentSessionId == null) ? 0 : currentSessionId.intValue() + 1;
    }

}
