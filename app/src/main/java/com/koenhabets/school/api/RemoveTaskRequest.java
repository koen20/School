package com.koenhabets.school.api;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RemoveTaskRequest extends Request<String> {
    private static String url = "https://api.scholica.com/2.0/communities/1/calendar/tasks/";
    private String requestToken;

    private Response.Listener<String> responListener;

    public RemoveTaskRequest(String requestToken,
                       String id,
                       Response.Listener<String> responseListener,
                       Response.ErrorListener errorListener) {

        super(Request.Method.POST, url + id + "/delete", errorListener);


        this.requestToken = requestToken;
        this.responListener = responseListener;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("token", requestToken);
        return params;
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
