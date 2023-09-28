/* */

package com.groza.Stereobliss.artwork.network.requests;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.groza.Stereobliss.artwork.network.ArtworkRequestModel;

import com.groza.Stereobliss.BuildConfig;
import com.groza.Stereobliss.artwork.network.ImageResponse;

import java.util.HashMap;
import java.util.Map;

public class OdysseyByteRequest extends Request<ImageResponse> {

    private final Response.Listener<ImageResponse> mListener;

    private final ArtworkRequestModel mModel;

    public OdysseyByteRequest(ArtworkRequestModel model, String url, Response.Listener<ImageResponse> listener, @Nullable Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        mModel = model;
        mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-agent", "Application Odyssey/" + BuildConfig.VERSION_NAME + " (https://github.com/gateship-one/odyssey)");
        return headers;
    }

    @Override
    protected Response<ImageResponse> parseNetworkResponse(NetworkResponse response) {
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.model = mModel;
        imageResponse.url = getUrl();
        imageResponse.image = response.data;
        return Response.success(imageResponse, null);
    }

    @Override
    protected void deliverResponse(ImageResponse response) {
        mListener.onResponse(response);
    }
}
