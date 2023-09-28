/* */

package com.groza.Stereobliss.artwork.storage;

import android.database.sqlite.SQLiteDatabase;

class ArtistArtTable {
    static final String TABLE_NAME = "odyssey_artist_artwork_items";

    static final String COLUMN_ARTIST_NAME = "artist_name";

    static final String COLUMN_ARTIST_MBID = "artist_mbid";

    static final String COLUMN_ARTIST_ID = "artist_id";

    static final String COLUMN_IMAGE_FILE_PATH = "artist_image_file_path";

    static final String COLUMN_IMAGE_NOT_FOUND = "image_not_found";

    private static final String DATABASE_CREATE = "CREATE TABLE if not exists " +
            TABLE_NAME
            + " (" +
            COLUMN_ARTIST_NAME + " text," +
            COLUMN_ARTIST_MBID + " text," +
            COLUMN_ARTIST_ID + " text primary key," +
            COLUMN_IMAGE_NOT_FOUND + " integer," +
            COLUMN_IMAGE_FILE_PATH + " text" +
            ");";

    private static final String DATABASE_DROP = "DROP TABLE if exists " + TABLE_NAME;

    static void createTable(SQLiteDatabase database) {
        // Create table if not already existing
        database.execSQL(DATABASE_CREATE);
    }

    static void dropTable(final SQLiteDatabase database) {
        // drop table if already existing
        database.execSQL(DATABASE_DROP);
    }
}
