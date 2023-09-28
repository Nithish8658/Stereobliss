

package com.groza.Stereobliss.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Pair;

import com.groza.Stereobliss.adapter.ScrollSpeedAdapter;
import com.groza.Stereobliss.artwork.ArtworkManager;
import com.groza.Stereobliss.artwork.storage.ImageNotFoundException;
import com.groza.Stereobliss.models.AlbumModel;
import com.groza.Stereobliss.models.ArtistModel;
import com.groza.Stereobliss.models.GenericModel;
import com.groza.Stereobliss.models.Trackmodel.TrackModel;
import com.groza.Stereobliss.viewitems.CoverLoadable;

/**
 * Loader class for covers
 */
public class AsyncLoader extends AsyncTask<AsyncLoader.CoverViewHolder, Void, Bitmap> {
    private static final String TAG = AsyncLoader.class.getSimpleName();

    private CoverViewHolder mCover;

    private long mStartTime;

    /**
     * Wrapper class for covers
     */
    public static class CoverViewHolder {
        public Pair<Integer, Integer> imageDimension;
        public CoverLoadable coverLoadable;
        public ArtworkManager artworkManager;
        public GenericModel modelItem;
        public ScrollSpeedAdapter mAdapter;
    }

    @Override
    protected Bitmap doInBackground(CoverViewHolder... params) {
        // Save the time when loading started for later duration calculation
        mStartTime = System.currentTimeMillis();

        mCover = params[0];
        Bitmap image = null;
        // Check if model item is artist or album
        if (mCover.modelItem instanceof ArtistModel) {
            ArtistModel artist = (ArtistModel) mCover.modelItem;

            try {
                // Check if image is available. If it is not yet fetched it will throw an exception
                // If it was already searched for and not found, this will be null.
                image = mCover.artworkManager.getImage(artist, mCover.imageDimension.first, mCover.imageDimension.second, false);
            } catch (ImageNotFoundException e) {
                // Check if fetching for this item is already ongoing
                if (!artist.getFetching()) {
                    // If not set it as ongoing and request the image fetch.
                    mCover.artworkManager.fetchImage(artist);
                    artist.setFetching(true);
                }
            }
        } else if (mCover.modelItem instanceof AlbumModel) {
            AlbumModel album = (AlbumModel) mCover.modelItem;

            try {
                // Check if image is available. If it is not yet fetched it will throw an exception.
                // If it was already searched for and not found, this will be null.
                image = mCover.artworkManager.getImage(album, mCover.imageDimension.first, mCover.imageDimension.second, false);
            } catch (ImageNotFoundException e) {
                // Check if fetching for this item is already ongoing
                if (!album.getFetching()) {
                    // If not set it as ongoing and request the image fetch.
                    mCover.artworkManager.fetchImage(album);
                    album.setFetching(true);
                }
            }
        } else if (mCover.modelItem instanceof TrackModel) {
            TrackModel track = (TrackModel) mCover.modelItem;

            try {
                // Check if image is available. If it is not yet fetched it will throw an exception.
                // If it was already searched for and not found, this will be null.
                image = mCover.artworkManager.getImage(track, mCover.imageDimension.first, mCover.imageDimension.second, false);
            } catch (ImageNotFoundException e) {
                // If not set it as ongoing and request the image fetch.
                mCover.artworkManager.fetchImage(track);
            }
        }
        return image;
    }


    @Override
    protected void onPostExecute(Bitmap result) {

        super.onPostExecute(result);

        // set mCover if exists
        if (result != null) {
            if (mCover.mAdapter != null) {
                mCover.mAdapter.addImageLoadTime(System.currentTimeMillis() - mStartTime);
            }
            mCover.coverLoadable.setImage(result);
        }
    }
}