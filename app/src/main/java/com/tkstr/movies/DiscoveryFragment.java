package com.tkstr.movies;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.widget.Toast.LENGTH_SHORT;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

/**
 * @author Ben Teichman
 */
public class DiscoveryFragment extends Fragment {

    public static final String SORT_POPULARITY = "popularity.desc";
    public static final String SORT_RATING = "vote_count.desc";
    public static final String MOVIE_KEY = "movies";
    private PosterAdapter adapter;
    private ArrayList<PosterAdapter.MovieHolder> movies = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(MOVIE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discovery, container, false);

        GridView posterGrid = (GridView) v;
        adapter = new PosterAdapter(getContext(), movies);
        if (emptyIfNull(movies).isEmpty()) {
            reload(SORT_POPULARITY);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_KEY, movies);
    }

    public void reload(String sort) {
        if (hasNetworkAccess()) {
            Log.d(getClass().getSimpleName(), "reloading movie list");
            new MovieUpdateTask(getContext(), adapter).execute(sort);
        } else {
            Toast.makeText(getContext(), "Unable to connect to network", LENGTH_SHORT).show();
        }
    }

    private boolean hasNetworkAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
