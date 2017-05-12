package com.koenhabets.school.api;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public class TaskRequest extends Request<String> {
    ///// TODO: 5/12/2017 fix task request 
    private static String url = "https://api.scholica.com/2.0/communities/1/calendar/tasks/1688285/complete";

    private Response.Listener<String> responListener;

    public TaskRequest(Response.Listener<String> responseListener,
                Response.ErrorListener errorListener) {

        super(Request.Method.POST, url, errorListener);

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
