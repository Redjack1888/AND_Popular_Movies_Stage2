package com.example.alessandro.popularMovies1.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// Content provider
public class FavMoviesProvider extends ContentProvider {

    private Context context;

    private static final int MOVIES = 100;
    private static final int MOVIES_ITEM = 101;
    private static final int VIDEOS = 200;
    private static final int VIDEOS_ITEM = 201;
    private static final int REVIEWS = 300;
    private static final int REVIEWS_ITEM = 301;
    private static final UriMatcher mUriMatcher = getUriMatcher();
    private FavMoviesDbHelper mFavoriteMoviesDBHelper;

    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavMoviesContract.CONTENT_AUTHORITY, FavMoviesContract
                .PATH_MOVIES, MOVIES);
        uriMatcher.addURI(FavMoviesContract.CONTENT_AUTHORITY, FavMoviesContract
                        .PATH_MOVIES + "/#",
                MOVIES_ITEM);
        uriMatcher.addURI(FavMoviesContract.CONTENT_AUTHORITY, FavMoviesContract
                .PATH_VIDEOS, VIDEOS);
        uriMatcher.addURI(FavMoviesContract.CONTENT_AUTHORITY, FavMoviesContract
                        .PATH_VIDEOS + "/#",
                VIDEOS_ITEM);
        uriMatcher.addURI(FavMoviesContract.CONTENT_AUTHORITY, FavMoviesContract
                .PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(FavMoviesContract.CONTENT_AUTHORITY, FavMoviesContract
                        .PATH_REVIEWS + "/#",
                REVIEWS_ITEM);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mFavoriteMoviesDBHelper = new FavMoviesDbHelper(getContext());
        context = getContext();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[]
            selectionArgs,
                        String sortOrder) {

        Cursor cursor;
        String id;
        switch (mUriMatcher.match(uri)) {
            case MOVIES: {
                cursor = mFavoriteMoviesDBHelper.getReadableDatabase().query(
                        FavMoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIES_ITEM: {
                id = uri.getPathSegments().get(1);
                cursor = getItem(FavMoviesContract.MoviesEntry.TABLE_NAME,
                        id, projection, selection, selectionArgs, sortOrder);
                break;
            }

            case VIDEOS: {
                cursor = mFavoriteMoviesDBHelper.getReadableDatabase().query(
                        FavMoviesContract.VideosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case VIDEOS_ITEM: {
                id = uri.getPathSegments().get(1);
                cursor = getItem(FavMoviesContract.VideosEntry.TABLE_NAME,
                        id, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case REVIEWS: {
                cursor = mFavoriteMoviesDBHelper.getReadableDatabase().query(
                        FavMoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEWS_ITEM: {
                id = uri.getPathSegments().get(1);
                cursor = getItem(FavMoviesContract.MoviesEntry.TABLE_NAME,
                        id, projection, selection, selectionArgs, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(context.getContentResolver(), uri);

        return cursor;
    }

    private Cursor getItem(String tableName, String id, String[] projection, String selection,
                           String[]
                                   selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(tableName);

        if (id != null) {
            sqliteQueryBuilder.appendWhere("_id" + " = " + id);
        }

        return sqliteQueryBuilder.query(mFavoriteMoviesDBHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MOVIES:
                return FavMoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIES_ITEM:
                return FavMoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case VIDEOS:
                return FavMoviesContract.VideosEntry.CONTENT_TYPE;
            case VIDEOS_ITEM:
                return FavMoviesContract.VideosEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return FavMoviesContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_ITEM:
                return FavMoviesContract.ReviewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mFavoriteMoviesDBHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(FavMoviesContract.MoviesEntry.TABLE_NAME, null,
                        contentValues);
                if (_id > 0)
                    returnUri = FavMoviesContract.MoviesEntry.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                long _id = db.insert(FavMoviesContract.VideosEntry.TABLE_NAME, null,
                        contentValues);
                if (_id > 0)
                    returnUri = FavMoviesContract.VideosEntry.buildVideosUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(FavMoviesContract.ReviewsEntry.TABLE_NAME, null,
                        contentValues);
                if (_id > 0)
                    returnUri = FavMoviesContract.ReviewsEntry.buildReviewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        context.getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMoviesDBHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(FavMoviesContract.MoviesEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case VIDEOS:
                rowsDeleted = db.delete(FavMoviesContract.VideosEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(FavMoviesContract.ReviewsEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMoviesDBHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(FavMoviesContract.MoviesEntry.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            case VIDEOS:
                rowsUpdated = db.update(FavMoviesContract.VideosEntry.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(FavMoviesContract.ReviewsEntry.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
