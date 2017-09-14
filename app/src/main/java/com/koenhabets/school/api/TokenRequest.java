package com.koenhabets.school.api;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class TokenRequest extends Request<String> {

    private static String url = "https://bernardinuscollege.zportal.nl/api/v2/oauth/token?grant_type=authorization_code&code=";

    private Response.Listener<String> responListener;

    public TokenRequest(String authCode,
                        Response.Listener<String> responseListener,
                        Response.ErrorListener errorListener) {

        super(Method.POST, url + authCode, errorListener);

        this.responListener = responseListener;
    }

    private static String parseResponse(String response) throws JSONException {
        Log.d("Response", response);
        JSONObject jsonObject = new JSONObject(response);

        return jsonObject.getString("access_token");
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
