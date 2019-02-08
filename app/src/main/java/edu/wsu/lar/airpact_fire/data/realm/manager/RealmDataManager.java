// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.manager;

import android.app.Activity;
import android.content.Context;

import java.text.SimpleDateFormat;

import edu.wsu.lar.airpact_fire.app.Constant;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.AppInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.data.realm.FireRealmMigration;
import edu.wsu.lar.airpact_fire.data.realm.model.App;
import edu.wsu.lar.airpact_fire.data.realm.model.Image;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.Target;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.data.realm.interface_object.RealmAppInterfaceObject;
import edu.wsu.lar.airpact_fire.data.realm.interface_object.RealmUserInterfaceObject;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.util.Util;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Realm implementation of the {@link DataManager}.
 *
 * <p>Uses {@link io.realm.RealmModel} objects under the hood
 * to store data.</p>
 */
public class RealmDataManager extends DataManager {

    // NOTE: Increment the version every time the Realm schema is updated.
    public static final int SCHEMA_VERSION = 1;

    private Realm mRealm;
    private DebugManager mDebugManager;
    private Activity mActivity;
    private boolean mIsInit = false;

    public RealmDataManager(DebugManager debugManager, Activity activity) {
        mDebugManager = debugManager;
        mActivity = activity;
    }

    /* Activity lifecycle methods */

    @Override
    public void onAppFirstRun(Object... args) {
        // Create app model with default fields at app's conception.
        mRealm = getRealmInstance((Context) args[0]);
        mRealm.executeTransaction(realm -> {
            App app = mRealm.createObject(App.class);
            app.lastUser = null;
            app.rememberUser = false;
        });
    }

    @Override
    public void onActivityStart(Object... args) {
        mRealm = getRealmInstance((Context) args[0]);
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

        initRealm((Context) args[0]);

        RealmConfiguration config = new RealmConfiguration.Builder()
            .schemaVersion(SCHEMA_VERSION)          // Must be bumped when the schema changes.
            .migration(new FireRealmMigration())    // Migration to run
            .build();
        Realm.setDefaultConfiguration(config);
        mDebugManager.printLog(String.format(
                "Realm database successfully migrated to schema version = %d.", SCHEMA_VERSION));
    }

    @Override
    public void onAppEnd(Object... args) {
    }

    /* Utilities */

    /**
      * Initialize Realm for this activity.
      */
    private void initRealm(Context context) {
        if (!mIsInit) {
            Realm.init(context);
            mIsInit = true;
        }
    }

    /**
     * Get new/existing `Realm` instance for this thread.
     *
     * @return `Realm` instance.
     */
    private Realm getRealmInstance(Context context) {
        initRealm(context);
        return (mRealm != null) ? mRealm : Realm.getDefaultInstance();
    }

    // Get user or create one if nonexistent
    private UserInterfaceObject createOrReturnUser(String username, String password) {

        mDebugManager.printLog("Create user if none");
        User userModel = getUser(username);
        mDebugManager.printLog("userModel = " + userModel);

        if (userModel == null) {
            mRealm.beginTransaction();
            userModel = mRealm.createObject(User.class, username); // Primary key
            userModel.password = password;
            userModel.distanceMetric = DataManager.DEFAULT_DISTANCE_METRIC;
            mRealm.commitTransaction();
            mDebugManager.printLog("User created!");
        }

        return new RealmUserInterfaceObject(mRealm, userModel, this, mDebugManager);
    }

    // Start new session with given user
    private void startSession(String username) {

        mDebugManager.printLog("Start session");

        // Start session
        mRealm.beginTransaction();
        User userModel = getUser(username);
        Session session = mRealm.createObject(Session.class, generateSessionId());
        session.startDate = new SimpleDateFormat(Constant.DATE_FORMAT).format(Util.getCurrentDate());
        session.user = userModel;
        userModel.sessions.add(session);
        mRealm.commitTransaction();

        mDebugManager.printLog("Created new session");
    }

    // End current session
    private void endSession() {

        mRealm.beginTransaction();
        Session session = (Session) getApp().getLastSession().getRaw();
        session.endDate = new SimpleDateFormat(Constant.DATE_FORMAT).format(Util.getCurrentDate());
        mRealm.commitTransaction();

        mDebugManager.printLog("Ended the session");
    }

    // Internal get-user method
    private User getUser(String username) {
        final RealmResults<User> results = mRealm.where(User.class).equalTo("username", username)
                .findAll();
        if (results.isEmpty()) { return null; }
        return results.first();
    }

    @Override
    public AppInterfaceObject getApp() {
        return new RealmAppInterfaceObject(mRealm, this, mDebugManager);
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }

    @Override
    public int generateSessionId() {
        RealmResults<Session> results = mRealm.where(Session.class).findAll();
        if (results == null) { return 0; }
        Number currentSessionId = mRealm.where(Session.class).max("sessionId");
        return (currentSessionId == null) ? 0 : currentSessionId.intValue() + 1;
    }

    @Override
    public int generatePostId() {
        RealmResults<Post> results = mRealm.where(Post.class).findAll();
        if (results == null) { return 0; }
        Number currentPostId = results.max("postId");
        return (currentPostId == null) ? 0 : currentPostId.intValue() + 1;
    }

    @Override
    public int generateImageId() {
        RealmResults<Image> results = mRealm.where(Image.class).findAll();
        if (results == null || results.size() == 0) { return 0; }
        Number currentImageId = results.max("imageId");
        return (currentImageId == null) ? 0 : currentImageId.intValue() + 1;
    }

    @Override
    public int generateTargetId() {
        RealmResults<Target> results = mRealm.where(Target.class).findAll();
        if (results == null || results.size() == 0) { return 0; }
        Number currentTargetId = results.max("targetId");
        return (currentTargetId == null) ? 0 : currentTargetId.intValue() + 1;
    }
}
