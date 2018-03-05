package com.koenhabets.school.api.som;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;


public class SchoolRequest extends Request<String> {
    private static String url = "https://servers.somtoday.nl/organisaties.json";

    private Response.Listener<String> responListener;

    public SchoolRequest(Response.Listener<String> responseListener,
                         Response.ErrorListener errorListener) {

        super(Request.Method.GET, url, errorListener);

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
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
