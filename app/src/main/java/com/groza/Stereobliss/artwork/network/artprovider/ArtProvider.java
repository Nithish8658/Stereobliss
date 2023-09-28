/* */

package com.groza.Stereobliss.artwork.network.artprovider;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.groza.Stereobliss.artwork.network.ArtworkRequestModel;
import com.groza.Stereobliss.utils.StringCompareUtils;

import com.groza.Stereobliss.artwork.network.ImageResponse;

import org.json.JSONException;

public abstract class ArtProvider {

    public interface ArtFetchError {
        void fetchJSONException(final ArtworkRequestModel model, final JSONException exception);

        void fetchVolleyError(final ArtworkRequestModel model, final VolleyError error);

        void fetchError(final ArtworkRequestModel model);
    }

    public abstract void fetchImage(final ArtworkRequestModel model, final Response.Listener<ImageResponse> listener, final ArtFetchError errorListener);

    boolean compareAlbumResponse(final String expectedAlbum, final String expectedArtist, final String retrievedAlbum, final String retrievedArtist) {
        return StringCompareUtils.compareStrings(expectedAlbum, retrievedAlbum) && StringCompareUtils.compareStrings(expectedArtist, retrievedArtist);
    }

    boolean compareArtistResponse(final String expectedArtist, final String retrievedArtist) {
        return StringCompareUtils.compareStrings(expectedArtist, retrievedArtist);
    }
}
