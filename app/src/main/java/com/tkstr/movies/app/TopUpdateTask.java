package com.tkstr.movies.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tkstr.movies.R;
import com.tkstr.movies.app.data.MovieContract.MovieEntry;

import org.json.JSONException;

/**
 * @author Ben Teichman
 */
public class TopUpdateTask extends MovieUpdateTask {

    private static final String URL_PATH = "/discover/movie?";
    private String sort;

    public TopUpdateTask(Context context) {
        super(context);
    }

    @Override
    protected String loadingMessage() {
        return context.getString(R.string.loading_movies);
    }

    @Override
    protected ContentValues[] doInBackground(String... params) {
        sort = params[0];

        Uri uri = Uri.parse(BASE_URL + URL_PATH).buildUpon()
                .appendQueryParameter("sort_by", sort)
                .build();

        ContentValues[] values = null;
        try {
            String json = makeRequest(uri);
            values = parseMovies(json);
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "unable to parse response", e);
        }
        return values;
    }

    @Override
    protected void onPostExecute(ContentValues[] result) {
        ContentResolver contentResolver = context.getContentResolver();
        if (DiscoveryFragment.SORT_RATING.equals(sort)) {
            contentResolver.delete(MovieEntry.TOP_RATED_CONTENT_URI, null, null);
            contentResolver.bulkInsert(MovieEntry.TOP_RATED_CONTENT_URI, result);
        } else {
            contentResolver.delete(MovieEntry.POPULAR_CONTENT_URI, null, null);
            contentResolver.bulkInsert(MovieEntry.POPULAR_CONTENT_URI, result);
        }
        super.onPostExecute(result);
    }
}
