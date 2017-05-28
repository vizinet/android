package edu.wsu.lar.airpact_fire.manager;

import android.content.Context;
import android.content.SharedPreferences;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.manager.RealmDataManager;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.server.manager.HTTPServerManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import static android.content.Context.MODE_PRIVATE;

public class AIRPACTFireAppManager implements AppManager {

    // NOTE: Flag set by programmer
    private static final boolean sIsDebugging = true;

    private DataManager mDataManager;
    private ServerManager mServerManager;
    private DebugManager mDebugManager;

    @Override
    public boolean isDebugging() {
        return sIsDebugging;
    }

    @Override
    public DataManager getDataManager(Object... args) {
        return mDataManager;
    }

    @Override
    public ServerManager getServerManager(Object... args) {
        return mServerManager;
    }

    @Override
    public DebugManager getDebugManager(Object...args) {
        return mDebugManager;
    }

    @Override
    public void onAppStart(Object... args) {

        // Do app's first-run stuff
        Context context = (Context) args[0];
        SharedPreferences mPreferences = context.getSharedPreferences(context.getPackageName(),
                MODE_PRIVATE);
        if (mPreferences.getBoolean("firstrun", true)) {
            mDebugManager.printLog("App's first run");
            mDataManager.onAppFirstRun();
            mPreferences.edit().putBoolean("firstrun", false).commit();
        } else {
            mDebugManager.printLog("Not app's first run");
        }

        mDataManager.onAppStart();
        mServerManager.onAppStart();
    }

    @Override
    public void onAppEnd(Object... args) {
        mDataManager.onAppEnd();
        mServerManager.onAppEnd();
    }

    @Override
    public void onActivityStart(Object... args) {

        final Context context = (Context) args[0];

        // Construct managers
        mDebugManager = new DebugManager(isDebugging());
        mDataManager = new RealmDataManager(mDebugManager);
        mServerManager = new HTTPServerManager();

        mDebugManager.printLog("Started activity = " + context.toString());

        // Notify that activity has begun
        mDataManager.onActivityStart(context);
        mServerManager.onActivityStart();

        // Catch global exceptions
        // NOTE: Has never been thrown, to my observation
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                mDebugManager.printToast(context, "[Global exception caught]");
            }
        });
    }

    @Override
    public void onActivityEnd(Object... args) {
        mDataManager.onActivityEnd();
        mServerManager.onActivityEnd();
    }

    @Override
    public void onLogin(Object... args) {
        mDataManager.onLogin(args);
        mServerManager.onLogin(args);
    }

    @Override
    public void onLogout(Object... args) {
        mDataManager.onLogout();
        mServerManager.onLogout();
    }
}
