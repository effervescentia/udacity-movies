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

import com.tkstr.movies.PosterAdapter.MovieHolder;

import java.util.ArrayList;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.tkstr.movies.DetailFragment.ID_KEY;
import static com.tkstr.movies.DetailFragment.TITLE_KEY;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

/**
 * @author Ben Teichman
 */
public class DiscoveryFragment extends Fragment {

    public static final String SORT_POPULARITY = "popularity.desc";
    public static final String SORT_RATING = "vote_count.desc";
    public static final String SORT_FAVORITES = "favorites";
    public static final String MOVIES_KEY = "movies";
    public static final String FAVORITES_KEY = "favorites";
    public static final String SORT_KEY = "sort";

    private PosterAdapter adapter;
    private ArrayList<MovieHolder> movies = new ArrayList<>();
    private String[] favorites = new String[]{};
    private String sort = SORT_POPULARITY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            favorites = savedInstanceState.getStringArray(FAVORITES_KEY);
            sort = savedInstanceState.getString(SORT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discovery, container, false);

        GridView posterGrid = (GridView) v;
        adapter = new PosterAdapter(getContext(), movies);
        if (emptyIfNull(movies).isEmpty()) {
            reload();
        }
        posterGrid.setAdapter(adapter);
        posterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieHolder movie = adapter.getItem(position);
                String movieId = movie.id;
                String title = movie.title;

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(ID_KEY, movieId);
                intent.putExtra(TITLE_KEY, title);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_KEY, movies);
        outState.putStringArray(FAVORITES_KEY, favorites);
        outState.putString(SORT_KEY, sort);
    }

    public DiscoveryFragment setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public void reload() {
        if (hasNetworkAccess()) {
            if (SORT_FAVORITES.equals(sort)) {
                Log.d(getClass().getSimpleName(), "restoring movie list from favorites");
                new FavoriteUpdateTask(getContext(), adapter).execute(favorites);
            } else {
                Log.d(getClass().getSimpleName(), "reloading movie list with sort: " + sort);
                new TopUpdateTask(getContext(), adapter).execute(sort);
            }
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
