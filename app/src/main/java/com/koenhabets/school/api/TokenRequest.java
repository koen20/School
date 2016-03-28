package com.koenhabets.school.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.koenhabets.school.SchoolApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TokenRequest extends Request<String> {

    private static String url = "https://api.scholica.com/2.0/communities/1/authenticate";

    private Response.Listener<String> responListener;

    public TokenRequest(
            Response.Listener<String> responseListener,
            Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.responListener = responseListener;
    }

    public static String parseResponse(String response) throws JSONException {
        Log.d("Response", response);
        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_YEAR);
        String requestToken = "";
        try {
            JSONObject responsetoken = new JSONObject(response);
            JSONObject jsonMain = responsetoken.getJSONObject("result");
            requestToken = jsonMain.getString("request_token");
            Log.i("requestToken", requestToken);

            SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("request_token", requestToken);
            editor.putInt("request_token_day", today);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestToken;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "407332");
        params.put("password", PasswordHolder.getPassword());
        params.put("access_token", "470d7d90cae6e34f36bc9110026a4370e8864551b0e7e7b33263163562c362a3d68f1937");
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
