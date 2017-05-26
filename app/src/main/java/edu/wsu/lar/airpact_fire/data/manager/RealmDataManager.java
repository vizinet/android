package edu.wsu.lar.airpact_fire.data.manager;

import android.content.Context;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import edu.wsu.lar.airpact_fire.data.model.App;
import edu.wsu.lar.airpact_fire.data.model.Session;
import edu.wsu.lar.airpact_fire.data.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;

public class RealmDataManager implements DataManager {

    private Realm mRealm;
    private DebugManager mDebugManager;
    private Map<String, Command> mAppMethodMap;
    private Map<String, Command> mUserMethodMap;

    interface Command {
        Object run(Object... args);
    }

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
    }

    @Override
    public void onActivityStart(Object... args) {

        final Context context = (Context) args[0];

        // Initialize Realm for this activity
        Realm.init(context);

        // Get a Realm instance for this thread
        mRealm = Realm.getDefaultInstance();

        mAppMethodMap = new HashMap();
        mAppMethodMap.put("rememberPassword", new Command() {
            @Override
            public Object run(Object... args) {
                return appRememberPassword(args);
            }
        });
        mAppMethodMap.put("lastUser", new Command() {
            @Override
            public Object run(Object... args) {
                return appLastUser(args);
            }
        });

        // TODO: Trigger/subscription setups
    }

    // Called at the end of a new activity
    @Override
    public void onActivityEnd(Object... args) {
        // TODO: Close triggers/subscriptions
    }

    @Override
    public void onLogin(Object... args) {

        final String username = (String) args[0];
        final String password = (String) args[1];

        // Get user or create one if nonexistent
        User user = getUser(username, password);
        if (user == null) {
            mRealm.beginTransaction();
            user = mRealm.createObject(User.class, username); // Primary key
            user.password = password;
            mRealm.commitTransaction();
        }

        // Start session
        // TODO: Trigger app.lastUser = user when a Session is created
        mRealm.beginTransaction();
        Session session = mRealm.createObject(Session.class);
        session.startTime = new Date(DATE_FORMAT);
        user.sessions.add(session);
        App app = getApp();
        app.lastUser = user; // TODO: Remove
        mRealm.commitTransaction();
    }

    @Override
    public void onLogout(Object... args) {

        // End session
        mRealm.beginTransaction();
        Session session = getCurrentSession();
        session.endTime = new Date(DATE_FORMAT);
        mRealm.commitTransaction();
    }

    @Override
    public void onAppStart(Object... args) {

    }

    @Override
    public void onAppEnd(Object... args) {

    }

    /* Data manipulation methods */

    @Override
    public Object getAppField(String fieldName) {
        Object fieldValue = mAppMethodMap.get(fieldName).run();
        mDebugManager.printLog(String.format("getAppField(%s) -> %s", fieldName, fieldValue.toString()));
        return fieldValue;
    }

    @Override
    public void setAppField(String fieldName, Object fieldValue) {
        mAppMethodMap.get(fieldName).run(fieldValue);
        mDebugManager.printLog(String.format("setAppField(%s, %s)", fieldName, fieldValue));
        getAppField(fieldName);
    }

    // Getter/setter of app's rememberPassword field
    private boolean appRememberPassword(Object... args) {
        App app = getApp();
        if (args.length == 0) { return app.rememberPassword; }
        mRealm.beginTransaction();
        app.rememberPassword = (boolean) args[0];
        mRealm.commitTransaction();
        return false;
    }

    // Getter/setter of app's lastUser field
    private User appLastUser(Object... args) {
        App app = getApp();
        if (args.length == 0) { return app.lastUser; }
        mRealm.beginTransaction();
        app.lastUser = (User) args[0];
        mRealm.commitTransaction();
        return null;
    }

    // TODO: Get/set methods for user

    @Override
    public Object getUserField(String fieldName) {
        return null;
    }

    @Override
    public void setUserField(String fieldName, String fieldValue) {

    }

    /* TODO: Assign section */

    @Override
    public boolean isUser(String username, String password) {
        return getUser(username, password) == null ? false : true;
    }

    @Override
    public User getUser(String username, String password) {
        final User user = mRealm.where(User.class)
                .equalTo("username", username)
                .equalTo("password", password)
                .findFirst();

        // TODO: Ensure this returns null if user isn't found
        return user;
    }

    @Override
    public App getApp() {
        final App app = mRealm.where(App.class).findFirst();
        return app;
    }

    @Override
    public String getLastUser() {
        return getApp().lastUser.toString();
    }

    public String getLastSession() { return null; }

    // Return user in the current app session
    @Override
    public User getCurrentUser() {
        // TODO
        return null;
    }

    @Override
    public Session getCurrentSession() {
        // TODO
        return null;
    }
}
