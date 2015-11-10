package com.tkstr.movies.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tkstr.movies.app.data.MovieContract.DetailEntry;

/**
 * @author Ben Teichman
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDetailsTable(db);
    }

    private void createDetailsTable(SQLiteDatabase database) {
        final String SQL_CREATE_DETAILS_TABLE = "CREATE TABLE " + DetailEntry.TABLE_NAME + " (" +
                DetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                DetailEntry.COLUMN_ID + " LONG NOT NULL," +
                DetailEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                DetailEntry.COLUMN_IMAGE + " TEXT NOT NULL," +
                DetailEntry.COLUMN_YEAR + " INTEGER NOT NULL," +
                DetailEntry.COLUMN_RUNTIME + " INTEGER NOT NULL," +
                DetailEntry.COLUMN_RATING + " DOUBLE NOT NULL," +
                DetailEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                DetailEntry.COLUMN_FAVORITE + " INTEGER NOT NULL," +

                DetailEntry.COLUMN_TRAILERS_JSON + " TEXT NOT NULL," +
                DetailEntry.COLUMN_REVIEWS_JSON + " TEXT NOT NULL," +

                " UNIQUE (" + DetailEntry.COLUMN_ID + ") ON CONFLICT REPLACE );";

        database.execSQL(SQL_CREATE_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DetailEntry.TABLE_NAME);
        onCreate(db);
    }
}
