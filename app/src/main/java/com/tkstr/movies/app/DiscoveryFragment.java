package com.tkstr.movies.app;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.tkstr.movies.R;

import java.util.ArrayList;
import java.util.Set;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.widget.Toast.LENGTH_SHORT;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

/**
 * @author Ben Teichman
 */
public class DiscoveryFragment extends Fragment {

    public static final String SORT_POPULARITY = "popularity.desc";
    public static final String SORT_RATING = "vote_count.desc";
    public static final String SORT_FAVORITES = "favorites";
    public static final String MOVIES_KEY = "movies";
    public static final String SORT_KEY = "sort";

    private PosterAdapter adapter;
    private GridView grid;
    private ArrayList<PosterAdapter.MovieHolder> movies = new ArrayList<>();
    private String sort = SORT_POPULARITY;
    private FavoritePrefs favorites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            sort = savedInstanceState.getString(SORT_KEY);
            movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
        }
        favorites = new FavoritePrefs(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_discovery, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        grid = (GridView) inflater.inflate(R.layout.fragment_discovery, container, false);

        adapter = new PosterAdapter(getContext(), movies);
        if (emptyIfNull(movies).isEmpty()) {
            reload();
        }
        grid.setAdapter(adapter);

        return grid;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_KEY, movies);
        outState.putString(SORT_KEY, sort);
    }

    public DiscoveryFragment setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public PosterAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(PosterAdapter adapter) {
        this.adapter = adapter;
    }

    public GridView getGrid() {
        return grid;
    }

    public void setGrid(GridView grid) {
        this.grid = grid;
    }

    public void reload() {
        if (hasNetworkAccess()) {
            if (SORT_FAVORITES.equals(sort)) {
                Log.d(getClass().getSimpleName(), "restoring movie list from favorites");
                Set<String> movieIds = this.favorites.getFavorites();
                new FavoriteUpdateTask(getContext(), adapter).execute(movieIds.toArray(new String[movieIds.size()]));
            } else {
                Log.d(getClass().getSimpleName(), "reloading movie list with sort: " + sort);
                new TopUpdateTask(getContext(), adapter).execute(sort);
            }
        } else {
            Toast.makeText(getContext(), "Unable to connect to network", LENGTH_SHORT).show();
        }
    }

    public void clearFavorites() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.silly_forget)
                .setPositiveButton(R.string.action_forget, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favorites.resetFavorites();
                        reload();
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .create().show();
    }

    private boolean hasNetworkAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}