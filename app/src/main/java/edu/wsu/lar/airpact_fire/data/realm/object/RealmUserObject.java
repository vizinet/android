package edu.wsu.lar.airpact_fire.data.realm.object;

import java.util.Date;

import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;

/**
 * @see UserObject
 */
public class RealmUserObject implements UserObject {

    private Realm mRealm;
    private String mUsername;
    private DebugManager mDebugManager;

    public RealmUserObject(Realm realm, String username, DebugManager debugManager) {
        mRealm = realm;
        mUsername = username;
        mDebugManager = debugManager;
    }

    public RealmUserObject(Realm realm, User userModel, DebugManager debugManager) {
        mRealm = realm;
        mUsername = userModel.username;
        mDebugManager = debugManager;
    }

    @Override
    public String getUsername() {
        return mUsername;
    }

    @Override
    public String getPassword() {
        return mRealm.where(User.class).equalTo("username", mUsername).findFirst().password;
    }

    @Override
    public PostObject[] getPosts() {
        return new PostObject[0];
    }

    @Override
    public PostObject createPost() {
        return null;
    }

    @Override
    public Date getFirstLoginDate() {
        return null;
    }

    @Override
    public void getFirstLoginDate(Date value) {

    }
}
