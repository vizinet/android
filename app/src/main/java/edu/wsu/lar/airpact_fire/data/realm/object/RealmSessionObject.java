// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import java.util.Date;

import edu.wsu.lar.airpact_fire.Reference;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.SessionObject;

/**
 * @see PostObject
 */
public class RealmSessionObject implements SessionObject {


    @Override
    public Date getStartDate() {
        return null;
    }

    @Override
    public void getEndDate() {

    }

    @Override
    public Date getLastLoginDate() {
        return null;
    }

    @Override
    public void setLastLoginDate(Date value) {

    }

    @Override
    public float getEstimatedDistance() {
        return 0;
    }

    @Override
    public void setEstimatedDistance(float value) {

    }

    @Override
    public Reference.ALGORITHM_ENUM getSelectedAlgorithm() {
        return null;
    }

    @Override
    public void setSelectedAlgorithm(Reference.ALGORITHM_ENUM value) {

    }
}
