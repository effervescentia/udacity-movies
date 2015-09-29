package com.tkstr.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * @author Ben Teichman
 */
public class DiscoveryFragment extends Fragment {

    public static final String SORT_POPULARITY = "popularity.desc";
    public static final String SORT_RATING = "vote_count.desc";
    private PosterAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discovery, container, false);

        GridView posterGrid = (GridView) v;
        adapter = new PosterAdapter(getContext(), new ArrayList<PosterAdapter.MovieHolder>());
        reload(SORT_POPULARITY);
        posterGrid.setAdapter(adapter);
        posterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieId = adapter.getItem(position).id;

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", movieId);
                startActivity(intent);
            }
        });

        return v;
    }

    public void reload(String sort) {
        new MovieUpdateTask(getContext(), adapter).execute(sort);
    }

}
