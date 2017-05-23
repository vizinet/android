package edu.wsu.lar.airpact_fire.server.manager;

import android.content.Context;

public class DummyServerManager implements ServerManager {

    private static boolean sBlindAuthentication = true;

    @Override
    public void onAppStart(Object... args) {

    }

    @Override
    public void onAppEnd(Object... args) {

    }

    @Override
    public void onActivityStart(Object... args) {

    }

    @Override
    public void onActivityEnd(Object... args) {

    }

    @Override
    public void onLogin(Object... args) {

    }

    @Override
    public void onLogout(Object... args) {

    }

    @Override
    public void authenticate(Context context, String username, String password, ServerCallback callback) {
        callback.onFinish(sBlindAuthentication, username, password);
    }
}
