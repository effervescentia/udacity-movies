package com.tkstr.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;
import static com.tkstr.movies.DetailFragment.ID_KEY;
import static com.tkstr.movies.DetailFragment.TITLE_KEY;
import static com.tkstr.movies.DiscoveryFragment.SORT_FAVORITES;
import static com.tkstr.movies.DiscoveryFragment.SORT_POPULARITY;
import static com.tkstr.movies.DiscoveryFragment.SORT_RATING;

public class DiscoveryActivity extends AppCompatActivity {

    private DiscoveryFragment discovery;
    private DetailFragment detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        detail = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_frag);

        if (detail != null)
            getSupportFragmentManager().beginTransaction()
                    .hide(detail)
                    .commit();

        discovery = (DiscoveryFragment) getSupportFragmentManager().findFragmentById(R.id.discovery_frag);
        discovery.getGrid().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PosterAdapter.MovieHolder movie = discovery.getAdapter().getItem(position);
                String movieId = movie.id;
                String title = movie.title;

                if (detail == null) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra(ID_KEY, movieId);
                    intent.putExtra(TITLE_KEY, title);
                    startActivity(intent);
                } else {
                    detail.updateDetails(movieId, title);
                    if (detail.isHidden()) {
                        GridView grid = discovery.getGrid();
                        grid.setNumColumns(3);

                        getSupportFragmentManager().beginTransaction()
                                .show(detail)
                                .commit();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        detail = null;
        super.onStop();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            setSelection(menu);
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setSelection(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isCheckable()) {
                item.setChecked(false);
            }
        }

        switch (discovery.getSort()) {
            case SORT_POPULARITY:
                menu.findItem(R.id.sort_popularity).setChecked(true);
                break;
            case SORT_RATING:
                menu.findItem(R.id.sort_rating).setChecked(true);
                break;
            case SORT_FAVORITES:
                menu.findItem(R.id.sort_favorites).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_popularity:
                sort(SORT_POPULARITY, R.string.sorting_popularity);
                break;
            case R.id.sort_rating:
                sort(SORT_RATING, R.string.sorting_rating);
                break;
            case R.id.sort_favorites:
                sort(SORT_FAVORITES, R.string.sorting_favorite);
                break;
            case R.id.action_reset:
                discovery.clearFavorites();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sort(String sort, int messageId) {
        Toast.makeText(getApplicationContext(), getResources().getString(messageId), LENGTH_SHORT).show();
        discovery.setSort(sort).reload();
    }
}
