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
import edu.wsu.lar.airpact_fire.data.realm.model.Coordinate;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.data.realm.model.Target;
import edu.wsu.lar.airpact_fire.data.realm.model.VisualRange;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * @see PostObject
 */
public class RealmPostObject implements PostObject {

    private Realm mRealm;
    private Post mPost;
    private int mPostId;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmPostObject(Realm realm, int postId, DataManager dataManager,
                           DebugManager debugManager) {
        mRealm = realm;
        mPost = mRealm.where(Post.class).equalTo("postId", mPostId).findFirst();
        mPostId = postId;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    public RealmPostObject(Realm realm, Post post, DataManager dataManager,
                           DebugManager debugManager) {
        this(realm, post.postId, dataManager, debugManager);
    }

    @Override
    public Date getDate() {
        return mPost.date;
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
    public Bitmap getImage() {
        // TODO
        String fileLocation = mPost.imageLocation;
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
        mPost.imageLocation = fileLocation;
        mRealm.commitTransaction();
    }

    @Override
    public void setGPS(double[] values) {
        mRealm.beginTransaction();
        Coordinate coordinate = mRealm.createObject(Coordinate.class);
        coordinate.x = values[0];
        coordinate.y = values[1];
        mPost.geoCoordinate = coordinate;
        mRealm.commitTransaction();
    }

    @Override
    public double[] getGPS() {
        Coordinate coordinate = mPost.geoCoordinate;
        return new double[] {coordinate.x, coordinate.y};
    }

    @Override
    public float[] getVisualRanges() {
        RealmList<VisualRange> realmList = mPost.visualRanges;
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
        RealmList<VisualRange> realmList = mPost.visualRanges;
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
        RealmList<Target> targets = mPost.targets;
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
