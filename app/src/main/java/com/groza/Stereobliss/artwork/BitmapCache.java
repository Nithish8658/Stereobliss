/* */

package com.groza.Stereobliss.artwork;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import com.groza.Stereobliss.models.AlbumModel;
import com.groza.Stereobliss.models.ArtistModel;

import java.util.Map;

/**
 * Simple LRU-based caching for album & artist images. This could reduce CPU usage
 * for the cost of memory usage by caching decoded {@link Bitmap} objects in a {@link LruCache}.
 */
public class BitmapCache {
    private static final String TAG = BitmapCache.class.getSimpleName();

    private static final int mMaxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    /**
     * Maximum size of the cache in kilobytes
     */
    private static final int mCacheSize = mMaxMemory / 4;

    /**
     * Hash prefix for album images
     */
    private static final String ALBUM_PREFIX = "A_";

    /**
     * Hash prefix for artist images
     */
    private static final String ARTIST_PREFIX = "B_";

    /**
     * Private cache instance
     */
    private final LruCache<String, Bitmap> mCache;

    /**
     * Singleton instance
     */
    private static BitmapCache mInstance;

    private BitmapCache() {
        mCache = new LruCache<String, Bitmap>(mCacheSize) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }

        };
    }

    public static synchronized BitmapCache getInstance() {
        if (mInstance == null) {
            mInstance = new BitmapCache();
        }
        return mInstance;
    }

    /**
     * Tries to get an album image from the cache
     *
     * @param album Album object to try
     * @return Bitmap if cache hit, null otherwise
     */
    public synchronized Bitmap requestAlbumBitmap(AlbumModel album) {
        return mCache.get(getAlbumHash(album));
    }

    /**
     * Puts an album image to the cache
     *
     * @param album Album object to use for cache key
     * @param bm    Bitmap to store in cache
     */
    public synchronized void putAlbumBitmap(AlbumModel album, Bitmap bm) {
        if (bm != null) {
            mCache.put(getAlbumHash(album), bm);
        }
    }

    /**
     * Removes an album image from the cache
     *
     * @param album Album object to use for cache key
     */
    public synchronized void removeAlbumBitmap(AlbumModel album) {
        mCache.remove(getAlbumHash(album));
    }

    /**
     * Private hash method for cache key
     *
     * @param album Album to calculate the key from
     * @return Hash string for cache key
     */
    private String getAlbumHash(AlbumModel album) {
        String hashString = ALBUM_PREFIX;
        final long albumId = album.getAlbumId();

        // Use albumId as key if available
        if (albumId != -1) {
            hashString += String.valueOf(albumId);
            return hashString;
        }

        // Else use artist and album name
        final String albumArtist = album.getArtistName();
        final String albumName = album.getAlbumName();

        hashString += albumArtist + '_' + albumName;
        return hashString;
    }

    /*
     * Begin of artist image handling
     */

    /**
     * Tries to get an artist image from the cache
     *
     * @param artist Artist object to check in cache
     * @return Bitmap if cache hit, null otherwise
     */
    public synchronized Bitmap requestArtistImage(ArtistModel artist) {
        return mCache.get(getArtistHash(artist));
    }

    /**
     * Puts an artist image to the cache
     *
     * @param artist Artist object used as cache key
     * @param bm     Bitmap to store in cache
     */
    public synchronized void putArtistImage(ArtistModel artist, Bitmap bm) {
        if (bm != null) {
            mCache.put(getArtistHash(artist), bm);
        }
    }

    /**
     * Removes an artist image from the cache
     *
     * @param artist Artist object used as cache key
     */
    public synchronized void removeArtistImage(ArtistModel artist) {
        mCache.remove(getArtistHash(artist));
    }

    /**
     * Private hash method for cache key
     *
     * @param artist Artist used as cache key
     * @return Hash string for cache key
     */
    private String getArtistHash(ArtistModel artist) {
        String hashString = ARTIST_PREFIX;

        final long artistId = artist.getArtistID();

        // Use albumId as key if available
        if (artistId != -1) {
            hashString += String.valueOf(artistId);
            return hashString;
        }

        hashString += artist.getArtistName();

        return hashString;
    }

    /**
     * Debug method to provide performance evaluation metrics
     */
    private void printUsage() {
        Log.v(TAG, "Cache usage: " + ((mCache.size() * 100) / mCache.maxSize()) + '%');
        int missCount = mCache.missCount();
        int hitCount = mCache.hitCount();
        if (missCount > 0) {
            Log.v(TAG, "Cache hit count: " + hitCount + " miss count: " + missCount + " Miss rate: " + ((hitCount * 100) / missCount) + '%');
        }
        Log.v(TAG, "Memory usage: " + (getMemoryUsage() / (1024 * 1024)) + " MB");
    }

    private long getMemoryUsage() {
        Map<String, Bitmap> snapShot = mCache.snapshot();
        long bytes = 0;
        for (Bitmap bitmap : snapShot.values()) {
            bytes += bitmap.getAllocationByteCount();
        }

        return bytes;
    }
}
