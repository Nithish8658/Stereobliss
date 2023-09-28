/* */

package com.groza.Stereobliss.artwork.network.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import com.groza.Stereobliss.BuildConfig;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OdysseyJsonObjectRequest extends JsonObjectRequest {

    public OdysseyJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-agent", "Application Odyssey/" + BuildConfig.VERSION_NAME + " (https://github.com/gateship-one/odyssey)");
        return headers;
    }
}
