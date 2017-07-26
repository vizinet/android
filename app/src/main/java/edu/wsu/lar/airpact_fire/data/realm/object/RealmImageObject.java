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
import java.util.Date;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Coordinate;
import edu.wsu.lar.airpact_fire.data.realm.model.Image;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import io.realm.Realm;
import io.realm.RealmList;

/** @see ImageObject */
public class RealmImageObject implements ImageObject {

    private Realm mRealm;
    private Image mImage;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmImageObject(Realm realm, int postId, int imageId, DataManager dataManager,
                            DebugManager debugManager) {
        this(
                realm,
                realm.where(Image.class)
                        .equalTo("postId", postId)
                        .equalTo("imageId", imageId)
                        .findFirst(),
                dataManager,
                debugManager);
    }

    public RealmImageObject(Realm realm, Image image, DataManager dataManager,
                            DebugManager debugManager) {
        mRealm = realm;
        mImage = image;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public Bitmap getImageBitmap() {
        String fileLocation = mPost.images.get(0).imageLocation;
        Bitmap bitmap = null;
        try {
            // TODO: Store image location s.t. we don't need to get it's substring
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
        // TODO: Possibly store in private directory
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName);
        Uri imageUri = Uri.fromFile(image);

        // Save image location
        mRealm.beginTransaction();
        Image imageModel = mRealm.createObject(Image.class, imageUri.toString());
        RealmList imageList = new RealmList();
        imageList.add(imageModel);
        mPost.images = imageList;
        mRealm.commitTransaction();

        return imageUri;
    }

    // TODO: Possibly remove, possibly use to store image file to private dir
    @Override
    public void setImage(Bitmap value) {
        String fileLocation = mPost.images.get(0).imageLocation;
        File file = new File(fileLocation.substring(7));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        value.compress(Bitmap.CompressFormat.PNG, 0, bos); // NOTE: Most time-consuming task
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
    public void setGPS(double[] values) {
        mRealm.beginTransaction();
        Coordinate coordinate = mRealm.createObject(Coordinate.class);
        coordinate.x = values[0];
        coordinate.y = values[1];
        mPost.images.get(0).gpsCoordinate = coordinate;
        mRealm.commitTransaction();
    }

    @Override
    public float[] getDistances() {
        return new float[0];
    }

    @Override
    public void setDistances(float[] values) {

    }

    @Override
    public double[] getGPS() {
        Coordinate coordinate = mPost.images.get(0).gpsCoordinate;
        return new double[] {coordinate.x, coordinate.y};
    }

    @Override
    public float[][] getTargetsCoordinates() {

        return null;

        /*
        RealmList<VisualRange> realmList = mRealm.where(Post.class).equalTo("postId", mPostId)
                .findFirst().visualRanges;
        float[] values = new float[realmList.size()];
        int i = 0;
        for (VisualRange v : realmList) {
            values[i++] = v.value;
        }
        return values;
        */
    }

    @Override
    public void setTargetsCoordinates(float[][] values) {
        // TODO

    }

    @Override
    public Object getRaw() {
        return mPost;
    }
}
