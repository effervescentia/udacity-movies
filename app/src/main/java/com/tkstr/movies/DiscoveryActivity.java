package com.tkstr.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;
import static com.tkstr.movies.DiscoveryFragment.SORT_FAVORITES;
import static com.tkstr.movies.DiscoveryFragment.SORT_POPULARITY;
import static com.tkstr.movies.DiscoveryFragment.SORT_RATING;

public class DiscoveryActivity extends AppCompatActivity {

    private DiscoveryFragment fragment;
    private static final String FRAGMENT_TAG = "frg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        if (savedInstanceState == null) {
            fragment = new DiscoveryFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.discovery_fragment, fragment, FRAGMENT_TAG)
                    .commit();
        } else {
            fragment = (DiscoveryFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discovery, menu);
        return true;
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

        switch (fragment.getSort()) {
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
                fragment.clearFavorites();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sort(String sort, int messageId) {
        Toast.makeText(getApplicationContext(), getResources().getString(messageId), LENGTH_SHORT).show();
        fragment.setSort(sort).reload();
    }
}
