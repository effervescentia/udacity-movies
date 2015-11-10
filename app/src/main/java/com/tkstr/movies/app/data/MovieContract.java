package com.tkstr.movies.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Ben Teichman
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.tkstr.movies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DETAILS = "details";
    public static final String PATH_TOP_RATED = "top";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_FAVORITES = "favorites";

    public static final class DetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "details";

        public static final String COLUMN_ID = "title";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_TRAILERS_JSON = "trailers";
        public static final String COLUMN_REVIEWS_JSON = "reviews";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DETAILS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DETAILS;

        public static Uri buildDetailsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class SimpleEntry implements BaseColumns {
        public static final String TOP_RATED_TABLE_NAME = "top";
        public static final String POPULAR_TABLE_NAME = "popular";
        public static final String FAVORITES_TABLE_NAME = "favorites";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";

        public static final Uri TOP_RATED_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();
        public static final Uri POPULAR_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();
        public static final Uri FAVORITES_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();

        public static final String TOP_RATED_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String POPULAR_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String FAVORITES_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
    }

}
