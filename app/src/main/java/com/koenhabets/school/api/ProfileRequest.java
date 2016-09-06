package com.koenhabets.school.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.koenhabets.school.SchoolApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ProfileRequest extends Request<String> {

    private static String url = "https://api.scholica.com/2.0/communities/1/profile/391";

    private Response.Listener<String> responListener;
    private String requestToken;

    public ProfileRequest(String requestToken,
                          Response.Listener<String> responseListener,
                          Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.requestToken = requestToken;
        this.responListener = responseListener;
    }

    public static String parseResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonMain = jsonObject.getJSONObject("result");

        SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("name", jsonMain.getString("name"));
        editor.putString("picture", jsonMain.getString("picture"));
        editor.apply();
        JSONObject jsonMain2 = jsonMain.getJSONObject("info");
        editor.putString("email", jsonMain2.getString("email"));
        //editor.putString("class", jsonMain2.getString("class"));
        editor.putString("username", jsonMain2.getString("username"));
        editor.apply();

        return "";
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
