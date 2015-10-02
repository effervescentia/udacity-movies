package com.tkstr.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Ben Teichman
 */
public class FavoritePrefs {

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
        Log.d(getClass().getSimpleName(), "favorites: " + favorites);
        return favorites;
    }

    public boolean isFavorite(String id) {
        Set<String> favorites = getFavorites();
        return favorites.contains(id);
    }

    public void setFavorite(String id, boolean favorite) {
        Set<String> current = getFavorites();
        if (favorite) {
            current.add(id);
        } else {
            current.remove(id);
        }
        SharedPreferences.Editor editor = preferences().edit();
        editor.putStringSet(FAVORITES_KEY, current);
        boolean commited = editor.commit();
        Log.d(getClass().getSimpleName(), "favorite commited: " + commited);
    }
}
