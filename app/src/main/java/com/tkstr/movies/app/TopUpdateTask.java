package com.tkstr.movies.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tkstr.movies.R;

import org.json.JSONException;

/**
 * @author Ben Teichman
 */
public class TopUpdateTask extends MovieUpdateTask {

    private static final String URL_PATH = "/discover/movie?";

    public TopUpdateTask(Context context, PosterAdapter adapter, String sort) {
        super(context, adapter, sort);
    }

    @Override
    protected String loadingMessage() {
        return context.getString(R.string.loading_movies);
    }

    @Override
    protected ContentValues[] doInBackground(String... params) {

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
}
