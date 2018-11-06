// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.interface_object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.interface_object.ImageInterfaceObject;
import edu.wsu.lar.airpact_fire.data.interface_object.TargetInterfaceObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Coordinate;
import edu.wsu.lar.airpact_fire.data.realm.model.Image;
import edu.wsu.lar.airpact_fire.data.realm.model.Target;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Realm implementation of the {@link ImageInterfaceObject}.
 */
public class RealmImageInterfaceObject implements ImageInterfaceObject {

    private Realm mRealm;
    private Image mImage;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmImageInterfaceObject(Realm realm, Image image, DataManager dataManager,
                                     DebugManager debugManager) {
        mRealm = realm;
        mImage = image;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public Bitmap getBitmap() {
        String fileLocation = mImage.imageLocation;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(fileLocation);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public File createImageFile() {
        return null;
    }

    @Override
    public File createImageFile(File storageDir) {

        // TODO: Possibly move file creation to a Util method

        // Create an image file in public "Pictures/" directory to be populated by picture capturing
        // activity
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        /*File storageDir = null; Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);*/

        // Attempt to create file
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            //image.getParentFile().mkdirs();
            //image.createNewFile();
        } catch (IOException e) {
            mDebugManager.printLog(String.format("Unable to create image file '%s'. Exception: %s",
                    imageFileName, e.toString()));
            return null;
        }

        // Save image location
        mRealm.beginTransaction();
        mImage.imageLocation = image.getAbsolutePath();
        mRealm.commitTransaction();

        return image;
    }

    // TODO: Possibly remove, possibly use to store image file to private dir
    @Override
    public void setImage(Bitmap value) {

        File file = new File(mImage.imageLocation);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // JPEG compression; time-consuming
        //value.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        // No compression
        value.compress(Bitmap.CompressFormat.PNG, 100, bos);

        byte[] bitmapData = bos.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setGps(double[] values) {
        mRealm.beginTransaction();
        Coordinate coordinate = mRealm.createObject(Coordinate.class);
        coordinate.x = values[0];
        coordinate.y = values[1];
        mImage.gpsCoordinate = coordinate;
        mRealm.commitTransaction();
    }

    @Override
    public double[] getGps() {
        Coordinate coordinate = mImage.gpsCoordinate;
        return new double[] { coordinate.x, coordinate.y };
    }

    @Override
    public TargetInterfaceObject createTargetObject() {
        mRealm.beginTransaction();
        Target targetModel = mRealm.createObject(Target.class, mDataManager.generateTargetId());
        targetModel.postId = mImage.postId;
        targetModel.imageId = mImage.imageId;
        mRealm.commitTransaction();
        return new RealmTargetInterfaceObject(mRealm, targetModel, mDataManager, mDebugManager);
    }

    @Override
    public List<TargetInterfaceObject> getTargetObjects() {
        RealmResults<Target> targetResults = mRealm.where(Target.class)
                .equalTo("imageId", mImage.imageId).findAllSorted("targetId");
        List targetList = new ArrayList<TargetInterfaceObject>();
        for (Target target : targetResults) {
            targetList.add(new RealmTargetInterfaceObject(mRealm, target, mDataManager, mDebugManager));
        }
        return targetList;
    }

    @Override
    public void delete() {

        if (!mRealm.isInTransaction()) mRealm.beginTransaction();

        // Delete associated targets
        RealmResults<Target> targetResults = mRealm.where(Target.class)
                .equalTo("imageId", mImage.imageId).findAll();
        for (Target target : targetResults) {
            (new RealmTargetInterfaceObject(mRealm, target, mDataManager, mDebugManager)).delete();
        }

        // Delete image
        RealmResults<Image> imageResults = mRealm.where(Image.class)
                .equalTo("imageId", mImage.imageId).findAll();
        imageResults.deleteAllFromRealm();

        if (!mRealm.isInTransaction()) mRealm.commitTransaction();
    }

    @Override
    public Object getRaw() {
        return mImage;
    }
}
