package com.tkstr.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Ben Teichman
 */
public class DetailsTask extends AsyncTask<String, Void, String> {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String THUMBNAIL_URL = "http://image.tmdb.org/t/p/w154";

    private ProgressDialog progress;
    private Context context;
    private View view;
    private OkHttpClient client;

    public DetailsTask(Context context, View view) {
        this.context = context;
        this.view = view;
        client = new OkHttpClient();
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
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
            Log.d("BOOM", "FOUND: " + result);
            if (result != null) {
                DetailHolder holder = parseJson(result);
                setViews(holder);
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

    private void setViews(DetailHolder holder) {
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(holder.title);

        TextView year = (TextView) view.findViewById(R.id.year);
        year.setText(holder.year);

        TextView length = (TextView) view.findViewById(R.id.length);
        length.setText(holder.runtime);

        TextView rating = (TextView) view.findViewById(R.id.rating);
        rating.setText(holder.rating);

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(holder.description);

        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail_poster);
        Glide.with(context)
                .load(THUMBNAIL_URL + holder.image)
                .error(android.R.drawable.ic_dialog_alert)
                .into(thumbnail);

        Button button = (Button) view.findViewById(R.id.favourite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Yeah... I'll try to remember that one", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class DetailHolder {
        String title;
        String image;
        String year;
        String runtime;
        String rating;
        String description;
    }
}
