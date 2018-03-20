package com.example.alessandro.popularMovies1.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.alessandro.popularMovies1.R;
import com.example.alessandro.popularMovies1.data.FavMoviesContract;
import com.example.alessandro.popularMovies1.model.Movie;
import com.example.alessandro.popularMovies1.model.Review;
import com.example.alessandro.popularMovies1.model.Video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Utility class
public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    // Method based on StackOverflow - https://goo.gl/Bnpjoc
    public static boolean hasImage(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return !hasImage;
    }

    // Method to retrieve Bitmap from ImageView
    public static Bitmap getBitmapFromImageView(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        Bitmap bitmap = null;

        if (hasImage) {
            if (drawable instanceof GlideBitmapDrawable) {
                bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
        }

        return bitmap;
    }

    // Method based on StackOverflow - https://goo.gl/kAjQsY
    public static void saveBitmapToInternalStorage(Context context, Bitmap bitmapImage,
                                                   String fileName) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException while saving poster to internal storage");
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "IOException while saving poster to internal storage");
                e.printStackTrace();
            }
        }
    }

    // Method to delete a file from app internal storage
    public static void deleteFileFromInternalStorage(Context context, String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        file.delete();
    }

    // Method that creates a Movie object from a cursor
    public static Movie createMovieFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(FavMoviesContract.MoviesEntry._ID));
        String title = cursor.getString(cursor.getColumnIndex(FavMoviesContract.MoviesEntry
                .COLUMN_TITLE));
        String releaseDate = cursor.getString(cursor.getColumnIndex(FavMoviesContract
                .MoviesEntry.COLUMN_RELEASE_DATE));
        String voteAverage = cursor.getString(cursor.getColumnIndex(FavMoviesContract
                .MoviesEntry.COLUMN_VOTE_AVERAGE));
        String overview = cursor.getString(cursor.getColumnIndex(FavMoviesContract
                .MoviesEntry.COLUMN_OVERVIEW));
        Uri posterUri = Uri.parse(cursor.getString(cursor.getColumnIndex(FavMoviesContract
                .MoviesEntry.COLUMN_PORTER_URI)));

        return new Movie(id, title, releaseDate, voteAverage, overview, posterUri);
    }

    // Method that creates a Video array  from a cursor
    public static Video[] createVideosFromCursor(Cursor cursor) {
        Video[] videos = null;
        if (cursor != null) {
            videos = new Video[cursor.getCount()];
            int videoIdColumnIndex = cursor.getColumnIndex(FavMoviesContract.VideosEntry._ID);
            int videoKeyColumnIndex = cursor.getColumnIndex(FavMoviesContract.VideosEntry
                    .COLUMN_KEY);
            int videoNameColumnIndex = cursor.getColumnIndex(FavMoviesContract.VideosEntry
                    .COLUMN_NAME);

            while (cursor.moveToNext()) {
                videos[cursor.getPosition()] =
                        new Video(cursor.getString(videoIdColumnIndex),
                                cursor.getString(videoKeyColumnIndex),
                                cursor.getString(videoNameColumnIndex));
            }
        }
        return videos;
    }

    // Method that creates a Movie array  from a cursor
    public static Review[] createReviewsFromCursor(Cursor cursor) {
        Review[] reviews = null;
        if (cursor != null) {
            reviews = new Review[cursor.getCount()];
            int reviewIdColumnIndex = cursor.getColumnIndex(FavMoviesContract.ReviewsEntry
                    ._ID);
            int reviewAuthorColumnIndex = cursor.getColumnIndex(FavMoviesContract
                    .ReviewsEntry.COLUMN_AUTHOR);
            int reviewContentColumnIndex = cursor.getColumnIndex(FavMoviesContract
                    .ReviewsEntry.COLUMN_CONTENT);

            while (cursor.moveToNext()) {
                reviews[cursor.getPosition()] =
                        new Review(cursor.getString(reviewIdColumnIndex),
                                cursor.getString(reviewAuthorColumnIndex),
                                cursor.getString(reviewContentColumnIndex));
            }
        }
        return reviews;
    }

    // Method that gets current Sort preference
    public static String getSortPref(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_popular_value));
    }

    // Method that checks if currentSort parameters is Favorite Sort
    public static boolean isFavoriteSort(Context context, String currentSort) {
        return TextUtils.equals(currentSort, context.getString(R.string.pref_fav_value));

    }

    // Method that checks if current Sort preference is set to Favorite
    public static boolean isFavoriteSort(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currentSort = preferences.getString(context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_popular_value));
        return TextUtils.equals(currentSort, context.getString(R.string.pref_fav_value));

    }

    /**
     * Method to check if internet connection is available or not.
     * Method from StackOverflow - https://goo.gl/GMY6r6
     */
    public static boolean isInternetConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnected();
    }

    // Converts date string with yyyy-MM-dd format to a string in current locale specific format
    public static String formatDateForLocale(String inputDateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date outputDate = inputFormat.parse(inputDateString);
            return DateFormat.getDateInstance().format(outputDate);
        } catch (ParseException e) {
            Log.d(LOG_TAG, "ParseException while parsing result from server." +
                    " Returning original date!");
            e.printStackTrace();
            return inputDateString;
        }
    }
}