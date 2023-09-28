

package com.groza.Stereobliss.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.groza.Stereobliss.artwork.ArtworkManager;
import com.groza.Stereobliss.artwork.BitmapCache;
import com.groza.Stereobliss.artwork.storage.ImageNotFoundException;
import com.groza.Stereobliss.models.AlbumModel;
import com.groza.Stereobliss.models.ArtistModel;
import com.groza.Stereobliss.models.Trackmodel.TrackModel;

public class CoverBitmapLoader {

    private final CoverBitmapReceiver mListener;

    private final Context mApplicationContext;

    public CoverBitmapLoader(Context context, CoverBitmapReceiver listener) {
        mApplicationContext = context.getApplicationContext();
        mListener = listener;
    }

    /**
     * Load the image for the given track from the mediastore.
     */
    public void getImage(final TrackModel track, final int width, final int height) {
        if (track != null && track.getTrackAlbumId() != -1) {
            // start the loader thread to load the image async
            final Thread loaderThread = new Thread(new TrackAlbumImageRunner(track, width, height));
            loaderThread.start();
        }
    }

    public void getArtistImage(final ArtistModel artist, final int width, final int height) {
        if (artist == null) {
            return;
        }

        // start the loader thread to load the image async
        final Thread loaderThread = new Thread(new ArtistImageRunner(artist, width, height));
        loaderThread.start();
    }

    public void getAlbumImage(final AlbumModel album, final int width, final int height) {
        if (album == null) {
            return;
        }

        // start the loader thread to load the image async
        final Thread loaderThread = new Thread(new AlbumImageRunner(album, width, height));
        loaderThread.start();
    }

    public void getArtistImage(final TrackModel track, final int width, final int height) {
        if (track == null) {
            return;
        }

        // start the loader thread to load the image async
        final Thread loaderThread = new Thread(new TrackArtistImageRunner(track, width, height));
        loaderThread.start();
    }

    private class TrackAlbumImageRunner implements Runnable {

        private final int mWidth;

        private final int mHeight;

        private final TrackModel mTrack;

        private TrackAlbumImageRunner(final TrackModel track, final int width, final int height) {
            mTrack = track;
            mWidth = width;
            mHeight = height;
        }

        /**
         * Load the image for the given track from the mediastore.
         */
        @Override
        public void run() {
            // At first get image independent of resolution (can be replaced later with higher resolution)
            final AlbumModel album = MusicLibraryHelper.createAlbumModelFromId(mTrack.getTrackAlbumId(), mApplicationContext);
            if (album == null) {
                // No album found for track, abort
                return;
            }

            Bitmap image = BitmapCache.getInstance().requestAlbumBitmap(album);
            if (image != null) {
                mListener.receiveAlbumBitmap(image);
            }

            try {
                // If image was to small get it in the right resolution
                if (image == null || !(mWidth <= image.getWidth() && mHeight <= image.getHeight())) {
                    image = ArtworkManager.getInstance(mApplicationContext).getImage(album, mWidth, mHeight, true);
                    mListener.receiveAlbumBitmap(image);
                    // Replace image with higher resolution one
                    BitmapCache.getInstance().putAlbumBitmap(album, image);
                }
            } catch (ImageNotFoundException e) {
                // Try to fetch the image here
                ArtworkManager.getInstance(mApplicationContext).fetchImage(mTrack);
            }
        }
    }

    private class ArtistImageRunner implements Runnable {

        private final int mWidth;

        private final int mHeight;

        private final ArtistModel mArtist;

        private ArtistImageRunner(final ArtistModel artist, final int width, final int height) {
            mArtist = artist;
            mWidth = width;
            mHeight = height;
        }

        /**
         * Load the image for the given artist from the mediastore.
         */
        @Override
        public void run() {
            // At first get image independent of resolution (can be replaced later with higher resolution)
            Bitmap image = BitmapCache.getInstance().requestArtistImage(mArtist);
            mListener.receiveArtistBitmap(image);

            // If image was to small get it in the right resolution
            if (image == null || !(mWidth <= image.getWidth() && mHeight <= image.getHeight())) {
                try {
                    image = ArtworkManager.getInstance(mApplicationContext).getImage(mArtist, mWidth, mHeight, true);
                    mListener.receiveArtistBitmap(image);
                    // Replace image with higher resolution one
                    BitmapCache.getInstance().putArtistImage(mArtist, image);
                } catch (ImageNotFoundException e) {
                    ArtworkManager.getInstance(mApplicationContext).fetchImage(mArtist);
                }
            }
        }
    }

    private class TrackArtistImageRunner implements Runnable {

        private final int mWidth;

        private final int mHeight;

        private final ArtistModel mArtist;

        private TrackArtistImageRunner(final TrackModel trackModel, final int width, final int height) {
            long artistId = MusicLibraryHelper.getArtistIDFromName(trackModel.getTrackArtistName(), mApplicationContext);
            mArtist = new ArtistModel(trackModel.getTrackArtistName(), artistId);
            mWidth = width;
            mHeight = height;
        }

        /**
         * Load the image for the given artist from the mediastore.
         */
        @Override
        public void run() {
            // At first get image independent of resolution (can be replaced later with higher resolution)
            Bitmap image = BitmapCache.getInstance().requestArtistImage(mArtist);
            mListener.receiveArtistBitmap(image);

            // If image was to small get it in the right resolution
            if (image == null || !(mWidth <= image.getWidth() && mHeight <= image.getHeight())) {
                try {
                    image = ArtworkManager.getInstance(mApplicationContext).getImage(mArtist, mWidth, mHeight, true);
                    mListener.receiveArtistBitmap(image);
                    // Replace image with higher resolution one
                    BitmapCache.getInstance().putArtistImage(mArtist, image);
                } catch (ImageNotFoundException e) {
                    ArtworkManager.getInstance(mApplicationContext).fetchImage(mArtist);
                }
            }
        }
    }

    private class AlbumImageRunner implements Runnable {

        private final int mWidth;

        private final int mHeight;

        private final AlbumModel mAlbum;

        private AlbumImageRunner(AlbumModel album, int width, int height) {
            mAlbum = album;
            mWidth = width;
            mHeight = height;
        }

        /**
         * Load the image for the given album from the mediastore.
         */
        @Override
        public void run() {
            // At first get image independent of resolution (can be replaced later with higher resolution)
            Bitmap image = BitmapCache.getInstance().requestAlbumBitmap(mAlbum);
            if (image != null) {
                mListener.receiveAlbumBitmap(image);
            }

            try {
                // If image was to small get it in the right resolution
                if (image == null || !(mWidth <= image.getWidth() && mHeight <= image.getHeight())) {
                    image = ArtworkManager.getInstance(mApplicationContext).getImage(mAlbum, mWidth, mHeight, true);
                    mListener.receiveAlbumBitmap(image);
                    // Replace image with higher resolution one
                    BitmapCache.getInstance().putAlbumBitmap(mAlbum, image);
                }
            } catch (ImageNotFoundException e) {
                // Try to fetch the image here
                ArtworkManager.getInstance(mApplicationContext).fetchImage(mAlbum);
            }
        }
    }

    /**
     * Callback if image was loaded.
     */
    public interface CoverBitmapReceiver {
        void receiveAlbumBitmap(Bitmap bm);

        void receiveArtistBitmap(Bitmap bm);
    }
}