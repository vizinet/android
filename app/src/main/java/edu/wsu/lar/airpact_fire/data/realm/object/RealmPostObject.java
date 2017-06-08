// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import java.util.Date;
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

    public RealmPostObject(Realm realm, String postId, DebugManager debugManager) {
        mRealm = realm;
        mPostId = postId;
        mDebugManager = debugManager;
    }

    public RealmPostObject(Realm realm, Post post, DebugManager debugManager) {
        mRealm = realm;
        mPostId = post.postId;
        mDebugManager = debugManager;
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
    public String getImage() {
        return null;
    }

    @Override
    public void setImage(String value) {

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
