package com.tkstr.movies.app;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.tkstr.movies.app.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ben Teichman
 */
public abstract class MovieUpdateTask extends NetworkTask<ContentValues[]> {

    private static final String LOG_TAG = MovieUpdateTask.class.getSimpleName();

    public MovieUpdateTask(Context context) {
        super(context);
    }

    @Override
    protected void onPostExecute(ContentValues[] result) {
        if (result != null) {

        }
        super.onPostExecute(result);
    }

    protected ContentValues parseMovie(String json) throws JSONException {
        return extractMovie(new JSONObject(json));
    }

    protected ContentValues[] parseMovies(String json) throws JSONException {

        JSONObject response = new JSONObject(json);
        JSONArray results = response.getJSONArray("results");
        ContentValues[] values = new ContentValues[results.length()];
        for (int i = 0; i < results.length(); i++) {
            values[i] = extractMovie(results.getJSONObject(i));
            Log.d(LOG_TAG, "parsing " + values[i]);
        }


        return values;
    }

    private ContentValues extractMovie(JSONObject result) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_ID, result.getLong("id"));
        values.put(MovieEntry.COLUMN_TITLE, result.getString("title"));
        values.put(MovieEntry.COLUMN_IMAGE, result.getString("poster_path"));

        return values;
    }
}
