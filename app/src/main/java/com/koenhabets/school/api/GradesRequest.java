package com.koenhabets.school.api;


import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.koenhabets.school.R;
import com.koenhabets.school.SchoolApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GradesRequest extends Request<String> {

    private static String url = "https://api.scholica.com/2.0/communities/1/module?json=1&term=2";

    private Response.Listener<String> responListener;
    private String requestToken;
    private String timeStamp;

    public GradesRequest(String requestToken,
                           String timeStamp,
                           Response.Listener<String> responseListener,
                           Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.requestToken = requestToken;
        this.timeStamp = timeStamp;
        this.responListener = responseListener;
    }

    public static String parseResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonMain = jsonObject.getJSONObject("grades");


        String resultString = "";
        return resultString;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("token", requestToken);
        params.put("path", "grades/get");
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
        try {
            String resultString = parseResponse(data);
            return Response.success(resultString, null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
