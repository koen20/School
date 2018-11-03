package com.koenhabets.school.api.som;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AccessTokenRequest extends Request<String> {
    private static String url = "https://productie.somtoday.nl/oauth2/token";
    private String username;
    private String password;
    private String uuid;
    private Response.Listener<String> responListener;

    public AccessTokenRequest(String username,
                               String password,
                               String uuid,
                               Response.Listener<String> responseListener,
                               Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.responListener = responseListener;
        this.username = username;
        this.password = password;
        this.uuid = uuid;
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
        params.put("authorization", "Basic RDUwRTBDMDYtMzJEMS00QjQxLUExMzctQTlBODUwQzg5MkMyOnZEZFdkS3dQTmFQQ3loQ0RoYUNuTmV5ZHlMeFNHTkpY");
        params.put("Content-Type", "application/x-www-form-urlencoded");

        return params;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("scope", "openid");
        params.put("grant_type", "password");
        params.put("username", uuid + "\\" + username);
        params.put("password", password);
        return params;
    };

    @Override
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
