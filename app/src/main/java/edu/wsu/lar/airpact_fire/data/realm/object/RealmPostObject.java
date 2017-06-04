// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import java.util.Date;

import edu.wsu.lar.airpact_fire.data.object.AppObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.SessionObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.App;
import edu.wsu.lar.airpact_fire.data.realm.model.Session;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.util.Util;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @see PostObject
 */
public class RealmPostObject implements PostObject {

    private Realm mRealm;
    private String mUsername;
    private DebugManager mDebugManager;

    public RealmPostObject(Realm realm, String username, DebugManager debugManager) {
        mRealm = realm;
        mUsername = username;
        mDebugManager = debugManager;
    }

    @Override
    public Date getDate() {
        return null;
    }

    @Override
    public int getMode() {
        return mRealm.where(Session.class).equalTo("user.username", mUsername).findFirst().mode;
    }

    @Override
    public void setMode(int value) {
        mRealm.beginTransaction();
        mRealm.where(Session.class).equalTo("user.username", mUsername).findFirst().mode = value;
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
    public float getVisualRange() {
        return 0;
    }

    @Override
    public void setVisualRange(float value) {

    }

    @Override
    public boolean getIsSubmitted() {
        return false;
    }

    @Override
    public void setIsSubmitted(boolean value) {

    }

    @Override
    public float[][] getTargets() {
        return new float[0][];
    }

    @Override
    public void setTargets(float[][] values) {

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

    @Override
    public double[] getGPSCoordinates() {
        return new double[0];
    }

    @Override
    public void setGPSCoordinates(double[] values) {

    }
}
