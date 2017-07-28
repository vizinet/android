// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import org.json.simple.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.wsu.lar.airpact_fire.app.Reference;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.TargetObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Image;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import edu.wsu.lar.airpact_fire.util.Util;
import io.realm.Realm;
import io.realm.RealmResults;

/** @see PostObject */
public class RealmPostObject implements PostObject {

    private Realm mRealm;
    private Post mPost;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmPostObject(Realm realm, Post post, DataManager dataManager,
                           DebugManager debugManager) {
        mRealm = realm;
        mPost = post;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public UserObject getUser() {
        return new RealmUserObject(mRealm, mPost.user, mDataManager, mDebugManager);
    }

    @Override
    public String getSecretKey() {
        return mPost.secretKey;
    }

    @Override
    public void setSecretKey(String value) {
        mRealm.beginTransaction();
        mPost.secretKey = value;
        mRealm.commitTransaction();
    }

    @Override
    public Date getDate() {
        return mPost.date;
    }

    @Override
    public void setDate(Date value) {
        mRealm.beginTransaction();
        mPost.date = value;
        mRealm.commitTransaction();
    }

    @Override
    public String getServerId() {
        return mPost.serverId;
    }

    @Override
    public void setServerId(String value) {
        mRealm.beginTransaction();
        mPost.serverId = value;
        mRealm.commitTransaction();
    }

    @Override
    public int getMetric() {
        return mPost.metric;
    }

    @Override
    public void setMetric(int value) {
        mRealm.beginTransaction();
        mPost.metric = value;
        mRealm.commitTransaction();
    }

    @Override
    public int getAlgorithm() {
        return mPost.algorithm;
    }

    @Override
    public void setAlgorithm(int value) {
        mRealm.beginTransaction();
        mPost.algorithm = value;
        mRealm.commitTransaction();
    }

    @Override
    public int getMode() {
        return mPost.mode;
    }

    @Override
    public void setMode(int value) {
        mRealm.beginTransaction();
        mPost.mode = value;
        mRealm.commitTransaction();
    }

    @Override
    public ImageObject createImageObject() {
        mRealm.beginTransaction();
        Image imageModel = mRealm.createObject(Image.class, mDataManager.generateImageId());
        imageModel.postId = mPost.postId;
        mRealm.commitTransaction();
        return new RealmImageObject(mRealm, imageModel, mDataManager, mDebugManager);
    }

    @Override
    public List<ImageObject> getImageObjects() {
        RealmResults realmResults = mRealm.where(Image.class)
                .equalTo("postId", mPost.postId)
                .findAllSorted("imageId");
        List imageObjects = new ArrayList<ImageObject>();
        for (Object image : realmResults) {
            ImageObject imageObject =
                    new RealmImageObject(mRealm, (Image) image, mDataManager, mDebugManager);
            imageObjects.add(imageObject);
        }
        return imageObjects;
    }

    @Override
    public float getEstimatedVisualRange() {
        return mPost.estimatedVisualRange;
    }

    @Override
    public void setEstimatedVisualRange(float value) {
        mRealm.beginTransaction();
        mPost.estimatedVisualRange = value;
        mRealm.commitTransaction();
    }

    @Override
    public float getComputedVisualRange() {
        return mPost.computedVisualRange;
    }

    @Override
    public void setComputedVisualRange(float value) {
        mRealm.beginTransaction();
        mPost.computedVisualRange = value;
        mRealm.commitTransaction();
    }

    @Override
    public String getDescription() {
        return mPost.description;
    }

    @Override
    public void setDescription(String value) {
        mRealm.beginTransaction();
        mPost.description = value;
        mRealm.commitTransaction();
    }

    @Override
    public String getLocation() {
        return mPost.location;
    }

    @Override
    public void setLocation(String value) {
        mRealm.beginTransaction();
        mPost.location = value;
        mRealm.commitTransaction();
    }

    @Override
    public void delete() {

        mRealm.beginTransaction();

        // Delete post object
        RealmResults<Post> postResults = mRealm.where(Post.class)
                .equalTo("postId", mPost.postId).findAll();
        postResults.deleteAllFromRealm();

        // Delete associated images
        RealmResults<Image> imageResults = mRealm.where(Image.class)
                .equalTo("postId", mPost.postId).findAll();
        for (Image image : imageResults) {
            (new RealmImageObject(mRealm, image, mDataManager, mDebugManager)).delete();
        }

        mRealm.commitTransaction();
    }

    @Override
    public JSONObject toJSON() {

        // TODO: Maybe send radii for the targets
        // TODO: Put this server post data in a ServerContract class which is mapped to the right algorithm

        // Get algorithm for this post
        Algorithm algorithm = null;
        try {
            // TODO: Improve our methods for retrieving both enums and lists from Reference...
            algorithm = Reference.ALGORITHMS[getAlgorithm() - 1].newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        JSONObject postJSON = algorithm.getServerContract().toJSON(this);

        return postJSON;
    }

    @Override
    public Object getRaw() {
        return mPost;
    }
}
