package com.tkstr.movies.app;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tkstr.movies.app.data.MovieContract.MovieEntry;

/**
 * @author Ben Teichman
 */
public class PosterAdapter extends CursorAdapter {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/w300";

    public PosterAdapter(Context context, Cursor c) {
        super(context, c, -1);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView image = new ImageView(context);

        bindImage(image, context, cursor);

        return image;
    }

    public static MovieHolder parseMovie(Cursor cursor) {
        MovieHolder holder = new MovieHolder();

        int idIndex = cursor.getColumnIndex(MovieEntry.COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
        int imageIndex = cursor.getColumnIndex(MovieEntry.COLUMN_IMAGE);

        holder.id = cursor.getLong(idIndex);
        holder.title = cursor.getString(titleIndex);
        holder.image = cursor.getString(imageIndex);

        return holder;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        bindImage((ImageView) view, context, cursor);
    }

    private void bindImage(ImageView image, Context context, Cursor cursor) {
        MovieHolder holder = parseMovie(cursor);

        Glide.with(context)
                .load(BASE_URL + holder.image)
                .error(android.R.drawable.ic_dialog_alert)
                .into(image);
    }

    public static class MovieHolder implements Parcelable {
        private static final String ID_KEY = "id";
        private static final String IMAGE_KEY = "image";
        private static final String TITLE_KEY = "title";

        long id;
        String image;
        String title;

        public MovieHolder() {
        }

        protected MovieHolder(Parcel in) {
            Bundle bundle = in.readBundle();

            id = bundle.getLong(ID_KEY);
            title = bundle.getString(TITLE_KEY);
            image = bundle.getString(IMAGE_KEY);
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
            Bundle bundle = new Bundle();
            bundle.putLong(ID_KEY, id);
            bundle.putString(TITLE_KEY, title);
            bundle.putString(IMAGE_KEY, image);

            dest.writeBundle(bundle);
        }
    }
}
