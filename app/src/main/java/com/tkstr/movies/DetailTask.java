package com.tkstr.movies;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.tkstr.movies.DetailFragment.DetailHolder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ben Teichman
 */
public class DetailTask extends NetworkTask<DetailHolder> {

    private static final String LOG_TAG = DetailTask.class.getSimpleName();
    private static final String FIND_PATH = "/movie";
    private static final String VIDEOS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";

    private DetailFragment fragment;
    private View view;
    private String title;
    private FavoritePrefs favorites;

    public DetailTask(DetailFragment fragment, View view, String title) {
        super(fragment.getContext());
        this.fragment = fragment;
        this.view = view;
        this.title = title;
        favorites = new FavoritePrefs(fragment.getActivity());
    }

    @Override
    protected DetailHolder doInBackground(String... params) {
        Uri baseUri = Uri.parse(BASE_URL + FIND_PATH).buildUpon()
                .appendPath(params[0])
                .build();

        Uri videosUri = baseUri.buildUpon()
                .appendPath(VIDEOS_PATH)
                .build();

        Uri reviewsUri = baseUri.buildUpon()
                .appendPath(REVIEWS_PATH)
                .build();

        DetailHolder details = null;
        try {
            String detailsJson = makeRequest(baseUri);
            Log.d(LOG_TAG, "details: " + detailsJson);
            String videosJson = makeRequest(videosUri);
            Log.d(LOG_TAG, "videos: " + videosJson);
            String reviewsJson = makeRequest(reviewsUri);
            Log.d(LOG_TAG, "reviews: " + reviewsJson);
            details = parseJson(detailsJson, videosJson, reviewsJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "unable to parse response", e);
        }
        return details;
    }

    @Override
    protected void onPostExecute(DetailHolder details) {
        if (details != null) {
            fragment.setDetails(details).fillDetails(view);
        }
        super.onPostExecute(details);
    }

    @Override
    protected String loadingMessage() {
        return fragment.getResources().getString(R.string.loading_details) + " " + title;
    }

    private DetailHolder parseJson(String movieJson, String videosJson, String reviewsJson) throws JSONException {
        DetailHolder holder = new DetailHolder();

        JSONObject response = new JSONObject(movieJson);
        holder.id = response.getString("id");
        holder.title = response.getString("title");
        holder.image = response.getString("poster_path");
        holder.year = response.getString("release_date").split("-", 2)[0];
        holder.rating = response.getString("vote_average") + "/10";
        holder.runtime = response.getString("runtime") + "min";
        holder.description = response.getString("overview");
        holder.favorite = favorites.isFavorite(holder.id);

        return holder;
    }


}
