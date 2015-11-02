package com.tkstr.movies;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.tkstr.movies.PosterAdapter.MovieHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ben Teichman
 */
public abstract class MovieUpdateTask extends NetworkTask<List<MovieHolder>> {

    protected static final String BASE_URL = "http://api.themoviedb.org/3";

    private ArrayAdapter adapter;

    public MovieUpdateTask(Context context, ArrayAdapter adapter) {
        super(context);
        this.adapter = adapter;
    }

    @Override
    protected void onPostExecute(List<MovieHolder> result) {
        if (result != null) {
            adapter.clear();
            //noinspection unchecked
            adapter.addAll(result);
        }
        super.onPostExecute(result);
    }

    protected MovieHolder parseMovie(String json) throws JSONException {
        return extractMovie(new JSONObject(json));
    }

    protected List<MovieHolder> parseMovies(String json) throws JSONException {
        List<MovieHolder> holders = new ArrayList<>();

        JSONObject response = new JSONObject(json);
        JSONArray results = response.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            holders.add(extractMovie(results.getJSONObject(i)));
        }

        return holders;
    }

    private MovieHolder extractMovie(JSONObject result) throws JSONException {
        MovieHolder holder = new MovieHolder();
        holder.id = result.getString("id");
        holder.image = result.getString("poster_path");
        holder.title = result.getString("title");
        return holder;
    }
}
