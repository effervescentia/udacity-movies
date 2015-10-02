package com.tkstr.movies;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ben Teichman
 */
public class PosterAdapter extends ArrayAdapter<PosterAdapter.MovieHolder> {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/w185";

    public PosterAdapter(Context context, List<MovieHolder> imageUrls) {
        super(context, -1, imageUrls);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        if (convertView == null) {
            image = new ImageView(getContext());
        } else {
            image = (ImageView) convertView;
        }

        MovieHolder holder = getItem(position);

        Glide.with(getContext())
                .load(BASE_URL + holder.image)
                .error(android.R.drawable.ic_dialog_alert)
                .into(image);

        return image;
    }

    public static class MovieHolder implements Parcelable {
        private static final String ID_KEY = "id";
        private static final String IMAGE_KEY = "image";
        private static final String TITLE_KEY = "title";

        String id;
        String image;
        String title;

        public MovieHolder() {
        }

        protected MovieHolder(Parcel in) {
            @SuppressWarnings("unchecked") HashMap<String, String> map = in.readHashMap(ClassLoader.getSystemClassLoader());
            id = map.get(ID_KEY);
            image = map.get(IMAGE_KEY);
            title = map.get(TITLE_KEY);
        }

        public static final Creator<MovieHolder> CREATOR = new Creator<MovieHolder>() {
            @Override
            public MovieHolder createFromParcel(Parcel in) {
                return new MovieHolder(in);
            }

            @Override
            public MovieHolder[] newArray(int size) {
                return new MovieHolder[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Map<String, String> map = new HashMap<>();
            map.put(ID_KEY, id);
            map.put(IMAGE_KEY, image);
            map.put(TITLE_KEY, title);
            dest.writeMap(map);
        }
    }
}
