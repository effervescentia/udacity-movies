package com.tkstr.movies.app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.tkstr.movies.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ben Teichman
 */
public class FavoriteUpdateTask extends MovieUpdateTask {

    private static final String LOG_TAG = FavoriteUpdateTask.class.getSimpleName();
    private static final String URL_PATH = "/movie";

    public FavoriteUpdateTask(Context context, ArrayAdapter adapter) {
        super(context, adapter);
    }

    @Override
    protected String loadingMessage() {
        return context.getString(R.string.loading_favorites);
    }

    @Override
    protected List<PosterAdapter.MovieHolder> doInBackground(String... params) {
        Uri baseUri = Uri.parse(BASE_URL + URL_PATH);

        List<PosterAdapter.MovieHolder> movies = new ArrayList<>();
        for (String id : params) {
            Log.d(LOG_TAG, "recalling favorite with id: " + id);
            String json = makeRequest(baseUri.buildUpon().appendPath(id).build());
            try {
                movies.add(parseMovie(json));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "unable to parse response: " + json, e);
            }
        }
        return movies;
    }
}
