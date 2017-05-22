package edu.wsu.lar.airpact_fire.data.manager;

import android.content.Context;
import java.lang.reflect.Field;
import java.util.Date;
import edu.wsu.lar.airpact_fire.data.model.App;
import edu.wsu.lar.airpact_fire.data.model.Session;
import edu.wsu.lar.airpact_fire.data.model.User;
import io.realm.Realm;

/**
 * This class consists variables and methods which abide by this app's data
 * manager interface for handling this app's persistently stored information
 * under the Realm database management platform.
 *
 * <p>This data manager is always run on the UI thread and will be constructed/
 * deconstructed with the corresponding life-cycle of each succeeding activity.
 * This class must be initialized for its methods to be called.</p>
 *
 * @author  Luke Weber
 * @see     DataManager
 * @see     Realm
 * @since   0.9
 */
public class RealmDataManager implements DataManager {

    // Data standards
    public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    public static final double[] GPS_DEFAULT_LOC = {46.73267, -117.163454}; // Pullman, WA

    private Realm mRealm;

    /* Activity Lifecycle Methods */

    @Override
    public void onAppFirstRun() {
        // Create app
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.createObject(App.class);
            }
        });
    }

    // Called at the start of a new activity (basically our constructor)
    public void onActivityStart(Context context) {

        // Initialize Realm
        Realm.init(context);

        // Get a Realm instance for this thread
        mRealm = Realm.getDefaultInstance();

        // TODO: Trigger/subscription setups
    }

    // Called at the end of a new activity
    public void onActivityEnd() {
        // TODO: Close triggers/subscriptions
    }

    @Override
    // NOTE: These user credentials must be authenticated with server
    // Called once user (successfully) logs into our app
    public void onLogin(final String username, final String password) {

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
    public void onLogout() {

        // End session
        mRealm.beginTransaction();
        Session session = getCurrentSession();
        session.endTime = new Date(DATE_FORMAT);
        mRealm.commitTransaction();
    }

    @Override
    // Called once app starts
    public void onAppStart() {

    }

    @Override
    // Called at the end of the app
    public void onAppEnd() {

    }

    /* Data Manipulation Methods */

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
    public void rememberPassword(final boolean b) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getApp().rememberPassword = b;
            }
        });
    }

    @Override
    public boolean rememberPassword() {
        return getApp().rememberPassword;
    }

    @Override
    // Returns the last user logged into the database
    public String getLastUser() {
        return getApp().lastUser.username;
    }

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

    @Override
    // Use reflection to get field value
    public String getUserField(String fieldName) {
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
    public Object setUserField(String fieldName, Object fieldValue) {
        User user = getCurrentUser();
        final User matchingUsers = mRealm.where(User.class)
                .equalTo("username", user.username)
                .equalTo("password", user.password)
                .findFirst();

        return null;
    }
}
