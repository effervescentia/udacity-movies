package com.tkstr.movies.app;

import android.net.Uri;
import android.util.Log;

import com.tkstr.movies.R;
import com.tkstr.movies.app.DetailFragment.DetailHolder;
import com.tkstr.movies.app.DetailFragment.ReviewHolder;
import com.tkstr.movies.app.DetailFragment.TrailerHolder;
import com.tkstr.movies.app.data.MovieContract.DetailEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Ben Teichman
 */
public class DetailTask extends NetworkTask<DetailHolder> {

    private static final String LOG_TAG = DetailTask.class.getSimpleName();
    private static final String FIND_PATH = "/movie";
    private static final String TRAILERS = "trailers";
    private static final String REVIEWS = "reviews";

    private DetailFragment fragment;
    private String title;
    private FavoritePrefs favorites;

    public DetailTask(DetailFragment fragment, String title) {
        super(fragment.getContext());
        this.fragment = fragment;
        this.title = title;
        favorites = new FavoritePrefs(fragment.getActivity());
    }

    @Override
    protected DetailHolder doInBackground(String... params) {
        Uri baseUri = Uri.parse(BASE_URL + FIND_PATH).buildUpon()
                .appendPath(params[0])
                .appendQueryParameter("append_to_response", TRAILERS + "," + REVIEWS)
                .build();

        DetailHolder details = null;
        try {
            String detailsJson = makeRequest(baseUri);
            Log.d(LOG_TAG, "details: " + detailsJson);
            details = parseJson(detailsJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "unable to parse response", e);
        }
        return details;
    }

    @Override
    protected void onPostExecute(DetailHolder details) {
        if (details != null) {
            context.getContentResolver().insert(DetailEntry.CONTENT_URI, details.toContentValues());
            fragment.setDetails(details).fillDetails();
        }
        super.onPostExecute(details);
    }

    @Override
    protected String loadingMessage() {
        return fragment.getResources().getString(R.string.loading_details) + " " + title;
    }

    private DetailHolder parseJson(String movieJson) throws JSONException {
        DetailHolder holder = new DetailHolder();

        JSONObject movie = new JSONObject(movieJson);
        holder.id = movie.getLong("id");
        holder.title = movie.getString("title");
        holder.image = movie.getString("poster_path");
        holder.year = Integer.parseInt(movie.getString("release_date").split("-", 2)[0]);
        holder.rating = movie.getDouble("vote_average");
        holder.runtime = movie.getInt("runtime");
        holder.description = movie.getString("overview");
        holder.favorite = favorites.isFavorite(String.valueOf(holder.id));

        JSONArray trailers = movie.getJSONObject(TRAILERS).getJSONArray("youtube");

        holder.trailers = new ArrayList<>();
        for (int i = 0; i < trailers.length(); i++) {
            JSONObject trailer = trailers.getJSONObject(i);

            TrailerHolder trailerHolder = new TrailerHolder();
            trailerHolder.url = "http://youtube.com/watch?v=" + trailer.getString("source");
            trailerHolder.name = trailer.getString("name");
            holder.trailers.add(trailerHolder);
        }

        JSONArray reviews = movie.getJSONObject(REVIEWS).getJSONArray("results");

        holder.reviews = new ArrayList<>();
        for (int i = 0; i < reviews.length(); i++) {
            JSONObject review = reviews.getJSONObject(i);

            ReviewHolder reviewHolder = new ReviewHolder();
            reviewHolder.author = review.getString("author");
            reviewHolder.content = review.getString("content");
            holder.reviews.add(reviewHolder);
        }

        return holder;
    }
}
