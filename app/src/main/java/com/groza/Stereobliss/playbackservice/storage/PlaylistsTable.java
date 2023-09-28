/* */

package com.groza.Stereobliss.playbackservice.storage;

import android.database.sqlite.SQLiteDatabase;

public class PlaylistsTable {

    /**
     * The name of the table.
     */
    public static final String TABLE_NAME = "odyssey_playlists";

    /**
     * The name of the column that holds a unique id for each playlist.
     */
    public static final String COLUMN_ID = "_id";

    /**
     * The name of the column that holds the title of the playlist.
     */
    public static final String COLUMN_TITLE = "title";

    /**
     * The name of the column that holds the number tracks of the playlist.
     */
    public static final String COLUMN_TRACKS = "tracks";

    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "(" +
            COLUMN_ID + " integer primary key," +
            COLUMN_TITLE + " text," +
            COLUMN_TRACKS + " integer" +
            ");";

    private static final String DATABASE_DROP = "DROP TABLE if exists " + TABLE_NAME;

    static void createTable(final SQLiteDatabase database) {
        // Create table if not already existing
        database.execSQL(DATABASE_CREATE);
    }

    static void dropTable(final SQLiteDatabase database) {
        // drop table if already existing
        database.execSQL(DATABASE_DROP);
    }
}
