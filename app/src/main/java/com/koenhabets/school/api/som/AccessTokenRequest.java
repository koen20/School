package com.koenhabets.school.api.som;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AccessTokenRequest extends Request<String> {
    private static String url = "https://productie.somtoday.nl/oauth2/token?scope=openid&grant_type=password&username=28f730ff-5c19-400c-ae40-7bcad041791e\\";

    private Response.Listener<String> responListener;

    public AccessTokenRequest(String username,
                               String password,
                               Response.Listener<String> responseListener,
                               Response.ErrorListener errorListener) {

        super(Method.POST, url + username + "&password=" + password, errorListener);

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
        Map<String, String> params = new HashMap<String, String>();
        params.put("authorization", "Basic RDUwRTBDMDYtMzJEMS00QjQxLUExMzctQTlBODUwQzg5MkMyOnZEZFdkS3dQTmFQQ3loQ0RoYUNuTmV5ZHlMeFNHTkpY");

        return params;
    }

    @Override
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
