package com.tkstr.movies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.tkstr.movies.PosterAdapter.MovieHolder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ben Teichman
 */
public class FavoriteUpdateTask extends MovieUpdateTask {

    private static final String URL_PATH = "/find";

    public FavoriteUpdateTask(Context context, ArrayAdapter adapter) {
        super(context, adapter);
    }

    @Override
    protected int loadingMessage() {
        return R.string.loading_favorites;
    }

    @Override
    protected List<MovieHolder> doInBackground(String... params) {
        Uri baseUri = Uri.parse(BASE_URL + URL_PATH);

        List<MovieHolder> movies = new ArrayList<>();
        for (String id : params) {
            String json = makeRequest(baseUri.buildUpon().appendPath(id).build());
            try {
                movies.addAll(parseJson(json, "movie_results"));
            } catch (JSONException e) {
                Log.e(getClass().getSimpleName(), "unable to parse response", e);
            }
        }
        return movies;
    }
}
