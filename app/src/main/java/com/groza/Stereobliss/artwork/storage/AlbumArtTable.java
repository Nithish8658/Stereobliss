/* */

package com.groza.Stereobliss.artwork.storage;

import android.database.sqlite.SQLiteDatabase;

class AlbumArtTable {
    static final String TABLE_NAME = "odyssey_album_artwork_items";

    static final String COLUMN_ALBUM_NAME = "album_name";

    static final String COLUMN_ARTIST_NAME = "artist_name";

    static final String COLUMN_ALBUM_MBID = "album_mbid";

    static final String COLUMN_ALBUM_ID = "album_id";

    static final String COLUMN_IMAGE_FILE_PATH = "album_image_file_path";

    static final String COLUMN_IMAGE_NOT_FOUND = "image_not_found";

    static final String COLUMN_IMAGE_HAS_FULL_PATH = "image_has_full_path";

    private static final String DATABASE_CREATE = "CREATE TABLE if not exists " +
            TABLE_NAME +
            " (" +
            COLUMN_ALBUM_NAME + " text," +
            COLUMN_ARTIST_NAME + " text," +
            COLUMN_ALBUM_MBID + " text," +
            COLUMN_ALBUM_ID + " text primary key," +
            COLUMN_IMAGE_NOT_FOUND + " integer," +
            COLUMN_IMAGE_FILE_PATH + " text," +
            COLUMN_IMAGE_HAS_FULL_PATH + " integer" +
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
