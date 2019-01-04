// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Node;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.ui.activity.HomeActivity;
import edu.wsu.lar.airpact_fire.R;
import edu.wsu.lar.airpact_fire.ui.activity.SignInActivity;

// TODO: Create classes for each category of functions and put under "tools/"?

/**
 * Class for basic utilities used throughout app.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Util {

    public static class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

    // Image fields
    private static final String TRANSACTION_IMAGE_FILENAME = "transaction_image";
    private static final int IMAGE_COMPRESSION_QUALITY = 0;

    /**
     * Check if string is null or empty.
     * @param text string to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String text) {
        if (text == null || text.trim().length() > 0) {
            return false;
        }
        return true;
    }

    /**
     * Create unpopulated image file in public "Pictures/" directory.
     * @param storageDir directory to store pictures within
     * @param debugManager debug manager from calling activity
     * @return new image file
     */
    public static File createPublicImageFile(File storageDir, DebugManager debugManager) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Attempt to create file
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            debugManager.printLog(String.format("Unable to create image file '%s'. Exception: %s",
                    imageFileName, e.toString()));
            return null;
        }
        return image;
    }

    /**
     * Delete file from device's local memory.
     * @param fileUri
     */
    public static void deleteLocalFile(String fileUri) {
        File file = new File(fileUri);
        file.delete();
        boolean exists = file.exists();
        Log.d("DEBUG", String.format("Attempted to delete file. Success: {0}", !exists));
    }

    // Check if internet is available
    private static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isServerAvailable(Activity activity) {

        // TODO: Remove
        if (true) return true;

        // Check for network connection first
        if (!isNetworkAvailable(activity)) return false;
        // Now attempt to connect with server
        try {
            /*
            URL myUrl = new URL(Post.SERVER_BASE_URL);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            */
            return true;
        } catch (Exception e) {
            Toast.makeText(activity.getApplicationContext(),
                    "Cannot connect to server.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Bitmap createScaledBitmap(Bitmap bitmap, int width) {
        // Calculate dimensions
        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();
        int height = Math.round(oldHeight * (width / (float) oldWidth));

        // Create new one
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // Check if (x, y) point is within view
    public static boolean isPointInView(View view, int x, int y) {
        return (x > view.getX())
                && (x < (view.getX() + view.getWidth()))
                && (y > view.getY())
                && (y < (view.getY() + view.getHeight()));
    }

    // SOURCE: http://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // TODO Get analysis of circular area around x and y point
    public static int getPixelAtPos(ImageView imageView, int x, int y) {
        Drawable imageDrawable = imageView.getDrawable();
        Bitmap image = Util.drawableToBitmap(imageDrawable);
        int pixel = image.getPixel(x, y);

        return pixel;
    }

    // Create an image file with collision resistant title to public directory
    public static File createImageFile() throws IOException {

        // Create an image file name -- timestamped
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        // Create image file
        File image = new File(storageDir, imageFileName + ".jpg");

        // Get image location
//        Post.ImageLocation = image.getAbsolutePath();

        return image;
    }

    public static float[][] splitXY(float[][] values) {
        float[][] newValues = new float[2][values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[0][i] = values[i][0];
            newValues[1][i] = values[i][1];
        }
        return newValues;
    }

    public static Float[] toObjectList(float[] array) {
        Float[] newArray = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = new Float(array[i]);
        }
        return newArray;
    }

    public static Integer[] toObjectList(int[] array) {
        Integer[] newArray = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = new Integer(array[i]);
        }
        return newArray;
    }

    public static Double[] toObjectList(double[] array) {
        Double[] newArray = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = new Double(array[i]);
        }
        return newArray;
    }

    public static String joinArray(double[] array, String token) {
        ArrayList<Double> arrayList = new ArrayList<>(Arrays.asList(toObjectList(array)));
        return joinArray(arrayList, token);
    }

    public static String joinArray(float[] array, String token) {
        ArrayList<Float> arrayList = new ArrayList<>(Arrays.asList(toObjectList(array)));
        return joinArray(arrayList, token);
    }

    public static String joinArray(int[] array, String token) {
        ArrayList<Integer> arrayList = new ArrayList<>(Arrays.asList(toObjectList(array)));
        return joinArray(arrayList, token);
    }

    public static String joinArray(List list, String token) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            stringBuilder.append(token + list.get(i));
        }
        return stringBuilder.toString();
    }

    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Compression format is lossless with PNG
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    // Converts image to byte array
    public static byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    // Storing/Retrieving images between activities
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void storeTransactionImage(Context context, Bitmap bitmap) {
        try {
            FileOutputStream fos =
                    context.getApplicationContext().openFileOutput(TRANSACTION_IMAGE_FILENAME,
                            Context.MODE_PRIVATE);

            String bitmapString = bitmapToString(bitmap);
            Log.println(Log.DEBUG, "DEBUG", "SET: Old bitmap string length is "
                    + bitmapString.length());

            // Reduce bitmap quality
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, out);
            bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

            // Write new bitmap to file
            bitmapString = bitmapToString(bitmap);
            fos.write(bitmapString.getBytes());

            Log.println(Log.DEBUG, "DEBUG", "SET: New bitmap string length is "
                    + bitmapString.length());

            // Cleanup
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Screen dimenstions
    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static Bitmap getTransactionImage(Context context) {
        FileInputStream fis = null;
        try {
            fis = context.getApplicationContext().openFileInput(TRANSACTION_IMAGE_FILENAME);
            StringBuilder builder = new StringBuilder();
            int ch;
            while ((ch = fis.read()) != -1) {
                builder.append((char) ch);
            }
            fis.close();

            Log.println(Log.DEBUG, "DEBUG", "GET: Bitmap string length is "
                    + builder.toString().length());

            Bitmap bitmap = stringToBitMap(builder.toString());

            return bitmap;
        } catch (FileNotFoundException e) {
            Log.println(Log.DEBUG, "DEBUG", "File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            Log.println(Log.DEBUG, "DEBUG", "IOException!");
            e.printStackTrace();
        }

        return null;
    }

    // TODO: Turn into informal date, like "Thursday afternoon"
    public static String toInformalDate(Date date) {
        return "No day";
    }

    // Get number of days away given date is from present
    public static int getDaysAgo(Calendar date) {
        Calendar now = Calendar.getInstance();

        // TODO: Make this equation legit
        int daysAgo = 0;
        // Account for months
        daysAgo += (now.get(Calendar.MONTH) - date.get(Calendar.MONTH)) * 30;
        // Days
        daysAgo += now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH);

        return daysAgo;
    }

    public static boolean doesContainText(TextView textView) {
        return (
                (textView != null) &&
                        (textView.getText() != null) &&
                        (textView.getText().length() > 0)
        );
    }

    public static String toDisplayDateTime(Calendar date) {
        StringBuilder stringBuilder = new StringBuilder();

        String meridiem = (date.get(Calendar.AM_PM) == 1) ? "PM" : "AM";
        stringBuilder.append(date.get(Calendar.MONTH)
                + "/" + date.get(Calendar.DATE)
                + "/" + date.get(Calendar.YEAR)
                + " "
                + date.get(Calendar.HOUR)
                + ":" + date.get(Calendar.MINUTE)
                + " " + meridiem);

        return stringBuilder.toString();
    }

    // Return to home activity
    public static void goHome(Activity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
        activity.startActivity(intent);
    }

    // Return to sign-in activity
    public static void goSignIn(Activity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), SignInActivity.class);
        activity.startActivity(intent);
    }

    // Set margins of some view programmatically
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    // Setup secondary nav-bar for current activity
    // TODO: Add modular method for information dialog
    public static void setupSecondaryNavBar(final Activity activity,
                                            final Class pastActivity,
                                            final String title) {
        // Get nav-bar elements
        ImageView backButton = (ImageView) activity.findViewById(R.id.back_button);
        TextView titleText = (TextView) activity.findViewById(R.id.navbar_title);
        ImageView informationButton = (ImageView) activity.findViewById(R.id.information_button);
        ImageView homeButton = (ImageView) activity.findViewById(R.id.home_button);

        // Set nav-bar title
        titleText.setText(title);

        // Button events
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), pastActivity);
                activity.startActivity(intent);
            }
        });
        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.goHome(activity);
            }
        });
    }

    // Do blue on image
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap doBlur(Context context, Bitmap image) {
        final float BITMAP_SCALE = 0.4f;
        final float BLUR_RADIUS = 7.5f;

        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        return date;
    }

    public static Date stringToDate(String dateString, String dateFormat) {
        Date parsed;
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            parsed = format.parse(dateString);
        } catch(ParseException pe) {
            throw new IllegalArgumentException();
        }
        return parsed;
    }

    // Make given color transparent to given degree
    public static int turnColorTransparent(int color, float transparency) {
        return Color.argb(Math.round(Color.alpha(color) * transparency), Color.red(color),
                Color.green(color), Color.blue(color));
    }

    // Remove child nodes for DOM node
    public static void removeNodeChildren(Node node) {
        while (node.hasChildNodes())
            node.removeChild(node.getFirstChild());
    }

    // Check if given page is installed
    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static double distanceBetween(LatLng startPosition, LatLng endPosition) {
        return Math.sqrt(
                Math.pow((startPosition.latitude - endPosition.latitude), 2) +
                        Math.pow((startPosition.longitude - endPosition.longitude), 2));
    }
}
