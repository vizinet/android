package edu.wsu.lar.airpact_fire.manager;

import android.content.Context;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.manager.RealmDataManager;

public final class AppManager {
    public static final boolean IS_DEBUGGING = false;
    public static DataManager getDataManager(Object... args) {
        return new RealmDataManager((Context) args[0]);
    }
}
