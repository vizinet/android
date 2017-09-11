// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.interface_object;

import android.graphics.Bitmap;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.UserInterfaceObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Image;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Realm implementation of the {@link PostInterfaceObject}.
 */
public class RealmPostInterfaceObject implements PostInterfaceObject {

    private Realm mRealm;
    private Post mPost;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmPostInterfaceObject(Realm realm, Post post, DataManager dataManager,
                                    DebugManager debugManager) {
        mRealm = realm;
        mPost = post;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public UserInterfaceObject getUser() {
        return new RealmUserInterfaceObject(mRealm, mPost.user, mDataManager, mDebugManager);
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
    public ImageInterfaceObject createImageObject() {
        mRealm.beginTransaction();
        Image imageModel = mRealm.createObject(Image.class, mDataManager.generateImageId());
        imageModel.postId = mPost.postId;
        mRealm.commitTransaction();
        return new RealmImageInterfaceObject(mRealm, imageModel, mDataManager, mDebugManager);
    }

    @Override
    public List<ImageInterfaceObject> getImageObjects() {
        RealmResults realmResults = mRealm.where(Image.class)
                .equalTo("postId", mPost.postId)
                .findAllSorted("imageId");
        List imageObjects = new ArrayList<ImageInterfaceObject>();
        for (Object image : realmResults) {
            ImageInterfaceObject imageInterfaceObject =
                    new RealmImageInterfaceObject(mRealm, (Image) image, mDataManager, mDebugManager);
            imageObjects.add(imageInterfaceObject);
        }
        return imageObjects;
    }

    @Override
    public Bitmap getThumbnail() {
        if (getAlgorithm() == 1) return getImageObjects().get(0).getBitmap();
        else if (getAlgorithm() == 2) return getImageObjects().get(1).getBitmap();
        else return null;
    }

    @Override
    public Bitmap getThumbnail(int width) {

        // TODO: Compression

        Bitmap bitmap;
        if (getAlgorithm() == 1) bitmap = getImageObjects().get(0).getBitmap();
        else if (getAlgorithm() == 2) bitmap = getImageObjects().get(1).getBitmap();
        else return null;

        bitmap = Bitmap.createScaledBitmap(bitmap, width,
                (int) ((width / (float) bitmap.getWidth()) * bitmap.getHeight()), false);

        return bitmap;
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

        // Delete associated images
        RealmResults<Image> imageResults = mRealm.where(Image.class)
                .equalTo("postId", mPost.postId).findAll();
        for (Image image : imageResults) {
            (new RealmImageInterfaceObject(mRealm, image, mDataManager, mDebugManager)).delete();
        }

        // Delete post object
        RealmResults<Post> postResults = mRealm.where(Post.class)
                .equalTo("postId", mPost.postId).findAll();
        postResults.deleteAllFromRealm();

        mRealm.commitTransaction();
    }

    @Override
    public JSONObject toJSON() {

        // TODO: Maybe send radii for the targets

        // Get algorithm for this post
        Algorithm algorithm = DataManager.getAlgorithm(getAlgorithm()).getInstance();
        JSONObject postJSON = algorithm.getServerContract().toJSON(this);

        return postJSON;
    }

    @Override
    public int getId() {
        return mPost.postId;
    }

    @Override
    public Object getRaw() {
        return mPost;
    }
}
