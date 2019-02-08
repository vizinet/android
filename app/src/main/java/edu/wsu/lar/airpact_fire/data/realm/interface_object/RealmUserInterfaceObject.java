package edu.wsu.lar.airpact_fire.data.realm.interface_object;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.SessionInterfaceObject;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Realm implementation of the {@link UserInterfaceObject}.
 */
public class RealmUserInterfaceObject implements UserInterfaceObject {

    private Realm mRealm;
    private User mUser;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmUserInterfaceObject(Realm realm, String username, DataManager dataManager,
                                    DebugManager debugManager) {
        this(realm, realm.where(User.class).equalTo("username", username).findFirst(),
                dataManager, debugManager);
    }

    public RealmUserInterfaceObject(Realm realm, User user, DataManager dataManager,
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

    @Override
    public List<SessionInterfaceObject> getSessions() {
        RealmResults results = mRealm.where(Session.class).equalTo("user.username", mUser.username)
                .findAllSorted("sessionId", Sort.ASCENDING);
        if (results == null || results.size() == 0) { return null; }
        List<SessionInterfaceObject> sessions = new ArrayList<>();
        for (Object post : results) {
           sessions.add(new RealmSessionInterfaceObject(mRealm, (Session) post, mDataManager, mDebugManager));
        }
        return sessions;
    }


    @Override
    public List<PostInterfaceObject> getPosts() {
        RealmResults results = mRealm.where(Post.class).equalTo("user.username", mUser.username)
                .findAllSorted("postId", Sort.DESCENDING);
        List<PostInterfaceObject> posts = new ArrayList<>();
        if (results == null || results.size() == 0) {
            // Return empty array
            return posts;
        }
        for (Object post : results) {
           posts.add(new RealmPostInterfaceObject(mRealm, (Post) post, mDataManager, mDebugManager));
        }
        return posts;
    }

    @Override
    public List<PostInterfaceObject> getPosts(int start, int end) {
        return null;
    }

    @Override
    public PostInterfaceObject getPost(int id) {
        Post post = mRealm.where(Post.class).equalTo("postId", id).findFirst();
        return new RealmPostInterfaceObject(mRealm, post, mDataManager, mDebugManager);
    }

    @Override
    public PostInterfaceObject getLastPost() {
        Post post = mRealm.where(Post.class).equalTo("user.username", mUser.username)
                .findAllSorted("date").first();
        return new RealmPostInterfaceObject(mRealm, post, mDataManager, mDebugManager);
    }

    @Override
    public PostInterfaceObject createPost() {

        mRealm.beginTransaction();
        Post postModel = mRealm.createObject(Post.class, mDataManager.generatePostId());
        postModel.user = mUser;
        postModel.mode = DataManager.DEFAULT_POST_MODE;
        postModel.algorithm = DataManager.DEFAULT_ALGORITHM;
        mRealm.copyToRealmOrUpdate(mUser);
        mRealm.commitTransaction();

        return new RealmPostInterfaceObject(mRealm, postModel, mDataManager, mDebugManager);
    }

    @Override
    public LatLng getRecentLatLng() {
        return new LatLng(mUser.lastLatitude, mUser.lastLongitude);
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
    public String getFirstLoginDate() {
        return getSessions().get(0).getStartDate();
    }

    @Override
    public void getFirstLoginDate(Date value) {

    }

    @Override
    public Object getRaw() {
        return mUser;
    }
}
