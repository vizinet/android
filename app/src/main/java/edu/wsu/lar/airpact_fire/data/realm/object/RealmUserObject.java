package edu.wsu.lar.airpact_fire.data.realm.object;

import java.util.Date;

import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Coordinate;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
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
    public void setGPS(double[] values) {
        mRealm.beginTransaction();
        Coordinate coordinate = mRealm.where(Post.class).equalTo("user.username", mUsername)
                .findFirst().geoCoordinate;
        coordinate.x = values[0];
        coordinate.y = values[1];
        mRealm.commitTransaction();
    }

    @Override
    public double[] getGPS() {
        Coordinate coordinate = mRealm.where(Post.class).equalTo("user.username", mUsername)
                .findFirst().geoCoordinate;
        return new double[] {coordinate.x, coordinate.y};
    }

    @Override
    // TODO: Forward to target selection activity when there is an active draft post
    public boolean getHasDraftPost() {
        return false;
    }

    @Override
    public void setHasDraftPost(boolean value) {

    }

    @Override
    public PostObject[] getPosts() {
        return new PostObject[0];
    }

    @Override
    public PostObject getLastPost() {
        Post post = mRealm.where(Post.class).equalTo("user.username", mUsername)
                .findAllSorted("date").first();
        return new RealmPostObject(mRealm, post, mDebugManager);
    }

    @Override
    public PostObject createPost() {
        return null;
    }

    @Override
    public int getDistanceMetric() {
        return mRealm.where(User.class).equalTo("username", mUsername).findFirst().distanceMetric;
    }

    @Override
    public void setDistanceMetric(int value) {
        mRealm.beginTransaction();
        mRealm.where(User.class).equalTo("username", mUsername).findFirst().distanceMetric = value;
        mRealm.commitTransaction();
    }

    @Override
    public Date getFirstLoginDate() {
        return null;
    }

    @Override
    public void getFirstLoginDate(Date value) {

    }
}
