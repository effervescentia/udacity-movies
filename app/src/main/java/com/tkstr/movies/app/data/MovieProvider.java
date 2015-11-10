package com.tkstr.movies.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tkstr.movies.app.data.MovieContract.DetailEntry;
import com.tkstr.movies.app.data.MovieContract.MovieEntry;

/**
 * @author Ben Teichman
 */
public class MovieProvider extends ContentProvider {

    private static final String LOG_KEY = MovieProvider.class.getSimpleName();

    private MovieDbHelper dbHelper;

    private static final SQLiteQueryBuilder sqlQueryBuilder;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    static final int DETAILS = 100;
    static final int DETAILS_WITH_ID = 101;
    static final int TOP_RATED = 200;
    static final int POPULAR = 300;
    static final int FAVORITES = 400;

    static {
        sqlQueryBuilder = new SQLiteQueryBuilder();
        sqlQueryBuilder.setTables(DetailEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_DETAILS, DETAILS);
        matcher.addURI(authority, MovieContract.PATH_DETAILS + "/*", DETAILS_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED, TOP_RATED);
        matcher.addURI(authority, MovieContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITES);

        return matcher;
    }

    private static final String idSelection = DetailEntry.TABLE_NAME + "." + DetailEntry.COLUMN_ID + " = ?";

    private Cursor getMovieDetailsById(Uri uri, String[] projection, String sortOrder) {
        String movieId = DetailEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection = idSelection;

        return sqlQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        Log.d(LOG_KEY, "query called for " + uri);

        switch (uriMatcher.match(uri)) {
            case DETAILS_WITH_ID:
                cursor = getMovieDetailsById(uri, projection, sortOrder);
                break;
            case TOP_RATED:
                cursor = dbHelper.getReadableDatabase().query(
                        MovieEntry.TOP_RATED_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case POPULAR:
                cursor = dbHelper.getReadableDatabase().query(
                        MovieEntry.POPULAR_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITES:
                cursor = dbHelper.getReadableDatabase().query(
                        MovieEntry.FAVORITES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case DETAILS_WITH_ID:
                return DetailEntry.CONTENT_ITEM_TYPE;
            case DETAILS:
                return DetailEntry.CONTENT_TYPE;
            case TOP_RATED:
                return MovieEntry.TOP_RATED_CONTENT_TYPE;
            case POPULAR:
                return MovieEntry.POPULAR_CONTENT_TYPE;
            case FAVORITES:
                return MovieEntry.FAVORITES_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        Log.d(LOG_KEY, "insert called for " + uri);

        switch (match) {
            case DETAILS:
                long _id = db.insert(DetailEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DetailEntry.buildDetailsUri(values.getAsLong(DetailEntry.COLUMN_ID));
                } else {
                    throw new SQLException("Failed to insert movie at uri " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int affected = 0;

        Log.d(LOG_KEY, "delete called for " + uri);

        if (selection == null) {
            selection = "1";
        }
        switch (match) {
            case DETAILS:
                affected = db.delete(DetailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_RATED:
                affected = db.delete(MovieEntry.TOP_RATED_TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR:
                affected = db.delete(MovieEntry.POPULAR_TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                affected = db.delete(MovieEntry.FAVORITES_TABLE_NAME, selection, selectionArgs);
                break;
        }

        if (affected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int affected = 0;

        Log.d(LOG_KEY, "update called for " + uri);

        switch (match) {
            case DETAILS:
                affected = db.update(DetailEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
        }
        if (affected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affected;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        Log.d(LOG_KEY, "bulk_insert called for " + uri);

        switch (match) {
            case DETAILS:
                return doBulkInsert(uri, values, db, DetailEntry.TABLE_NAME);
            case TOP_RATED:
                return doBulkInsert(uri, values, db, MovieEntry.TOP_RATED_TABLE_NAME);
            case POPULAR:
                return doBulkInsert(uri, values, db, MovieEntry.POPULAR_TABLE_NAME);
            case FAVORITES:
                return doBulkInsert(uri, values, db, MovieEntry.FAVORITES_TABLE_NAME);
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int doBulkInsert(Uri uri, ContentValues[] values, SQLiteDatabase db, String tableName) {
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues contentValues : values) {
                long _id = db.insert(tableName, null, contentValues);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (returnCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnCount;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}
