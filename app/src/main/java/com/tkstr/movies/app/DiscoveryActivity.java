package com.tkstr.movies.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.tkstr.movies.R;
import com.tkstr.movies.app.PosterAdapter.MovieHolder;

import static android.widget.Toast.LENGTH_SHORT;
import static com.tkstr.movies.app.DetailFragment.ID_KEY;
import static com.tkstr.movies.app.DetailFragment.TITLE_KEY;

public class DiscoveryActivity extends AppCompatActivity {

    private static final String LOG_KEY = DiscoveryActivity.class.getSimpleName();

    private DiscoveryFragment discovery;
    private DetailFragment detail;
    private boolean fragmentSplitView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        if (findViewById(R.id.detail_fragment) != null) {
            fragmentSplitView = true;
        }

        detail = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);

        if (fragmentSplitView) {
            getSupportFragmentManager().beginTransaction()
                    .hide(detail)
                    .commit();
        }

        discovery = (DiscoveryFragment) getSupportFragmentManager().findFragmentById(R.id.discovery_fragment);
        discovery.getGrid().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) discovery.getAdapter().getItem(position);
                MovieHolder movie = PosterAdapter.parseMovie(cursor);
                long movieId = movie.id;
                String title = movie.title;

                if (detail == null || !fragmentSplitView) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra(ID_KEY, movieId);
                    intent.putExtra(TITLE_KEY, title);
                    startActivity(intent);
                } else {
                    if (detail.isHidden()) {
                        GridView grid = discovery.getGrid();
                        grid.setNumColumns(3);

                        getSupportFragmentManager().beginTransaction()
                                .show(detail)
                                .commit();
                    }
                    detail.updateDetails(movieId, title);
                }
            }
        });
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
            case DiscoveryFragment.SORT_POPULARITY:
                menu.findItem(R.id.sort_popularity).setChecked(true);
                break;
            case DiscoveryFragment.SORT_RATING:
                menu.findItem(R.id.sort_rating).setChecked(true);
                break;
            case DiscoveryFragment.SORT_FAVORITES:
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
                sort(DiscoveryFragment.SORT_POPULARITY, R.string.sorting_popularity);
                break;
            case R.id.sort_rating:
                sort(DiscoveryFragment.SORT_RATING, R.string.sorting_rating);
                break;
            case R.id.sort_favorites:
                sort(DiscoveryFragment.SORT_FAVORITES, R.string.sorting_favorite);
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
