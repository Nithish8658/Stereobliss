/* */

package com.groza.Stereobliss.artwork.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.groza.Stereobliss.models.AlbumModel;
import com.groza.Stereobliss.artwork.storage.ArtworkDatabaseManager;
import com.groza.Stereobliss.models.ArtistModel;

import java.io.ByteArrayOutputStream;

public class InsertImageTask extends AsyncTask<ImageResponse, Object, ArtworkRequestModel> {

    public interface ImageSavedCallback {
        void onImageSaved(ArtworkRequestModel artworkRequestModel);
    }

    /**
     * Maximmum size for either x or y of an image
     */
    private static final int MAXIMUM_IMAGE_RESOLUTION = 500;

    /**
     * Compression level if images are rescaled
     */
    private static final int IMAGE_COMPRESSION_SETTING = 80;

    /**
     * Maximum size of an image blob to insert in SQLite database. (1MB)
     */
    private static final int MAXIMUM_IMAGE_SIZE = 1024 * 1024;

    private final ArtworkDatabaseManager mArtworkDatabaseManager;

    private final ImageSavedCallback mImageSavedCallback;

    public InsertImageTask(final Context context, final ImageSavedCallback imageSavedCallback) {
        mArtworkDatabaseManager = ArtworkDatabaseManager.getInstance(context);
        mImageSavedCallback = imageSavedCallback;
    }

    @Override
    protected ArtworkRequestModel doInBackground(ImageResponse... params) {
        ImageResponse response = params[0];

        if (response.image == null) {
            insertImage(response.model, null, response.localArtworkPath);
            return response.model;
        }

        // Rescale them if to big
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(response.image, 0, response.image.length, options);
        if ((options.outHeight > MAXIMUM_IMAGE_RESOLUTION || options.outWidth > MAXIMUM_IMAGE_RESOLUTION)) {
            // Calculate minimal scaling factor
            float factor = Math.min((float) MAXIMUM_IMAGE_RESOLUTION / (float) options.outHeight, (float) MAXIMUM_IMAGE_RESOLUTION / (float) options.outWidth);
            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeByteArray(response.image, 0, response.image.length, options);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            Bitmap.createScaledBitmap(bm, (int) (options.outWidth * factor), (int) (options.outHeight * factor), true)
                    .compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_SETTING, byteStream);

            if (byteStream.size() <= MAXIMUM_IMAGE_SIZE) {
                insertImage(response.model, byteStream.toByteArray(), null);
            }
        } else {
            if (response.image.length <= MAXIMUM_IMAGE_SIZE) {
                insertImage(response.model, response.image, null);
            }
        }

        return response.model;
    }

    @Override
    protected void onPostExecute(ArtworkRequestModel artworkRequestModel) {
        mImageSavedCallback.onImageSaved(artworkRequestModel);
    }

    private void insertImage(final ArtworkRequestModel model, final byte[] image, final String localArtworkPath) {
        switch (model.getType()) {
            case ALBUM:
                mArtworkDatabaseManager.insertAlbumImage((AlbumModel) model.getGenericModel(), image, localArtworkPath);
                break;
            case ARTIST:
                mArtworkDatabaseManager.insertArtistImage((ArtistModel) model.getGenericModel(), image);
                break;
        }
    }
}
