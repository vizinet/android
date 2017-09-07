// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.interface_object;

import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.TargetInterfaceObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Coordinate;
import edu.wsu.lar.airpact_fire.data.realm.model.Target;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Realm implementation of the {@link TargetInterfaceObject}.
 */
public class RealmTargetInterfaceObject implements TargetInterfaceObject {

    private Realm mRealm;
    private Target mTarget;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmTargetInterfaceObject(Realm realm, Target target, DataManager dataManager,
                                      DebugManager debugManager) {
        mRealm = realm;
        mTarget = target;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public float getDistance() {
        return mTarget.distance;
    }

    @Override
    public void setDistance(float value) {
        mRealm.beginTransaction();
        mTarget.distance = value;
        mRealm.commitTransaction();
    }

    @Override
    public float[] getCoordinates() {
        Coordinate coordinate = mTarget.coordinate;
        return new float[] { (float) coordinate.x, (float) coordinate.y };
    }

    @Override
    public void setCoordinates(float[] values) {
        mRealm.beginTransaction();
        mTarget.coordinate = mRealm.createObject(Coordinate.class);
        mTarget.coordinate.x = values[0];
        mTarget.coordinate.y = values[1];
        mRealm.commitTransaction();
    }

    @Override
    public void delete() {

        if (!mRealm.isInTransaction()) mRealm.beginTransaction();

        // Delete target
        RealmResults<Target> targetResults = mRealm.where(Target.class)
                .equalTo("targetId", mTarget.targetId).findAll();
        targetResults.deleteAllFromRealm();

        if (!mRealm.isInTransaction()) mRealm.commitTransaction();
    }

    @Override
    public Object getRaw() {
        return mTarget;
    }
}
