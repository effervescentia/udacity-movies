package com.tkstr.movies;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String THUMBNAIL_URL = "http://image.tmdb.org/t/p/w154";
    public static final String ID_KEY = "id";
    public static final String DETAILS_KEY = "id";
    private DetailHolder details;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            details = savedInstanceState.getParcelable(DETAILS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        ListView trailerList = (ListView) v.findViewById(R.id.trailer_list);

        trailerList.addHeaderView(inflater.inflate(R.layout.header_detail, null));

        trailerList.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, new ArrayList<>(Collections.<String>emptyList())));

        if (details == null) {
            String id = getActivity().getIntent().getStringExtra(ID_KEY);
            Log.d(getClass().getSimpleName(), "loading details for movie with id: " + id);
            new DetailsTask(this, v).execute(id);
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

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(details.description);

        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail_poster);
        Glide.with(getContext())
                .load(THUMBNAIL_URL + details.image)
                .error(android.R.drawable.ic_dialog_alert)
                .into(thumbnail);

        Button button = (Button) view.findViewById(R.id.favourite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Yeah... I'll try to remember that one", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DETAILS_KEY, details);
    }

    public DetailHolder getDetails() {
        return details;
    }

    public DetailFragment setDetails(DetailHolder details) {
        this.details = details;
        return this;
    }

    public static class DetailHolder implements Parcelable {
        private static final String TITLE_KEY = "title";
        private static final String IMAGE_KEY = "image";
        private static final String YEAR_KEY = "year";
        private static final String RUNTIME_KEY = "runtime";
        private static final String RATING_KEY = "rating";
        private static final String DESCRIPTION_KEY = "description";

        String title;
        String image;
        String year;
        String runtime;
        String rating;
        String description;

        public DetailHolder() {
        }

        protected DetailHolder(Parcel in) {
            @SuppressWarnings("unchecked") Map<String, String> map = in.readHashMap(ClassLoader.getSystemClassLoader());
            title = map.get(TITLE_KEY);
            image = map.get(IMAGE_KEY);
            year = map.get(YEAR_KEY);
            runtime = map.get(RUNTIME_KEY);
            rating = map.get(RATING_KEY);
            description = map.get(DESCRIPTION_KEY);
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
            Map<String, String> map = new HashMap<>();
            map.put(TITLE_KEY, title);
            map.put(IMAGE_KEY, image);
            map.put(YEAR_KEY, year);
            map.put(RUNTIME_KEY, runtime);
            map.put(RATING_KEY, rating);
            map.put(DESCRIPTION_KEY, description);

            dest.writeMap(map);
        }
    }
}
