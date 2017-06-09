// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.data.realm.model.VisualRange;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * @see PostObject
 */
public class RealmPostObject implements PostObject {

    private Realm mRealm;
    private String mPostId;
    private DebugManager mDebugManager;
    private DataManager mDataManager;

    public RealmPostObject(Realm realm, String postId, DataManager mDataManager, DebugManager debugManager) {
        mRealm = realm;
        mPostId = postId;
        mDebugManager = debugManager;
        mDataManager = mDataManager;
    }

    public RealmPostObject(Realm realm, Post post, DataManager mDataManager, DebugManager debugManager) {
        mRealm = realm;
        mPostId = post.postId;
        mDebugManager = debugManager;
        mDataManager = mDataManager;
    }

    @Override
    public Date getDate() {
        return mRealm.where(Post.class).equalTo("postId", mPostId).findFirst().date;
    }

    @Override
    public int getMode() {
        return mRealm.where(Post.class).equalTo("postId", mPostId).findFirst().mode;
    }

    @Override
    public void setMode(int value) {
        mRealm.beginTransaction();
        mRealm.where(Post.class).equalTo("postId", mPostId).findFirst().mode = value;
        mRealm.commitTransaction();
    }

    @Override
    public Bitmap getImage() {
        // TODO
        String fileLocation = mRealm.where(Post.class).equalTo("postId", mPostId)
                .findFirst().imageLocation;
        return null;
    }

    @Override
    public void setImage(Bitmap value) {

        ContextWrapper cw = new ContextWrapper(mDataManager.getActivity().getApplicationContext());

        // Path to "/data/data/airpact_fire/app_data/imageDir"
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, "post_image_" + mPostId + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            value.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileLocation = file.getAbsolutePath();

        // Store image location in DB
        mRealm.beginTransaction();
        mRealm.where(Post.class).equalTo("postId", mPostId).findFirst().imageLocation = fileLocation;
        mRealm.commitTransaction();
    }

    @Override
    public float[] getGPS() {
        return new float[0];
    }

    @Override
    public void setGPS(float[] values) {

    }

    @Override
    public float[] getVisualRanges() {
        RealmList<VisualRange> realmList = mRealm.where(Post.class).equalTo("postId", mPostId)
                .findFirst().visualRanges;
        float[] values = new float[realmList.size()];
        int i = 0;
        for (VisualRange v : realmList) {
            values[i++] = v.value;
        }
        return values;
    }

    @Override
    public void setVisualRanges(float[] values) {
        mRealm.beginTransaction();
        RealmList<VisualRange> realmList = mRealm.where(Post.class).equalTo("postId", mPostId)
                .findFirst().visualRanges;
        int i = 0;
        for (VisualRange v : realmList) {
            v.value = values[i++];
        }
        mRealm.commitTransaction();
    }

    @Override
    public boolean getIsSubmitted() {
        return false;
    }

    @Override
    public void setIsSubmitted(boolean value) {

    }

    @Override
    public float[][] getTargetsCoordinates() {

        return null;

        /*
        RealmList<VisualRange> realmList = mRealm.where(Post.class).equalTo("postId", mPostId)
                .findFirst().visualRanges;
        float[] values = new float[realmList.size()];
        int i = 0;
        for (VisualRange v : realmList) {
            values[i++] = v.value;
        }
        return values;
        */
    }

    @Override
    public void setTargetsCoorindates(float[][] values) {
        // TODO
    }

    @Override
    public int[] getTargetsColors() {
        mRealm.where(Post.class).equalTo("postId", mPostId).findAll();
        return null;
    }

    @Override
    public void setTargetsColors(int[] values) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String value) {

    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public void setTag(String value) {

    }
}
