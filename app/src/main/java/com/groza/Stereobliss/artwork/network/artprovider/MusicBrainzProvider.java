/* */

package com.groza.Stereobliss.artwork.network.artprovider;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.groza.Stereobliss.artwork.network.ArtworkRequestModel;
import com.groza.Stereobliss.artwork.network.LimitingRequestQueue;

import com.groza.Stereobliss.BuildConfig;
import com.groza.Stereobliss.artwork.network.ImageResponse;
import com.groza.Stereobliss.artwork.network.requests.OdysseyByteRequest;
import com.groza.Stereobliss.artwork.network.requests.OdysseyJsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MusicBrainzProvider extends ArtProvider {

    private static final String TAG = MusicBrainzProvider.class.getSimpleName();

    private static final String MUSICBRAINZ_API_URL = "https://musicbrainz.org/ws/2";

    private static final String COVERART_ARCHIVE_API_URL = "https://coverartarchive.org";

    private static final String MUSICBRAINZ_FORMAT_JSON = "&fmt=json";

    private static final int MUSICBRAINZ_LIMIT_RESULT_COUNT = 10;

    private static final String MUSICBRAINZ_LIMIT_RESULT = "&limit=" + MUSICBRAINZ_LIMIT_RESULT_COUNT;

    /**
     * {@link RequestQueue} used to handle the requests of this class.
     */
    private final RequestQueue mRequestQueue;

    /**
     * Singleton instance
     */
    private static MusicBrainzProvider mInstance;

    private MusicBrainzProvider(final Context context) {
        mRequestQueue = LimitingRequestQueue.getInstance(context.getApplicationContext());
    }

    public static synchronized MusicBrainzProvider getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new MusicBrainzProvider(context);
        }
        return mInstance;
    }

    @Override
    public void fetchImage(final ArtworkRequestModel model, final Response.Listener<ImageResponse> listener, final ArtFetchError errorListener) {
        switch (model.getType()) {
            case ALBUM:
                getAlbumMBId(model,
                        response -> parseMusicBrainzReleaseJSON(model, 0, response, listener, errorListener),
                        error -> errorListener.fetchVolleyError(model, error));
                break;
            case ARTIST:
                // not used for this provider
                break;
        }
    }

    /**
     * Wrapper to get an MBID out of an {@link ArtworkRequestModel}.
     *
     * @param model         Album to get the MBID for
     * @param listener      Response listener
     * @param errorListener Error listener
     */
    private void getAlbumMBId(final ArtworkRequestModel model, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {
        final String albumName = model.getLuceneEscapedEncodedAlbumName();
        final String artistName = model.getLuceneEscapedEncodedArtistName();

        String url;
        if (!artistName.isEmpty()) {
            url = MUSICBRAINZ_API_URL + "/" + "release/?query=release:" + albumName + "%20AND%20artist:" + artistName + MUSICBRAINZ_LIMIT_RESULT + MUSICBRAINZ_FORMAT_JSON;
        } else {
            url = MUSICBRAINZ_API_URL + "/" + "release/?query=release:" + albumName + MUSICBRAINZ_LIMIT_RESULT + MUSICBRAINZ_FORMAT_JSON;
        }

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Requesting release mbid for: " + url);
        }

        OdysseyJsonObjectRequest jsonObjectRequest = new OdysseyJsonObjectRequest(url, null, listener, errorListener);

        mRequestQueue.add(jsonObjectRequest);
    }

    /**
     * Parses the JSON response and searches the image URL
     *
     * @param model         Album to check for an image
     * @param releaseIndex  Index of the requested release to check for an image
     * @param response      Response to check use to search for an image
     * @param listener      Callback to handle the response
     * @param errorListener Callback to handle errors
     */
    private void parseMusicBrainzReleaseJSON(final ArtworkRequestModel model, final int releaseIndex, final JSONObject response,
                                             final Response.Listener<ImageResponse> listener, final ArtFetchError errorListener) {
        if (releaseIndex >= MUSICBRAINZ_LIMIT_RESULT_COUNT) {
            errorListener.fetchVolleyError(model, null);
            return;
        }

        try {
            final JSONArray releases = response.getJSONArray("releases");
            if (releases.length() > releaseIndex) {
                final JSONObject baseObj = releases.getJSONObject(releaseIndex);

                // verify response
                final String album = baseObj.getString("title");
                final String artist = baseObj.getJSONArray("artist-credit").getJSONObject(0).getString("name");

                final boolean isMatching = compareAlbumResponse(model.getAlbumName(), model.getArtistName(), album, artist);

                if (isMatching) {
                    final String mbid = releases.getJSONObject(releaseIndex).getString("id");
                    model.setMBId(mbid);

                    final String url = COVERART_ARCHIVE_API_URL + "/" + "release/" + mbid + "/front-500";

                    getAlbumImage(url, model, listener, error -> {
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "No image found for: " + model.getAlbumName() + " with release index: " + releaseIndex);
                        }

                        if (releaseIndex + 1 < releases.length()) {
                            parseMusicBrainzReleaseJSON(model, releaseIndex + 1, response, listener, errorListener);
                        } else {
                            errorListener.fetchVolleyError(model, error);
                        }
                    });
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.v(TAG, "Response ( " + album + "-" + artist + " )" + " doesn't match requested model: " +
                                "( " + model.getLoggingString() + " )");
                    }

                    if (releaseIndex + 1 < releases.length()) {
                        parseMusicBrainzReleaseJSON(model, releaseIndex + 1, response, listener, errorListener);
                    } else {
                        errorListener.fetchVolleyError(model, null);
                    }
                }
            } else {
                errorListener.fetchVolleyError(model, null);
            }
        } catch (JSONException e) {
            errorListener.fetchJSONException(model, e);
        }
    }

    /**
     * Raw download for an image
     *
     * @param url           Final image URL to download
     * @param model         Album associated with the image to download
     * @param listener      Response listener to receive the image as a byte array
     * @param errorListener Error listener
     */
    private void getAlbumImage(final String url, final ArtworkRequestModel model,
                               final Response.Listener<ImageResponse> listener,
                               final Response.ErrorListener errorListener) {
        Request<ImageResponse> byteResponse = new OdysseyByteRequest(model, url, listener, errorListener);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Get image: " + url);
        }

        mRequestQueue.add(byteResponse);
    }
}
