// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.object;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import org.json.simple.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.data.object.ImageObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.data.realm.model.Coordinate;
import edu.wsu.lar.airpact_fire.data.realm.model.Image;
import edu.wsu.lar.airpact_fire.data.realm.model.Post;
import edu.wsu.lar.airpact_fire.data.realm.model.User;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;
import edu.wsu.lar.airpact_fire.util.Util;
import io.realm.Realm;
import io.realm.RealmList;

/** @see PostObject */
public class RealmPostObject implements PostObject {

    private Realm mRealm;
    private Post mPost;
    private DataManager mDataManager;
    private DebugManager mDebugManager;

    public RealmPostObject(Realm realm, int postId, DataManager dataManager,
                           DebugManager debugManager) {
        this(realm, realm.where(Post.class).equalTo("postId", postId).findFirst(),
                dataManager, debugManager);
    }

    public RealmPostObject(Realm realm, Post post, DataManager dataManager,
                           DebugManager debugManager) {
        mRealm = realm;
        mPost = post;
        mDataManager = dataManager;
        mDebugManager = debugManager;
    }

    @Override
    public UserObject getUser() {
        return new RealmUserObject(mRealm, mPost.user, mDataManager, mDebugManager);
    }

    @Override
    public String getSecretKey() {
        return mPost.secretKey;
    }

    @Override
    public void setSecretKey(String value) {

    }

    @Override
    public Date getDate() {
        return mPost.date;
    }

    @Override
    public void setDate(Date value) {
        mRealm.beginTransaction();
        mPost.date = value;
        mRealm.commitTransaction();
    }

    @Override
    public long getImageServerId() {
        return 0;
    }

    @Override
    public void setImageServerId(long value) {

    }

    @Override
    public int getMetric() {
        return 0;
    }

    @Override
    public void setMetric(int value) {

    }

    @Override
    public int getAlgorithm() {
        return 1;
    }

    @Override
    public void setAlgorithm(int value) {

    }

    @Override
    public int getMode() {
        return mPost.mode;
    }

    @Override
    public void setMode(int value) {
        mRealm.beginTransaction();
        mPost.mode = value;
        mRealm.commitTransaction();
    }

    @Override
    public Bitmap getImageBitmap() {
        String fileLocation = mPost.images.get(0).imageLocation;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(fileLocation.substring(7));
            if (true) return bitmap;
            bitmap = MediaStore.Images.Media.getBitmap(mDataManager.getActivity()
                    .getContentResolver(), Uri.parse(fileLocation));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /*
    @Override
    public ImageObject getImage() {
       //return new ImageObject(mPost.images.get(0));
        return null;
    }
    */

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
    private void setImage(Bitmap value) {

        ContextWrapper cw = new ContextWrapper(mDataManager.getActivity().getApplicationContext());

        // Path to "/data/data/airpact_fire/app_data/imageDir"
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, "post_image_" + mPost.postId + ".jpg");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            value.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileLocation = file.getAbsolutePath();

        // Store image location in DB
        mRealm.beginTransaction();
        //mPost.imageLocation = fileLocation;
        mRealm.commitTransaction();
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
    public float getEstimatedVisualRange() {
        return 0;
    }

    @Override
    public void setEstimatedVisualRange(float value) {

    }

    @Override
    public float getComputedVisualRange() {
        return 0;
    }

    @Override
    public void setComputedVisualRange(float value) {

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
    public void setTargetsCoorindates(float[][] values) {
        // TODO
    }

    @Override
    public String getDescription() {
        return "test description";
    }

    @Override
    public void setDescription(String value) {

    }

    @Override
    public String getLocation() {
        return "test location";
    }

    @Override
    public void setLocation(String value) {

    }

    // TODO: Something cooler so that this Data object doesn't need to know any server specs!

    @Override
    public JSONObject toJSON() {

        // Intermediate objects
        float[][] targetCoordinates = getTargetsCoordinates();

        // TODO: Adapt post for many images and visual ranges and fields
        // TODO: Dynamically change JSON for algorithm type
        // TODO: Maybe send radii for the targets

        // Post submission field vars => JSON
        JSONObject root = new JSONObject();
        root.put("user", getUser().getUsername());
        root.put("description", getDescription());
        Bitmap bitmap = getImageBitmap();
        root.put("image", Util.bitmapToString(getImageBitmap()));
        root.put("secretKey", getSecretKey());
        root.put("distanceMetric", "kilometers"); // TODO: Change to integer, getMetric());
        root.put("location", getLocation());
        root.put("time", new SimpleDateFormat(ServerManager.DATE_FORMAT).format(getDate()));

        // TODO: Send int representing algorithm; adapt my docs to
        // algorithm #1 = two-in-one
        // algorithm #2 = one-for-one
        //int algorithm = getAlgorithm();
        int algorithm = 1;
        // TODO: Set the algorithm in the UI
        root.put("algorithmType", algorithm);
        switch (algorithm) {

            case 1: // Two-in-one
                root.put("nearTargetX", 1.0); //getImageBitmap(). targetCoordinates[0][0]);
                root.put("nearTargetY", 2.0); //targetCoordinates[0][1]);
                root.put("nearTargetEstimatedDistance", 3.0);
                root.put("farTargetX", 4.0); //targetCoordinates[1][0]);
                root.put("farTargetY", 5.0); //targetCoordinates[1][1]);
                root.put("farTargetEstimatedDistance", 6.0); //Util.joinArray(getDistances(), ","));
                root.put("estimatedVisualRange", 7.0); //getEstimatedVisualRange());
                root.put("gpsLongitude", 8.0); //Util.joinArray(getGPS(), ","));
                root.put("gpsLatitude", 9.0); //Util.joinArray(getGPS(), ","));
                break;

            case 2: // One-for-one
                // TODO
                break;

            default:
                break;
        }

        return root;
    }
}
