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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NetpresenterRequest extends Request<String> {

    private static String url = "https://api.scholica.com/2.0/communities/1/module";

    private Response.Listener<String> responListener;
    private String requestToken;

    public NetpresenterRequest(String requestToken,
                               Response.Listener<String> responseListener,
                               Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.requestToken = requestToken;
        this.responListener = responseListener;
    }

    public static String parseResponse(String response) throws JSONException {
        final NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(SchoolApp.getContext());
        boolean b = response.contains("H31");
        if (b) {
            SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
            String noti = sharedPref.getString("notification", "false");
            if (Objects.equals(noti, "false")) {
                mBuilder.setVibrate(new long[]{50, 50, 50, 50, 50, 50, 50, 50, 50, 50});
            }
            mBuilder.setSmallIcon(R.drawable.ic_stat_action_list);
            mBuilder.setContentTitle("JAAAA");
            mBuilder.setOngoing(true);
            NotificationManager mNotificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(2, mBuilder.build());

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("notification", "true");
            editor.apply();

            Log.i("Uitval", "ja");
        } else {
            SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("notification", "false");
            editor.apply();
            NotificationManager notificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(2);
        }
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonMain = jsonObject.getJSONObject("result");
        String resultString = jsonMain.getString("content");
        return resultString;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("path", "/netpresenter");
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
