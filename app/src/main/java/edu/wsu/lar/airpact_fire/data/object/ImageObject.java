// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

import android.graphics.Bitmap;
import android.net.Uri;

public interface ImageObject {

    double[] getGPS();
    void setGPS(double[] values);

    TargetObject createTargetObject();

    Bitmap getImageBitmap();
    Uri createImage();
    void setImage(Bitmap value);

    void delete();

    Object getRaw();
}
