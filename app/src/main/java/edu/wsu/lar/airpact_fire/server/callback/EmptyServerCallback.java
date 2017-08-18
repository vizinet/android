// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.server.callback;

public class EmptyServerCallback implements ServerCallback {

    public EmptyServerCallback() { }

    @Override
    public Object onStart() {
        return null;
    }

    @Override
    public Object onFinish(Object... args) {
        return null;
    }
}
