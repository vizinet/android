// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.manager;

import android.app.Activity;
import edu.wsu.lar.airpact_fire.data.algorithm.Algorithm;
import edu.wsu.lar.airpact_fire.data.algorithm.ofo.OneForOneAlgorithm;
import edu.wsu.lar.airpact_fire.data.algorithm.tio.TwoInOneAlgorithm;
import edu.wsu.lar.airpact_fire.data.interface_object.AppInterfaceObject;

/**
 * This abstract class consists of variables and methods for handling this app's
 * persistently stored information under various database management platforms,
 * e.g. Realm and MySQL.
 *
 * <p>This data manager <b>must</b> always run on the UI thread and will be constructed/
 * deconstructed with the corresponding life-cycle of each succeeding activity.
 * Any implementer of this class must be initialized for its methods to be called.
 * </p>
 *
 * <p>Any DataManager deals with the lending of database objects (e.g. AppInterfaceObject)
 * to the UI, as well as conforming to standard (and custom) activity lifecycle
 * methods, such as onLogin, onAppFirstStart, and onActivityEnd.</p>
 *
 * @see edu.wsu.lar.airpact_fire.app.manager.AppManager
 */
public abstract class DataManager {

    /**
     * Enum of post modes internal to app, designating how we treat particular posts.
     *
     * <p>We refer to these metrics on a 1-based index.</p>
     */
    public enum PostMode {

        DRAFTED (1, "Drafted"),
        QUEUED (2, "Queued"),
        SUBMITTED (3, "Submitted");

        private final int mId;
        private final String mName;

        PostMode(int id, String name) {
            mId = id;
            mName = name;
        }
        public int getId() {
            return mId;
        }
        public String getName() {
            return mName;
        }
    }
    public static PostMode getPostMode(int postModeId) {
        return PostMode.values()[postModeId - 1];
    }

    /**
     * Array of algorithms agreed upon by server and app.
     *
     * <p>We refer to these algorithms on a 1-based index.</p>
     *
     * <p>Must be updated once a new {@link Algorithm} is added to the
     * {@link edu.wsu.lar.airpact_fire.data.algorithm} package.</p>
     *
     * <p>Note: was attempting to use the Reflections library, but
     * it kept crashing and I didn't have time to debug.</p>
     */
    public static final Class<Algorithm>[] ALGORITHMS = new Class[]{
            TwoInOneAlgorithm.class,    // 1
            OneForOneAlgorithm.class    // 2
    };

    public enum PostAlgorithm {

        TIO (1, TwoInOneAlgorithm.class),
        OFO (2, OneForOneAlgorithm.class);

        private int mId;
        private Class mAlgorithm;

        PostAlgorithm(int id, Class algorithm) {
           mId = id;
           mAlgorithm = algorithm;
        }
        public int getId() { return mId; }
        public Algorithm getInstance() {
            try {
                return (Algorithm) mAlgorithm.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public static PostAlgorithm getAlgorithm(int algorithmId) {
        return PostAlgorithm.values()[algorithmId - 1];
    }

    /** @return app object, key to all of database access for UI. */
    public abstract AppInterfaceObject getApp();
    public abstract Activity getActivity();
    public abstract int generateSessionId();
    public abstract int generatePostId();
    public abstract int generateImageId();
    public abstract int generateTargetId();

    /* Activity lifecycle methods */

    public abstract void onAppFirstRun(Object... args);
    public abstract void onAppStart(Object... args);
    public abstract void onAppEnd(Object... args);
    public abstract void onLogin(Object... args);
    public abstract void onLogout(Object... args);
    public abstract void onActivityStart(Object... args);
    public abstract void onActivityEnd(Object... args);
}
