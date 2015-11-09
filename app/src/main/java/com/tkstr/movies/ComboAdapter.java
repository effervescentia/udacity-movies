package com.tkstr.movies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tkstr.movies.DetailFragment.ReviewHolder;
import com.tkstr.movies.DetailFragment.TrailerHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ben Teichman
 */
public class ComboAdapter extends ArrayAdapter<Object> {

    private static final String LOG_KEY = ComboAdapter.class.getSimpleName();

    public static final int TRAILER_TYPE = 0;
    public static final int REVIEW_TYPE = 1;
    public static final int TITLE_TYPE = 2;

    public ComboAdapter(Context context, List<TrailerHolder> trailers, List<ReviewHolder> reviews) {
        super(context, -1, combine(context, trailers, reviews));
    }

    private static ArrayList<Object> combine(Context context, List<TrailerHolder> trailers, List<ReviewHolder> reviews) {
        ArrayList<Object> combined = new ArrayList<>();
        if (trailers != null && trailers.size() > 0) {
            combined.add(context.getResources().getString(R.string.trailers));
            combined.addAll(trailers);
        }
        if (reviews != null && reviews.size() > 0) {
            combined.add(context.getResources().getString(R.string.reviews));
            combined.addAll(reviews);
        }
        return combined;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof TrailerHolder) {
            return TRAILER_TYPE;
        } else if (item instanceof ReviewHolder) {
            return REVIEW_TYPE;
        } else {
            return TITLE_TYPE;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout item;
        int itemType = getItemViewType(position);
        int layoutRes = getLayoutResource(itemType);

        if (convertView != null && convertView.getTag().equals(itemType)) {
            item = (LinearLayout) convertView;
        } else {
            item = (LinearLayout) LayoutInflater.from(getContext()).inflate(layoutRes, parent, false);
            item.setTag(itemType);
        }

        switch (itemType) {
            case TRAILER_TYPE:
                fillTrailer(item, (TrailerHolder) getItem(position));
                break;
            case REVIEW_TYPE:
                fillReview(item, (ReviewHolder) getItem(position));
                break;
            case TITLE_TYPE:
                fillTitle(item, (String) getItem(position));
                break;
            default:
                Log.e(LOG_KEY, "unable to find matching item type " + itemType);
        }

        return item;
    }

    private int getLayoutResource(int type) {
        switch (type) {
            case TRAILER_TYPE:
                return R.layout.trailer_item;
            case REVIEW_TYPE:
                return R.layout.review_item;
            case TITLE_TYPE:
                return R.layout.title_item;
            default:
                return -1;
        }
    }

    private void fillTrailer(LinearLayout item, TrailerHolder trailer) {
        TextView trailerTitle = (TextView) item.findViewById(R.id.trailer_title);
        trailerTitle.setText(trailer.name);
    }

    private void fillReview(LinearLayout item, ReviewHolder review) {
        TextView author = (TextView) item.findViewById(R.id.review_author);
        author.setText(String.format("- %s", review.author));

        TextView content = (TextView) item.findViewById(R.id.review_content);
        content.setText(review.content);
    }

    private void fillTitle(LinearLayout item, String title) {
        TextView header = (TextView) item.findViewById(R.id.section_header);
        header.setText(title);
    }
}
