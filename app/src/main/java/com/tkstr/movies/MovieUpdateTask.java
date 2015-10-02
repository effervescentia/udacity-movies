package com.tkstr.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tkstr.movies.PosterAdapter.MovieHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ben Teichman
 */
public abstract class MovieUpdateTask extends AsyncTask<String, Void, List<MovieHolder>> {

    protected static final String BASE_URL = "http://api.themoviedb.org/3";

    private ProgressDialog progress;
    private Context context;
    private ArrayAdapter adapter;
    private OkHttpClient client;

    public MovieUpdateTask(Context context, ArrayAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        client = new OkHttpClient();
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setMessage(context.getResources().getString(loadingMessage()));
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected void onPostExecute(List<MovieHolder> result) {
        if (result != null) {
            adapter.clear();
            //noinspection unchecked
            adapter.addAll(result);
        }
        if (progress.isShowing()) {
            progress.dismiss();
        }
    }

    protected String makeRequest(Uri uri) {
        uri = uri.buildUpon().appendQueryParameter("api_key", API.KEY).build();

        Request req = new Request.Builder()
                .url(uri.toString())
                .build();

        String result = null;
        try {
            Response res = client.newCall(req).execute();
            result = res.body().string();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "call to the movie db failed", e);
        }
        return result;
    }

    protected List<MovieHolder> parseJson(String json, String resultsKey) throws JSONException {
        List<MovieHolder> holders = new ArrayList<>();

        JSONObject response = new JSONObject(json);
        JSONArray results = response.getJSONArray(resultsKey);
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            MovieHolder holder = new MovieHolder();
            holder.id = result.getString("id");
            holder.image = result.getString("poster_path");
            holder.title = result.getString("title");
            holders.add(holder);
        }

        return holders;
    }

    protected abstract int loadingMessage();
}
