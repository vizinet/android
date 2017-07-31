package edu.wsu.lar.airpact_fire.data.realm.object;

import java.util.Date;
import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;

/** @see UserObject */
public class RealmUserObject implements UserObject {

    private Realm mRealm;
    private User mUser;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmUserObject(Realm realm, String username, DataManager dataManager,
                           DebugManager debugManager) {
        this(realm, realm.where(User.class).equalTo("username", username).findFirst(),
                dataManager, debugManager);
    }

    public RealmUserObject(Realm realm, User user, DataManager dataManager,
                           DebugManager debugManager) {
        mRealm = realm;
        mUser = user;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public String getUsername() {
        return mUser.username;
    }

    @Override
    public String getPassword() {
        return mUser.password;
    }

    @Override
    public boolean getRememberAlgorithmChoice() {
        return mUser.rememberAlgorithmChoice;
    }

    @Override
    public void setRememberAlgorithmChoice(boolean value) {
        mRealm.beginTransaction();
        mUser.rememberAlgorithmChoice = value;
        mRealm.commitTransaction();
    }

    @Override
    // TODO: Forward to target selection activity when there is an active draft post
    public boolean getHasDraftPost() {
        return false;
    }

    @Override
    public void setHasDraftPost(boolean value) {
        // TODO
    }

    // TODO

    @Override
    public PostObject[] getPosts() {
        return new PostObject[0];
    }

    @Override
    public PostObject[] getPosts(int start, int end) {
        return new PostObject[0];
    }

    @Override
    public PostObject getLastPost() {
        Post post = mRealm.where(Post.class).equalTo("user.username", mUser.username)
                .findAllSorted("date").first();
        return new RealmPostObject(mRealm, post, mDataManager, mDebugManager);
    }

    @Override
    public PostObject createPost() {
        mRealm.beginTransaction();
        Post postModel = mRealm.createObject(Post.class, mDataManager.generatePostId());
        postModel.user = mUser;
        postModel.mode = Reference.DEFAULT_POST_MODE;
        postModel.algorithm = Reference.DEFAULT_ALGORITHM;
        mRealm.copyToRealmOrUpdate(mUser);
        mRealm.commitTransaction();

        return new RealmPostObject(mRealm, postModel, mDataManager, mDebugManager);
    }

    @Override
    public int getDistanceMetric() {
        return mUser.distanceMetric;
    }

    @Override
    public void setDistanceMetric(int value) {
        mRealm.beginTransaction();
        mUser.distanceMetric = value;
        mRealm.commitTransaction();
    }

    @Override
    public Date getFirstLoginDate() {
        return null;
    }

    @Override
    public void getFirstLoginDate(Date value) {

    }

    @Override
    public Object getRaw() {
        return mUser;
    }
}
