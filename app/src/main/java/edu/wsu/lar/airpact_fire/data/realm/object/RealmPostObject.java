// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import org.json.simple.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Coordinate;
import edu.wsu.lar.airpact_fire.data.realm.model.Image;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import edu.wsu.lar.airpact_fire.util.Util;
import io.realm.Realm;
import io.realm.RealmList;

/** @see PostObject */
public class RealmPostObject implements PostObject {

    private Realm mRealm;
    private Post mPost;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmPostObject(Realm realm, int postId, DataManager dataManager,
                           DebugManager debugManager) {
        this(realm, realm.where(Post.class).equalTo("postId", postId).findFirst(),
                dataManager, debugManager);
    }

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
        // TODO
        return null;
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

    // TODO: Something cooler so that this Data object doesn't need to know any server specs!

    @Override
    public JSONObject toJSON() {

        // Intermediate objects
        float[][] targetCoordinates = getTargetsCoordinates();

        // TODO: Maybe send radii for the targets
        // TODO: Put this server post data in a ServerContract class which is mapped to the right
        // algorithm


        // Post submission field vars => JSON
        JSONObject root = new JSONObject();
        root.put("user", getUser().getUsername());
        root.put("description", getDescription());
        root.put("secretKey", getSecretKey());
        root.put("distanceMetric", "kilometers"); // TODO: Change to integer, getMetric());
        root.put("location", "luke_tests"); //getLocation());
        root.put("time", new SimpleDateFormat(ServerManager.DATE_FORMAT).format(getDate()));
        root.put("estimatedVisualRange", 7.0); //getEstimatedVisualRange());
        root.put("image", Util.bitmapToString(getImageBitmap()));

        int algorithm = getAlgorithm();
        root.put("algorithmType", algorithm);
        switch (algorithm) {

            // Two-in-one
            case 1:
                root.put("nearTargetX", 1.0); //getImageBitmap(). targetCoordinates[0][0]);
                root.put("nearTargetY", 2.0); //targetCoordinates[0][1]);
                root.put("nearTargetEstimatedDistance", 3.0);
                root.put("farTargetX", 4.0); //targetCoordinates[1][0]);
                root.put("farTargetY", 5.0); //targetCoordinates[1][1]);
                root.put("farTargetEstimatedDistance", 6.0); //Util.joinArray(getDistances(), ","));
                root.put("gpsLongitude", 8.0); //Util.joinArray(getGPS(), ","));
                root.put("gpsLatitude", 9.0); //Util.joinArray(getGPS(), ","));
                break;

            // One-for-one
            case 2:
                root.put("imageTwo", Util.bitmapToString(getImageBitmap()));
                root.put("nearTargetX", 1.0); //getImageBitmap(). targetCoordinates[0][0]);
                root.put("nearTargetY", 2.0); //targetCoordinates[0][1]);
                root.put("nearTargetEstimatedDistance", 3.0);
                root.put("farTargetX", 4.0); //targetCoordinates[1][0]);
                root.put("farTargetY", 5.0); //targetCoordinates[1][1]);
                root.put("farTargetEstimatedDistance", 6.0); //Util.joinArray(getDistances(), ","));
                root.put("nearGpsLongitude", 8.0); //Util.joinArray(getGPS(), ","));
                root.put("nearGpsLatitude", 9.0); //Util.joinArray(getGPS(), ","));
                root.put("farGpsLongitude", 8.0); //Util.joinArray(getGPS(), ","));
                root.put("farGpsLatitude", 9.0); //Util.joinArray(getGPS(), ","));
                break;

            default:
                break;
        }

        return root;
    }

    @Override
    public Object getRaw() {
        return mPost;
    }
}
