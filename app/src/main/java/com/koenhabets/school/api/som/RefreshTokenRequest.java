package com.koenhabets.school.api.som;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RefreshTokenRequest extends Request<String> {
    private static String url = "/oauth2/token?grant_type=refresh_token&client_id=D50E0C06-32D1-4B41-A137-A9A850C892C2&client_secret=vDdWdKwPNaPCyhCDhaCnNeydyLxSGNJX&refresh_token=";

    private Response.Listener<String> responListener;

    public RefreshTokenRequest(String refreshToken,
                              String somApiUrl,
                              Response.Listener<String> responseListener,
                              Response.ErrorListener errorListener) {

        super(Request.Method.POST, somApiUrl  + url + refreshToken, errorListener);

        this.responListener = responseListener;
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
        params.put("Content-Type", "application/x-www-form-urlencoded");

        return params;
    }

    @Override
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
