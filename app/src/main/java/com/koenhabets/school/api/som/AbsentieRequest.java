package com.koenhabets.school.api.som;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AbsentieRequest extends Request<String> {
    private static String url = "/rest/v1/absentiemeldingen";

    private Response.Listener<String> responListener;
    private String accessToken;

    public AbsentieRequest(String accessToken,
                           String somApiUrl,
                           Response.Listener<String> responseListener,
                           Response.ErrorListener errorListener) {

        super(Request.Method.GET, somApiUrl + url, errorListener);

        this.responListener = responseListener;
        this.accessToken = accessToken;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String data;
        try {
            data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            data = new String(response.data);
        }
        return Response.success(data, null);
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> params = new HashMap<>();
        params.put("Accept", "application/vnd.topicus.platinum+json");
        params.put("Authorization", "Bearer " + accessToken);

        return params;
    }

    @Override
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
