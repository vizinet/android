// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm.tio;

import org.json.simple.JSONObject;
import java.text.SimpleDateFormat;
import java.util.List;
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.TargetObject;
import edu.wsu.lar.airpact_fire.server.contract.ServerContract;
import edu.wsu.lar.airpact_fire.util.Util;

public class TwoInOneServerContract implements ServerContract {

    @Override
    public JSONObject toJSON(PostObject postObject) {

        List<ImageObject> imageObjectList = postObject.getImageObjects();
        ImageObject imageObjectOne = imageObjectList.get(0);
        List<TargetObject> imageObjectOneTargetObjects = imageObjectOne.getTargetObjects();

        JSONObject root = new JSONObject();
        root.put("user", postObject.getUser().getUsername());
        root.put("description", postObject.getDescription());
        root.put("secretKey", postObject.getSecretKey());
        root.put("distanceMetric", "kilometers"); // TODO: Change to integer on server, getMetric());
        root.put("location", postObject.getLocation());
        root.put("time", new SimpleDateFormat(ServerContract.DATE_FORMAT)
                .format(postObject.getDate()));
        root.put("estimatedVisualRange", postObject.getEstimatedVisualRange());
        root.put("image", Util.bitmapToString(imageObjectOne.getImageBitmap()));
        root.put("algorithmType", postObject.getAlgorithm());
        root.put("nearTargetX", imageObjectOneTargetObjects.get(0).getCoordinates()[0]);
        root.put("nearTargetY", imageObjectOneTargetObjects.get(0).getCoordinates()[1]);
        root.put("nearTargetEstimatedDistance",
                imageObjectOneTargetObjects.get(0).getDistance());
        root.put("farTargetX", imageObjectOneTargetObjects.get(1).getCoordinates()[0]);
        root.put("farTargetY", imageObjectOneTargetObjects.get(1).getCoordinates()[1]);
        root.put("farTargetEstimatedDistance",
                imageObjectOneTargetObjects.get(1).getDistance());
        root.put("gpsLongitude", imageObjectOne.getGps()[0]);
        root.put("gpsLatitude", imageObjectOne.getGps()[1]);

        return root;
    }
}
