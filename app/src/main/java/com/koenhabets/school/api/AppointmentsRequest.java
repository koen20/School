package com.koenhabets.school.api;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public class AppointmentsRequest extends Request<String> {
    private static String url = "https://bernardinuscollege.zportal.nl/api/v3/appointments?user=~me&access_token=";

    private Response.Listener<String> responListener;

    public AppointmentsRequest(String accessToken,
                               long startTime,
                               long endTime,
                               Response.Listener<String> responseListener,
                               Response.ErrorListener errorListener) {

        super(Method.GET, url + accessToken + "&start=" + startTime + "&end=" + endTime, errorListener);

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
