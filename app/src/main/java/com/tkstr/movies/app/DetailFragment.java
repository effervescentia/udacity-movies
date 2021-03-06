package com.tkstr.movies.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tkstr.movies.R;
import com.tkstr.movies.app.data.MovieContract.DetailEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * @author Ben Teichman
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String THUMBNAIL_URL = "http://image.tmdb.org/t/p/w154";
    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String DETAILS_KEY = "details";

    private DetailHolder details;
    private FavoritePrefs favorites;
    private ComboAdapter adapter;
    private ShareActionProvider shareActionProvider;
    private View fragmentView;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            details = savedInstanceState.getParcelable(DETAILS_KEY);
        }
        favorites = new FavoritePrefs(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem share = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);

        ListView comboList = (ListView) fragmentView.findViewById(R.id.combo_list);

        comboList.addHeaderView(inflater.inflate(R.layout.header_detail, comboList, false));

        if (details == null) {
            long id = getActivity().getIntent().getLongExtra(ID_KEY, -1);
            String title = getActivity().getIntent().getStringExtra(TITLE_KEY);
            updateDetails(id, title);
        } else {
            fillDetails();
        }

        return fragmentView;
    }

    public void updateDetails(Long id, String title) {
        if (id != null && title != null && this.getContext() != null) {
            Log.d(LOG_TAG, "loading details for : " + title);

            String[] projection = new String[]{DetailEntry._ID, DetailEntry.COLUMN_ID, DetailEntry.COLUMN_TITLE,
                    DetailEntry.COLUMN_IMAGE, DetailEntry.COLUMN_YEAR, DetailEntry.COLUMN_RUNTIME, DetailEntry.COLUMN_RATING,
                    DetailEntry.COLUMN_DESCRIPTION, DetailEntry.COLUMN_FAVORITE, DetailEntry.COLUMN_TRAILERS_JSON,
                    DetailEntry.COLUMN_REVIEWS_JSON};
            Cursor cursor = getActivity().getContentResolver().query(DetailEntry.buildDetailsUri(id), projection, null, null, null);
            if (cursor.moveToFirst()) {
                details = DetailHolder.fromCursor(cursor);
                fillDetails();
            } else if (hasNetworkAccess()) {
                new DetailTask(this, title).execute(String.valueOf(id));
            }
            cursor.close();
        }
    }

    private boolean hasNetworkAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    protected void fillDetails() {
        TextView title = (TextView) fragmentView.findViewById(R.id.title);
        title.setText(details.title);

        TextView year = (TextView) fragmentView.findViewById(R.id.year);
        year.setText(String.valueOf(details.year));

        TextView length = (TextView) fragmentView.findViewById(R.id.length);
        length.setText(String.format("%dmin", details.runtime));

        TextView rating = (TextView) fragmentView.findViewById(R.id.rating);
        rating.setText(String.format("%.1f/10", details.rating));

        final TextView description = (TextView) fragmentView.findViewById(R.id.description);
        description.setText(details.description);

        ImageView thumbnail = (ImageView) fragmentView.findViewById(R.id.thumbnail_poster);
        Glide.with(getContext())
                .load(THUMBNAIL_URL + details.image)
                .error(android.R.drawable.ic_dialog_alert)
                .into(thumbnail);

        FloatingActionButton button = (FloatingActionButton) fragmentView.findViewById(R.id.favorite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details.favorite = !details.favorite;
                setFavorite();
                Toast.makeText(getContext(), details.favorite ? getString(R.string.silly_like) : getString(R.string.silly_dislike), Toast.LENGTH_SHORT).show();
                styleFavoriteButton((FloatingActionButton) v);
            }
        });
        styleFavoriteButton(button);

        ListView comboList = (ListView) fragmentView.findViewById(R.id.combo_list);
        adapter = new ComboAdapter(getContext(), details.trailers, details.reviews);

        if (details.trailers != null && details.trailers.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, details.trailers.get(0).url);
            intent.setType("text/plain");

            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(intent);
            }
        }

        comboList.setAdapter(adapter);

        comboList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    position--; //to account for header

                    int itemType = adapter.getItemViewType(position);
                    if (itemType == ComboAdapter.TRAILER_TYPE) {
                        TrailerHolder holder = (TrailerHolder) adapter.getItem(position);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(holder.url)));
                    }
                }
            }
        });
    }

    private void styleFavoriteButton(FloatingActionButton button) {
        if (details.favorite) {
            button.setImageResource(R.drawable.ic_star_favorite);
        } else {
            button.setImageResource(R.drawable.ic_star);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DETAILS_KEY, details);
    }

    @Override
    public void onStop() {
        super.onStop();
        setFavorite();
    }

    private void setFavorite() {
        if (details != null) {
            favorites.setFavorite(String.valueOf(details.id), details.favorite);
        }
    }

    public DetailHolder getDetails() {
        return details;
    }

    public DetailFragment setDetails(DetailHolder details) {
        this.details = details;
        return this;
    }

    public static class DetailHolder implements Parcelable {
        private static final String ID_KEY = "id";
        private static final String TITLE_KEY = "title";
        private static final String IMAGE_KEY = "image";
        private static final String YEAR_KEY = "year";
        private static final String RUNTIME_KEY = "runtime";
        private static final String RATING_KEY = "rating";
        private static final String DESCRIPTION_KEY = "description";
        private static final String FAVORITE_KEY = "favorite";
        private static final String TRAILERS_KEY = "trailers";
        private static final String REVIEWS_KEY = "reviews";

        long id;
        String title;
        String image;
        int year;
        int runtime;
        double rating;
        String description;
        boolean favorite;
        ArrayList<TrailerHolder> trailers;
        ArrayList<ReviewHolder> reviews;

        public DetailHolder() {
        }

        protected DetailHolder(Parcel in) {
            Bundle bundle = in.readBundle();
            id = bundle.getLong(ID_KEY);
            title = bundle.getString(TITLE_KEY);
            image = bundle.getString(IMAGE_KEY);
            year = bundle.getInt(YEAR_KEY);
            runtime = bundle.getInt(RUNTIME_KEY);
            rating = bundle.getDouble(RATING_KEY);
            description = bundle.getString(DESCRIPTION_KEY);
            favorite = bundle.getBoolean(FAVORITE_KEY);
            trailers = bundle.getParcelableArrayList(TRAILERS_KEY);
            reviews = bundle.getParcelableArrayList(REVIEWS_KEY);
        }

        public static final Creator<DetailHolder> CREATOR = new Creator<DetailHolder>() {
            @Override
            public DetailHolder createFromParcel(Parcel in) {
                return new DetailHolder(in);
            }

            @Override
            public DetailHolder[] newArray(int size) {
                return new DetailHolder[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Bundle bundle = new Bundle();
            bundle.putLong(ID_KEY, id);
            bundle.putString(TITLE_KEY, title);
            bundle.putString(IMAGE_KEY, image);
            bundle.putInt(YEAR_KEY, year);
            bundle.putInt(RUNTIME_KEY, runtime);
            bundle.putDouble(RATING_KEY, rating);
            bundle.putString(DESCRIPTION_KEY, description);
            bundle.putBoolean(FAVORITE_KEY, favorite);
            bundle.putParcelableArrayList(TRAILERS_KEY, trailers);
            bundle.putParcelableArrayList(REVIEWS_KEY, reviews);

            dest.writeBundle(bundle);
        }

        public ContentValues toContentValues() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DetailEntry.COLUMN_ID, id);
            contentValues.put(DetailEntry.COLUMN_TITLE, title);
            contentValues.put(DetailEntry.COLUMN_IMAGE, image);
            contentValues.put(DetailEntry.COLUMN_YEAR, year);
            contentValues.put(DetailEntry.COLUMN_RUNTIME, runtime);
            contentValues.put(DetailEntry.COLUMN_RATING, rating);
            contentValues.put(DetailEntry.COLUMN_DESCRIPTION, description);
            contentValues.put(DetailEntry.COLUMN_FAVORITE, favorite ? 1 : 0);

            JSONArray jsonTrailers = new JSONArray();
            JSONArray jsonReviews = new JSONArray();
            try {
                for (TrailerHolder trailer : trailers) {
                    jsonTrailers.put(trailer.toJson());
                }

                for (ReviewHolder review : reviews) {
                    jsonReviews.put(review.toJson());
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "unable to convert json for SQL", e);
            }

            contentValues.put(DetailEntry.COLUMN_TRAILERS_JSON, jsonTrailers.toString());
            contentValues.put(DetailEntry.COLUMN_REVIEWS_JSON, jsonReviews.toString());

            return contentValues;
        }

        public static DetailHolder fromCursor(Cursor cursor) {
            DetailHolder holder = new DetailHolder();

            int idIndex = cursor.getColumnIndex(DetailEntry.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(DetailEntry.COLUMN_TITLE);
            int imageIndex = cursor.getColumnIndex(DetailEntry.COLUMN_IMAGE);
            int yearIndex = cursor.getColumnIndex(DetailEntry.COLUMN_YEAR);
            int runtimeIndex = cursor.getColumnIndex(DetailEntry.COLUMN_RUNTIME);
            int ratingIndex = cursor.getColumnIndex(DetailEntry.COLUMN_RATING);
            int descriptionIndex = cursor.getColumnIndex(DetailEntry.COLUMN_DESCRIPTION);
            int favoriteIndex = cursor.getColumnIndex(DetailEntry.COLUMN_FAVORITE);
            int trailersIndex = cursor.getColumnIndex(DetailEntry.COLUMN_TRAILERS_JSON);
            int reviewsIndex = cursor.getColumnIndex(DetailEntry.COLUMN_REVIEWS_JSON);

            holder.id = cursor.getLong(idIndex);
            holder.title = cursor.getString(titleIndex);
            holder.image = cursor.getString(imageIndex);
            holder.year = cursor.getInt(yearIndex);
            holder.runtime = cursor.getInt(runtimeIndex);
            holder.rating = cursor.getDouble(ratingIndex);
            holder.description = cursor.getString(descriptionIndex);
            holder.favorite = cursor.getInt(favoriteIndex) == 1;
            holder.trailers = new ArrayList<>();
            holder.reviews = new ArrayList<>();

            try {
                JSONArray trailers = new JSONArray(cursor.getString(trailersIndex));
                for (int i = 0; i < trailers.length(); i++) {
                    holder.trailers.add(TrailerHolder.fromJson(trailers.getJSONObject(i)));
                }

                JSONArray reviews = new JSONArray(cursor.getString(reviewsIndex));
                for (int i = 0; i < reviews.length(); i++) {
                    holder.reviews.add(ReviewHolder.fromJson(reviews.getJSONObject(i)));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "unable to parse json from SQLite", e);
            }

            return holder;
        }
    }

    public abstract static class MetadataHolder implements Parcelable {
    }

    public static class TrailerHolder extends MetadataHolder {

        private static final String NAME_KEY = "name";
        private static final String URL_KEY = "url";

        String name;
        String url;

        public TrailerHolder() {
        }

        public TrailerHolder(Parcel in) {
            Bundle bundle = in.readBundle();
            name = bundle.getString(NAME_KEY);
            url = bundle.getString(URL_KEY);
        }

        public static final Creator<TrailerHolder> CREATOR = new Creator<TrailerHolder>() {
            @Override
            public TrailerHolder createFromParcel(Parcel in) {
                return new TrailerHolder(in);
            }

            @Override
            public TrailerHolder[] newArray(int size) {
                return new TrailerHolder[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Bundle bundle = new Bundle();
            bundle.putString(NAME_KEY, name);
            bundle.putString(URL_KEY, url);

            dest.writeBundle(bundle);
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("url", url);

            return json;
        }

        public static TrailerHolder fromJson(JSONObject json) throws JSONException {
            TrailerHolder holder = new TrailerHolder();
            holder.name = json.getString("name");
            holder.url = json.getString("url");

            return holder;
        }
    }

    public static class ReviewHolder extends MetadataHolder {

        private static final String AUTHOR_KEY = "author";
        private static final String CONTENT_KEY = "content";

        String author;
        String content;

        public ReviewHolder() {
        }

        public ReviewHolder(Parcel in) {
            Bundle bundle = in.readBundle();
            author = bundle.getString(AUTHOR_KEY);
            content = bundle.getString(CONTENT_KEY);
        }

        public static final Creator<ReviewHolder> CREATOR = new Creator<ReviewHolder>() {
            @Override
            public ReviewHolder createFromParcel(Parcel in) {
                return new ReviewHolder(in);
            }

            @Override
            public ReviewHolder[] newArray(int size) {
                return new ReviewHolder[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Bundle bundle = new Bundle();
            bundle.putString(AUTHOR_KEY, author);
            bundle.putString(CONTENT_KEY, content);

            dest.writeBundle(bundle);
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("author", author);
            json.put("content", content);

            return json;
        }

        public static ReviewHolder fromJson(JSONObject json) throws JSONException {
            ReviewHolder holder = new ReviewHolder();
            holder.author = json.getString("author");
            holder.content = json.getString("content");

            return holder;
        }
    }
}
