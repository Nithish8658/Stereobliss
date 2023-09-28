/* */

package com.groza.Stereobliss.playbackservice.storage;

import android.database.sqlite.SQLiteDatabase;

public class PlaylistsTracksTable {

    public static final String TABLE_NAME = "odyssey_playlist_tracks";

    /**
     * The name of the column that holds a unique id for each track.
     */
    public static final String COLUMN_ID = "_id";

    /**
     * Name of the column that holds the number of the track in the related album
     */
    public static final String COLUMN_TRACK_NUMBER = "track_number";

    /**
     * Name of the column that holds the title of the track
     */
    public static final String COLUMN_TRACK_TITLE = "title";

    /**
     * Name of the column that holds the album name of the track
     */
    public static final String COLUMN_TRACK_ALBUM = "album";

    /**
     * Name of the column that holds the album id of the track
     */
    public static final String COLUMN_TRACK_ALBUM_ID = "album_id";

    /**
     * Name of the column that holds the duration of the track
     */
    public static final String COLUMN_TRACK_DURATION = "duration";

    /**
     * Name of the column that holds the artist name of the track
     */
    public static final String COLUMN_TRACK_ARTIST = "artist";

    /**
     * Name of the column that holds the artist id of the track
     */
    public static final String COLUMN_TRACK_ARTIST_ID = "artist_id";

    /**
     * Name of the column that holds the url of the track
     */
    public static final String COLUMN_TRACK_URL = "url";

    /**
     * Name of the column that holds the id of the track from mediastore
     */
    public static final String COLUMN_TRACK_ID = "track_id";

    /**
     * Name of the column that holds the id related to the playlist
     */
    public static final String COLUMN_PLAYLIST_ID = "playlist_id";

    public static final String COLUMN_PLAYLIST_POSITION = "playlist_position";

    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "(" +
            COLUMN_ID + " integer primary key autoincrement," +
            COLUMN_TRACK_NUMBER + " integer," +
            COLUMN_TRACK_TITLE + " text," +
            COLUMN_TRACK_ALBUM + " text," +
            COLUMN_TRACK_ALBUM_ID + " integer," +
            COLUMN_TRACK_DURATION + " integer," +
            COLUMN_TRACK_ARTIST + " text," +
            COLUMN_TRACK_ARTIST_ID + " integer," +
            COLUMN_TRACK_URL + " text," +
            COLUMN_TRACK_ID + " integer," +
            COLUMN_PLAYLIST_ID + " integer," +
            COLUMN_PLAYLIST_POSITION + " integer" +
            ");";

    private static final String DATABASE_DROP = "DROP TABLE if exists " + TABLE_NAME;

    public static void createTable(final SQLiteDatabase database) {
        // create new table
        database.execSQL(DATABASE_CREATE);
    }

    public static void dropTable(final SQLiteDatabase database) {
        // drop table if already existing
        database.execSQL(DATABASE_DROP);
    }
}
