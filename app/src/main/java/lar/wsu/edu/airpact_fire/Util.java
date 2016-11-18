package lar.wsu.edu.airpact_fire;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Class for basic utilities used throughout app
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Util {
    // TODO: create subclasses for each category of functions

    // Storage permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Image fields
    private static final String TRANSACTION_IMAGE_FILENAME = "transaction_image";
    private static final int IMAGE_COMPRESSION_QUALITY = 0; // 100;

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
            URL myUrl = new URL(Post.SERVER_BASE_URL);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            return true;
        } catch (Exception e) {
            Toast.makeText(activity.getApplicationContext(), "Cannot connect to server.", Toast.LENGTH_SHORT).show();
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
                && (x <  (view.getX() + view.getWidth()))
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
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    // TODO Get analysis of circular area around x and y point
    public static int getPixelAtPos(ImageView imageView, int x, int y) {
        //Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
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
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Create image file
        File image = new File(storageDir, imageFileName + ".jpg");

        // Get image location
//        Post.ImageLocation = image.getAbsolutePath();

        return image;
    }
    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Compression format is loss less with PNG
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
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
                    context.getApplicationContext().openFileOutput(TRANSACTION_IMAGE_FILENAME, Context.MODE_PRIVATE);

            String bitmapString = bitmapToString(bitmap);
            Log.println(Log.DEBUG, "DEBUG", "SET: Old bitmap string length is " + bitmapString.length());

            // Reduce bitmap quality
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, out);
            bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

            // Write new bitmap to file
            bitmapString = bitmapToString(bitmap);
            fos.write(bitmapString.getBytes());

            Log.println(Log.DEBUG, "DEBUG", "SET: New bitmap string length is " + bitmapString.length());

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

            Log.println(Log.DEBUG, "DEBUG", "GET: Bitmap string length is " + builder.toString().length());

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

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    // Make given color transparent to given degree
    public static int turnColorTransparent(int color, float transparency) {
        return Color.argb(Math.round(Color.alpha(color) * transparency), Color.red(color), Color.green(color), Color.blue(color));
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

}
