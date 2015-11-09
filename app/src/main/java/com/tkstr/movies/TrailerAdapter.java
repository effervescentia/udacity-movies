package com.tkstr.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tkstr.movies.DetailFragment.TrailerHolder;

import java.util.List;

/**
 * @author Ben Teichman
 */
public class TrailerAdapter extends ArrayAdapter<TrailerHolder> {

    public TrailerAdapter(Context context, List<TrailerHolder> trailerUrls) {
        super(context, -1, trailerUrls);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout trailerItem;
        if (convertView == null) {
            trailerItem = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
        } else {
            trailerItem = (LinearLayout) convertView;
        }

        TrailerHolder holder = getItem(position);

        TextView trailerTitle = (TextView) trailerItem.findViewById(R.id.trailer_title);
        trailerTitle.setText(holder.name);

        return trailerItem;
    }
}
