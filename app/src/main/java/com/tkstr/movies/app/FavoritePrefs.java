package com.tkstr.movies.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tkstr.movies.R;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Ben Teichman
 */
public class FavoritePrefs {

    private static final String LOG_TAG = FavoritePrefs.class.getSimpleName();

    public static final String FAVORITES_KEY = "favorites";

    private Context context;

    public FavoritePrefs(Context context) {
        this.context = context;
    }

    public SharedPreferences preferences() {
        return context.getSharedPreferences(context.getString(R.string.pref_favorites), MODE_PRIVATE);
    }

    public Set<String> getFavorites() {
        Set<String> favorites = preferences().getStringSet(FAVORITES_KEY, new HashSet<String>());
        Log.d(LOG_TAG, "favorites: " + favorites);
        return favorites;
    }

    public boolean isFavorite(String id) {
        Set<String> favorites = getFavorites();
        return favorites.contains(id);
    }

    public void setFavorite(String id, boolean favorite) {
        Set<String> current = new HashSet<>(getFavorites());
        if (favorite) {
            if (id != null) {
                current.add(id);
            } else {
                Log.w(LOG_TAG, "favorite thrown away for null id");
            }
        } else {
            current.remove(id);
        }
        SharedPreferences.Editor editor = preferences().edit();
        editor.putStringSet(FAVORITES_KEY, current);
        editor.apply();
        Log.d(LOG_TAG, "set favorites: " + current);
    }

    public void resetFavorites() {
        SharedPreferences.Editor editor = preferences().edit();
        editor.putStringSet(FAVORITES_KEY, new HashSet<String>());
        editor.apply();
        Log.d(LOG_TAG, "favorites reset");
    }
}
