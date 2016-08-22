package ishaanmalhi.com.popularmoviesapp;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by ishaan on 14/8/16.
 */
public class Utility {

    /*
     Contains functions to store downloaded movie poster and background poster to
      internal storage for later retrieval

      This works better than storing that actual bitmap in an SQLite
      database since the users can utilize the movie images elsewhere

       Returns { @directory_path } to be used to later to load the image
      */

    public static Bitmap convertToBitmap(ImageView imageView) {
        imageView.buildDrawingCache();
        return (Bitmap) imageView.getDrawingCache();
    }

    public static String saveToInternalStorage(ImageView imageView, String image_name, Context context) {

        Bitmap moviePoster = convertToBitmap(imageView);
        // Path to  /sdcard/PopularMoviesApp
        File movieDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/Popular Movies");
        if (movieDir.mkdirs()) {
            Timber.e("Directory not created");
        }
        // Create imageDir
        File posterFilePath = new File(movieDir,image_name+".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(posterFilePath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            moviePoster.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            Timber.e("IO Error: " + e);
        }
        String path = posterFilePath.getAbsolutePath();
        Timber.v("Image saved at " + path);

        return path;
    }

    public static String getSortOrder(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sort_order_key),context.getString(R.string.pref_sort_order_default));
    }

    //function to check for network access
    public static boolean isNetWorkAvailable(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }
}
