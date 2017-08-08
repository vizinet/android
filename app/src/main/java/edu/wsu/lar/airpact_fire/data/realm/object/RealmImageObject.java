// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.object.TargetObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Coordinate;
import edu.wsu.lar.airpact_fire.data.realm.model.Image;
import edu.wsu.lar.airpact_fire.data.realm.model.Target;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.ui.target.UiTarget;
import io.realm.Realm;
import io.realm.RealmResults;

/** @see ImageObject */
public class RealmImageObject implements ImageObject {

    private Realm mRealm;
    private Image mImage;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmImageObject(Realm realm, Image image, DataManager dataManager,
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
            bitmap = BitmapFactory.decodeFile(fileLocation.substring(7));
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public Uri createImage() {

        // Create an image file in public "Pictures/" directory to be populated by
        // picture capturing activity
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName);
        Uri imageUri = Uri.fromFile(image);

        // Save image location
        mRealm.beginTransaction();
        mImage.imageLocation = imageUri.toString();
        mRealm.commitTransaction();

        return imageUri;
    }

    // TODO: Possibly remove, possibly use to store image file to private dir
    @Override
    public void setImage(Bitmap value) {
        String fileLocation = mImage.imageLocation;
        File file = new File(fileLocation.substring(7));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // JPEG compression; time-consuming
        value.compress(Bitmap.CompressFormat.JPEG, 50, bos);

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
    public TargetObject createTargetObject() {
        mRealm.beginTransaction();
        Target targetModel = mRealm.createObject(Target.class, mDataManager.generateTargetId());
        targetModel.postId = mImage.postId;
        targetModel.imageId = mImage.imageId;
        mRealm.commitTransaction();
        return new RealmTargetObject(mRealm, targetModel, mDataManager, mDebugManager);
    }

    @Override
    public List<TargetObject> getTargetObjects() {
        RealmResults<Target> targetResults = mRealm.where(Target.class)
                .equalTo("imageId", mImage.imageId).findAllSorted("targetId");
        List targetList = new ArrayList<TargetObject>();
        for (Target target : targetResults) {
            targetList.add(new RealmTargetObject(mRealm, target, mDataManager, mDebugManager));
        }
        return targetList;
    }

    @Override
    public void delete() {

        mRealm.beginTransaction();

        // Delete image
        RealmResults<Image> imageResults = mRealm.where(Image.class)
                .equalTo("imageId", mImage.imageId).findAll();
        imageResults.deleteAllFromRealm();

        // Delete associated targets
        RealmResults<Target> targetResults = mRealm.where(Target.class)
                .equalTo("imageId", mImage.imageId).findAll();
        for (Target target : targetResults) {
            (new RealmTargetObject(mRealm, target, mDataManager, mDebugManager)).delete();
        }

        mRealm.commitTransaction();
    }

    @Override
    public Object getRaw() {
        return mImage;
    }
}
