package com.tkstr.movies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

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

    public static class MovieHolder {
        String id;
        String image;
    }
}
