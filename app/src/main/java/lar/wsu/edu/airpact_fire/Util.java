package lar.wsu.edu.airpact_fire;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

// Class for basic utilities used throughout app
public class Util {
    // TODO posting to server
    // TODO authenticate user with server
    // TODO store post in SQL
    // TODO read post from SQL

    // Check if internet is available
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // Check if (x, y) point is within view
    public static boolean isPointInView(View view, int x, int y) {
        return (x > view.getX())
                && (x <  (view.getX() + view.getWidth()))
                && (y > view.getY())
                && (y < (view.getY() + view.getHeight()));
    }

    // TODO Get analysis of circular area around x and y point
    public static int getPixelAtPos(ImageView imageView, int x, int y) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
        int pixel = bitmap.getPixel(x, y);

        return pixel;
    }

    // Create an image file with collision resistant title to public directory
    public static File createImageFile() throws IOException {
        // Create an image file name -- timestamped
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Create image file
        File image = new File(storageDir, imageFileName + ".jpg");

        // Get image location
//        Post.ImageLocation = image.getAbsolutePath();

        return image;
    }

    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    // Converts image to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    public static Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
