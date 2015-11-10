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
public class FavoriteUpdateTask extends MovieUpdateTask {

    private static final String LOG_TAG = FavoriteUpdateTask.class.getSimpleName();
    private static final String URL_PATH = "/movie";

    public FavoriteUpdateTask(Context context) {
        super(context);
    }

    @Override
    protected String loadingMessage() {
        return context.getString(R.string.loading_favorites);
    }

    @Override
    protected ContentValues[] doInBackground(String... params) {
        Uri baseUri = Uri.parse(BASE_URL + URL_PATH);

        ContentValues[] values = new ContentValues[params.length];
        for (int i = 0; i < params.length; i++) {
            String id = params[i];
            Log.d(LOG_TAG, "recalling favorite with id: " + id);
            String json = makeRequest(baseUri.buildUpon().appendPath(id).build());
            try {
                values[i] = parseMovie(json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "unable to parse response: " + json, e);
            }
        }
        return values;
    }
}
