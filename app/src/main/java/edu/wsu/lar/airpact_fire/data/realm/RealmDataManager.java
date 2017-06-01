// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm;

import android.content.Context;
import java.util.Date;
import java.util.Map;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.AppObject;
import edu.wsu.lar.airpact_fire.data.realm.model.App;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.data.realm.object.RealmAppObject;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmObjectChangeListener;

// TODO: Make sure to return the Realm version of our data.objects

public class RealmDataManager implements DataManager {

    private Realm mRealm;
    private DebugManager mDebugManager;
    private Map<Object, Object> mDataFieldCommandMap;

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

        // Data field -> method
        /*
        mDataFieldCommandMap = new HashMap();
        mDataFieldCommandMap.put(DataField.APP_LAST_USER, new Command() {
            @Override
            public Object run(Object... args) {
                return appLastUser(args);
            }
        });
        mDataFieldCommandMap.put(DataField.APP_REMEMBER_PASSWORD, new Command() {
            @Override
            public Object run(Object... args) {
                return appRememberPassword(args);
            }
        });
        mDataFieldCommandMap.put(DataField.USER_USERNAME, new Command() {
            @Override
            public Object run(Object... args) {
                return userUsername(args);
            }
        });
        mDataFieldCommandMap.put(DataField.USER_PASSWORD, new Command() {
            @Override
            public Object run(Object... args) {
                return userPassword(args);
            }
        });
        mDataFieldCommandMap.put(DataField.USER_FIRST_LOGIN_DATE, new Command() {
            @Override
            public Object run(Object... args) {
                return userFirstLoginDate(args);
            }
        });
        mDataFieldCommandMap.put(DataField.USER_LAST_LOGIN_DATE, new Command() {
            @Override
            public Object run(Object... args) {
                return userLastLoginDate(args);
            }
        });
        mDataFieldCommandMap.put(DataField.USER_DISTANCE_METRIC, new Command() {
            @Override
            public Object run(Object... args) {
                return userDistanceMetric(args);
            }
        });
        */
    }

    @Override
    public void onActivityEnd(Object... args) {
        // TODO: Close triggers/subscriptions
    }

    @Override
    public void onLogin(Object... args) {

        final String username = (String) args[0];
        final String password = (String) args[1];

        getApp().startSession(username, password);
    }

    @Override
    public void onLogout(Object... args) {

        // End session
        mRealm.beginTransaction();
        Session session = getLastSession();
        session.endTime = new Date(DATE_FORMAT);
        mRealm.commitTransaction();
    }

    @Override
    public void onAppStart(Object... args) {
        mDebugManager.printLog("App started!");
    }

    @Override
    public void onAppEnd(Object... args) {

    }

    /* Data field manipulation methods */

    @Override
    public Object fieldAccess(Object... args) {

        if (args.length == 1) {

            // Getter
            DataField field = (DataField) args[0];
            Object value = mDataFieldCommandMap.get(field).run();
            mDebugManager.printLog(String.format("fieldAccess(%s) -> %s", field.name(), value.toString()));
            return value;

        } else if (args.length == 2) {

            // Setter
            DataField field = (DataField) args[0];
            Object value = args[1];
            mDataFieldCommandMap.get(field).run(value);
            mDebugManager.printLog(String.format("fieldAccess(%s, %s)", field.name(), value.toString()));
            return null;
        }

        // Follow-up
        throw new IllegalArgumentException();
    }

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

    /* ... */

    private void setupRealmObjectNotifications() {

    }

    private final RealmObjectChangeListener<Session> listener = new RealmObjectChangeListener<Session>() {
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

    @Override
    public AppObject getApp() {
        return new RealmAppObject(mRealm);
    }

    @Override
    public Session getLastSession() { return null; }

}
