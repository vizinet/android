package edu.wsu.lar.airpact_fire.data.realm.object;

import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
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

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public PostObject[] getPosts() {
        return new PostObject[0];
    }

    @Override
    public PostObject createPost() {
        return null;
    }
}
