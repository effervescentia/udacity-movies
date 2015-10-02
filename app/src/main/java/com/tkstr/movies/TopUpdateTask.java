package com.tkstr.movies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.tkstr.movies.PosterAdapter.MovieHolder;

import org.json.JSONException;

import java.util.List;

/**
 * @author Ben Teichman
 */
public class TopUpdateTask extends MovieUpdateTask {

    private static final String URL_PATH = "/discover/movie?";

    public TopUpdateTask(Context context, ArrayAdapter adapter) {
        super(context, adapter);
    }

    @Override
    protected int loadingMessage() {
        return R.string.loading_movies;
    }

    @Override
    protected List<MovieHolder> doInBackground(String... params) {
        Uri uri = Uri.parse(BASE_URL + URL_PATH).buildUpon()
                .appendQueryParameter("sort_by", params[0])
                .build();

        List<MovieHolder> movies = null;
        try {
            String json = makeRequest(uri);
            movies = parseJson(json, "results");
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "unable to parse response", e);
        }
        return movies;
    }
}
