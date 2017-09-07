// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.algorithm.ofo;

import org.json.simple.JSONObject;
import java.text.SimpleDateFormat;
import java.util.List;

import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.PostInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.TargetInterfaceObject;
import edu.wsu.lar.airpact_fire.server.contract.ServerContract;
import edu.wsu.lar.airpact_fire.util.Util;

/**
 * Contract with server and a PostInterfaceObject on how to communicate.
 *
 * <p>This is for posts which fulfill the {@link OneForOneAlgorithm}.</p>
 */
public class OneForOneServerContract implements ServerContract {

    @Override
    public JSONObject toJSON(PostInterfaceObject postInterfaceObject) {

        // TODO: Some renaming with the whole "near" and "far" thing, because they're reversed

        List<ImageInterfaceObject> imageInterfaceObjectList = postInterfaceObject.getImageObjects();
        ImageInterfaceObject imageInterfaceObjectOne = imageInterfaceObjectList.get(0);
        ImageInterfaceObject imageInterfaceObjectTwo = imageInterfaceObjectList.get(1);
        List<TargetInterfaceObject> imageObjectOneTargetInterfaceObjects = imageInterfaceObjectOne.getTargetObjects();
        List<TargetInterfaceObject> imageObjectTwoTargetInterfaceObjects = imageInterfaceObjectTwo.getTargetObjects();

        JSONObject root = new JSONObject();
        root.put("user", postInterfaceObject.getUser().getUsername());
        root.put("description", postInterfaceObject.getDescription());
        root.put("secretKey", postInterfaceObject.getSecretKey());
        root.put("distanceMetric", "kilometers"); // TODO: Change to integer, getMetric());
        root.put("location", postInterfaceObject.getLocation());
        root.put("time", new SimpleDateFormat(ServerContract.DATE_FORMAT)
                .format(postInterfaceObject.getDate()));
        root.put("estimatedVisualRange", postInterfaceObject.getEstimatedVisualRange());
        root.put("algorithmType", postInterfaceObject.getAlgorithm());
        root.put("image", Util.bitmapToString(imageInterfaceObjectOne.getBitmap()));
        root.put("imageTwo", Util.bitmapToString(imageInterfaceObjectTwo.getBitmap()));
        root.put("nearTargetX", imageObjectOneTargetInterfaceObjects.get(0).getCoordinates()[0]);
        root.put("nearTargetY", imageObjectOneTargetInterfaceObjects.get(0).getCoordinates()[1]);
        root.put("nearTargetEstimatedDistance",
                imageObjectOneTargetInterfaceObjects.get(0).getDistance());
        root.put("gpsLongitude", imageInterfaceObjectOne.getGps()[0]);
        root.put("gpsLatitude", imageInterfaceObjectOne.getGps()[1]);
        //root.put("nearGpsLongitude", imageInterfaceObjectOne.getGps()[0]);
        //root.put("nearGpsLatitude", imageInterfaceObjectOne.getGps()[1]);
        root.put("farTargetX", imageObjectTwoTargetInterfaceObjects.get(0).getCoordinates()[0]);
        root.put("farTargetY", imageObjectTwoTargetInterfaceObjects.get(0).getCoordinates()[1]);
        root.put("farTargetEstimatedDistance",
                imageObjectTwoTargetInterfaceObjects.get(0).getDistance());
        //root.put("farGpsLongitude", imageInterfaceObjectTwo.getGps()[0]);
        //root.put("farGpsLatitude", imageInterfaceObjectTwo.getGps()[1]);

        return root;
    }
}
