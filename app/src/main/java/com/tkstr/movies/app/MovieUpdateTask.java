package com.tkstr.movies.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tkstr.movies.app.PosterAdapter.MovieHolder;
import com.tkstr.movies.app.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ben Teichman
 */
public abstract class MovieUpdateTask extends NetworkTask<ContentValues[]> {

    private static final String LOG_TAG = MovieUpdateTask.class.getSimpleName();

    protected String sort;
    protected PosterAdapter adapter;

    public MovieUpdateTask(Context context, PosterAdapter adapter, String sort) {
        super(context);
        this.sort = sort;
        this.adapter = adapter;
    }

    protected ContentValues parseMovie(String json) throws JSONException {
        return MovieHolder.fromJson(new JSONObject(json));
    }

    protected ContentValues[] parseMovies(String json) throws JSONException {

        JSONObject response = new JSONObject(json);
        JSONArray results = response.getJSONArray("results");
        ContentValues[] values = new ContentValues[results.length()];
        for (int i = 0; i < results.length(); i++) {
            values[i] = MovieHolder.fromJson(results.getJSONObject(i));
            Log.d(LOG_TAG, "parsing " + values[i]);
        }


        return values;
    }

    public static Uri getContentUriFromSort(String sort) {
        switch (sort) {
            case DiscoveryFragment.SORT_RATING:
                return MovieEntry.TOP_RATED_CONTENT_URI;
            case DiscoveryFragment.SORT_POPULARITY:
                return MovieEntry.POPULAR_CONTENT_URI;
            case DiscoveryFragment.SORT_FAVORITES:
                return MovieEntry.FAVORITES_CONTENT_URI;
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(ContentValues[] result) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = getContentUriFromSort(sort);
        contentResolver.delete(contentUri, null, null);
        contentResolver.bulkInsert(contentUri, result);

        adapter.changeCursor(context.getContentResolver().query(contentUri, MovieEntry.PROJECTION, null, null, null));
        adapter.notifyDataSetChanged();

        super.onPostExecute(result);
    }
}
