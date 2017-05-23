package edu.wsu.lar.airpact_fire.data.manager;

import android.content.Context;
import java.lang.reflect.Field;
import java.util.Date;
import edu.wsu.lar.airpact_fire.data.model.App;
import edu.wsu.lar.airpact_fire.data.model.Session;
import edu.wsu.lar.airpact_fire.data.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmFieldType;

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
                mRealm.beginTransaction();
                App app = mRealm.createObject(App.class);
                app.lastUser = null;
                app.rememberPassword = false;
                mRealm.commitTransaction();
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

        App app = getApp();
        String fieldValue = null;
        try {
            Field field = app.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldValue = field.get(app).toString();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mDebugManager.printLog(String.format("getAppField(%s) -> %s", fieldName, fieldValue));
        return fieldValue;
    }

    // TODO: Ensure the passed type is Realm compatible (RealmFieldType doesn't work)

    @Override
    public void setAppField(String fieldName, RealmFieldType fieldValue) {

        App app = getApp();
        mRealm.beginTransaction();
        try {
            Field field = app.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(app, fieldValue);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mRealm.commitTransaction();
        mDebugManager.printLog(String.format("setAppField(%s, %s)", fieldName, fieldValue));
    }

    @Override
    public Object getUserField(String fieldName) {

        User user = getCurrentUser();
        String value = null;
        try {
            Field field = user.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            value = field.get(user).toString();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void setUserField(String fieldName, String fieldValue) {

        User user = getCurrentUser();
        mRealm.beginTransaction();
        try {
            Field field = user.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(user, fieldValue);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mRealm.commitTransaction();
    }

    // TODO: Give each database model an explicit field name to stand on its own or to update
    // its value given a trigger from somewhere else

    // TODO: Make sure we can get a field value given its name

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
