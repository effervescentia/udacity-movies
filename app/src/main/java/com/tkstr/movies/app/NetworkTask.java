package com.tkstr.movies.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author Ben Teichman
 */
public abstract class NetworkTask<T> extends AsyncTask<String, Void, T> {

    private static final String LOG_TAG = NetworkTask.class.getSimpleName();

    protected static final String BASE_URL = "http://api.themoviedb.org/3";

    private ProgressDialog progress;
    protected Context context;
    private OkHttpClient client;

    public NetworkTask(Context context) {
        this.context = context;
        client = new OkHttpClient();
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setMessage(loadingMessage());
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected void onPostExecute(T result) {
        if (progress.isShowing()) {
            progress.dismiss();
        }
    }

    protected String makeRequest(Uri uri) {
        Log.d(LOG_TAG, "making request for " + uri);
        uri = uri.buildUpon().appendQueryParameter("api_key", API.KEY).build();

        Request req = new Request.Builder()
                .url(uri.toString())
                .build();

        String result = null;
        try {
            Response res = client.newCall(req).execute();
            result = res.body().string();
        } catch (IOException e) {
            Log.e(LOG_TAG, "call to the movie db failed", e);
        }
        return result;
    }

    protected abstract String loadingMessage();
}
