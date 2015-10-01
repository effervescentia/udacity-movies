package com.tkstr.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
public class MovieUpdateTask extends AsyncTask<String, Void, String> {

    private static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";

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
        progress.setMessage("Loading Movies");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected String doInBackground(String... params) {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("sort_by", params[0])
                .appendQueryParameter("api_key", API.KEY)
                .build();

        Request req = new Request.Builder()
                .url(uri.toString())
                .build();

        String json = null;
        try {
            Response res = client.newCall(req).execute();
            json = res.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (result != null) {
                adapter.clear();
                adapter.addAll(parseJson(result));
            }
            if (progress.isShowing()) {
                progress.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<MovieHolder> parseJson(String json) throws JSONException {
        List<MovieHolder> holders = new ArrayList<>();

        JSONObject response = new JSONObject(json);
        JSONArray results = response.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            MovieHolder holder = new MovieHolder();
            holder.id = result.getString("id");
            holder.image = result.getString("poster_path");
            holders.add(holder);
        }

        return holders;
    }
}
