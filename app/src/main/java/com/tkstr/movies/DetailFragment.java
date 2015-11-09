package com.tkstr.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * @author Ben Teichman
 */
public class DetailFragment extends Fragment {

    private static final String LOG_KEY = DetailFragment.class.getSimpleName();
    private static final String THUMBNAIL_URL = "http://image.tmdb.org/t/p/w154";
    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String DETAILS_KEY = "details";

    private DetailHolder details;
    private FavoritePrefs favorites;
    private ComboAdapter comboAdapter;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            details = savedInstanceState.getParcelable(DETAILS_KEY);
        }
        favorites = new FavoritePrefs(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        ListView comboList = (ListView) v.findViewById(R.id.combo_list);

        comboList.addHeaderView(inflater.inflate(R.layout.header_detail, comboList, false));

        if (details == null) {
            String id = getActivity().getIntent().getStringExtra(ID_KEY);
            String title = getActivity().getIntent().getStringExtra(TITLE_KEY);
            Log.d(LOG_KEY, "loading details for : " + title);
            new DetailTask(this, v, title).execute(id);
        } else {
            fillDetails(v);
        }

        return v;
    }

    protected void fillDetails(View view) {
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(details.title);

        TextView year = (TextView) view.findViewById(R.id.year);
        year.setText(details.year);

        TextView length = (TextView) view.findViewById(R.id.length);
        length.setText(details.runtime);

        TextView rating = (TextView) view.findViewById(R.id.rating);
        rating.setText(details.rating);

        final TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(details.description);

        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail_poster);
        Glide.with(getContext())
                .load(THUMBNAIL_URL + details.image)
                .error(android.R.drawable.ic_dialog_alert)
                .into(thumbnail);

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.favorite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details.favorite = !details.favorite;
                Toast.makeText(getContext(), details.favorite ? getString(R.string.silly_like) : getString(R.string.silly_dislike), Toast.LENGTH_SHORT).show();
                styleFavoriteButton((FloatingActionButton) v);
            }
        });
        styleFavoriteButton(button);

        ListView comboList = (ListView) view.findViewById(R.id.combo_list);
        comboAdapter = new ComboAdapter(getContext(), details.trailers, details.reviews);

        comboList.setAdapter(comboAdapter);

        comboList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position--; //to account for header?

                int itemType = comboAdapter.getItemViewType(position);
                if (itemType == ComboAdapter.TRAILER_TYPE) {
                    TrailerHolder holder = (TrailerHolder) comboAdapter.getItem(position);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://youtube.com/watch?v=" + holder.id)));
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
        favorites.setFavorite(details.id, details.favorite);
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

        String id;
        String title;
        String image;
        String year;
        String runtime;
        String rating;
        String description;
        boolean favorite;
        ArrayList<TrailerHolder> trailers;
        ArrayList<ReviewHolder> reviews;

        public DetailHolder() {
        }

        protected DetailHolder(Parcel in) {
            Bundle bundle = in.readBundle();
            id = bundle.getString(ID_KEY);
            title = bundle.getString(TITLE_KEY);
            image = bundle.getString(IMAGE_KEY);
            year = bundle.getString(YEAR_KEY);
            runtime = bundle.getString(RUNTIME_KEY);
            rating = bundle.getString(RATING_KEY);
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
            bundle.putString(ID_KEY, id);
            bundle.putString(TITLE_KEY, id);
            bundle.putString(IMAGE_KEY, image);
            bundle.putString(YEAR_KEY, year);
            bundle.putString(RUNTIME_KEY, runtime);
            bundle.putString(RATING_KEY, rating);
            bundle.putString(DESCRIPTION_KEY, description);
            bundle.putBoolean(FAVORITE_KEY, favorite);
            bundle.putParcelableArrayList(TRAILERS_KEY, trailers);
            bundle.putParcelableArrayList(REVIEWS_KEY, reviews);

            dest.writeBundle(bundle);
        }
    }

    public abstract static class MetadataHolder implements Parcelable {
    }

    public static class TrailerHolder extends MetadataHolder {

        private static final String ID_KEY = "id";
        private static final String NAME_KEY = "name";

        String id;
        String name;

        public TrailerHolder() {
        }

        public TrailerHolder(Parcel in) {
            Bundle bundle = in.readBundle();
            id = bundle.getString(ID_KEY);
            name = bundle.getString(NAME_KEY);
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
            bundle.putString(ID_KEY, id);
            bundle.putString(NAME_KEY, name);

            dest.writeBundle(bundle);
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
    }
}
