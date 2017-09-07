// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm.tio;

import org.json.simple.JSONObject;
import java.text.SimpleDateFormat;
import java.util.List;

import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.TargetInterfaceObject;
import edu.wsu.lar.airpact_fire.server.contract.ServerContract;
import edu.wsu.lar.airpact_fire.util.Util;

public class TwoInOneServerContract implements ServerContract {

    @Override
    public JSONObject toJSON(PostInterfaceObject postInterfaceObject) {

        List<ImageInterfaceObject> imageInterfaceObjectList = postInterfaceObject.getImageObjects();
        ImageInterfaceObject imageInterfaceObjectOne = imageInterfaceObjectList.get(0);
        List<TargetInterfaceObject> imageObjectOneTargetInterfaceObjects = imageInterfaceObjectOne.getTargetObjects();

        JSONObject root = new JSONObject();
        root.put("user", postInterfaceObject.getUser().getUsername());
        root.put("description", postInterfaceObject.getDescription());
        root.put("secretKey", postInterfaceObject.getSecretKey());
        root.put("distanceMetric", "kilometers"); // TODO: Change to integer on server, getMetric());
        root.put("location", postInterfaceObject.getLocation());
        root.put("time", new SimpleDateFormat(ServerContract.DATE_FORMAT)
                .format(postInterfaceObject.getDate()));
        root.put("estimatedVisualRange", postInterfaceObject.getEstimatedVisualRange());
        root.put("image", Util.bitmapToString(imageInterfaceObjectOne.getBitmap()));
        root.put("algorithmType", postInterfaceObject.getAlgorithm());
        root.put("nearTargetX", imageObjectOneTargetInterfaceObjects.get(0).getCoordinates()[0]);
        root.put("nearTargetY", imageObjectOneTargetInterfaceObjects.get(0).getCoordinates()[1]);
        root.put("nearTargetEstimatedDistance",
                imageObjectOneTargetInterfaceObjects.get(0).getDistance());
        root.put("farTargetX", imageObjectOneTargetInterfaceObjects.get(1).getCoordinates()[0]);
        root.put("farTargetY", imageObjectOneTargetInterfaceObjects.get(1).getCoordinates()[1]);
        root.put("farTargetEstimatedDistance",
                imageObjectOneTargetInterfaceObjects.get(1).getDistance());
        root.put("gpsLongitude", imageInterfaceObjectOne.getGps()[0]);
        root.put("gpsLatitude", imageInterfaceObjectOne.getGps()[1]);

        return root;
    }
}
