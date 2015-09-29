package com.tkstr.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sort_popularity) {
            Toast.makeText(getApplicationContext(), "Sorting movies by popularity", LENGTH_SHORT).show();
            fragment.reload(SORT_POPULARITY);
        } else if (id == R.id.sort_rating) {
            Toast.makeText(getApplicationContext(), "Sorting movies by rating", LENGTH_SHORT).show();
            fragment.reload(SORT_RATING);
        }
        return super.onOptionsItemSelected(item);
    }
}
