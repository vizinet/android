// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

import android.graphics.Bitmap;
import android.net.Uri;
import java.util.List;

public interface ImageObject {

    double[] getGps();
    void setGps(double[] values);

    TargetObject createTargetObject();
    List<TargetObject> getTargetObjects();

    Bitmap getBitmap();
    Uri createImage();
    void setImage(Bitmap value);

    void delete();

    Object getRaw();
}
