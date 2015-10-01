package com.tkstr.movies;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tkstr.movies.DetailFragment.DetailHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Ben Teichman
 */
public class DetailsTask extends AsyncTask<String, Void, String> {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

    private ProgressDialog progress;
    private DetailFragment fragment;
    private View view;
    private OkHttpClient client;

    public DetailsTask(DetailFragment fragment, View view) {
        this.fragment = fragment;
        this.view = view;
        client = new OkHttpClient();
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(fragment.getContext());
        progress.setMessage("Loading Details");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected String doInBackground(String... params) {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(params[0])
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
                fragment.setDetails(parseJson(result)).fillDetails(view);
            }
            progress.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private DetailHolder parseJson(String json) throws JSONException {
        DetailHolder holder = new DetailHolder();

        JSONObject response = new JSONObject(json);
        holder.title = response.getString("title");
        holder.image = response.getString("poster_path");
        holder.year = response.getString("release_date").split("-", 2)[0];
        holder.rating = response.getString("vote_average") + "/10";
        holder.runtime = response.getString("runtime") + "min";
        holder.description = response.getString("overview");

        return holder;
    }


}
