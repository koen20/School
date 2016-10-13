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
        NotificationManager mNotificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(SchoolApp.getContext());
        SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String klas = sharedPref.getString("class", "");
        boolean b = response.contains(klas);
        if (b) {
            mBuilder.setVibrate(new long[]{50, 50, 50, 50, 50, 50, 50, 50, 50, 50});
            mBuilder.setSmallIcon(R.drawable.ic_netpresenter_black_24dp);
            mBuilder.setContentTitle(klas + " Staat in de netpresenter");

            boolean notificatienet = sharedPref.getBoolean("notificatie-netpresenter", true);
            Boolean notified = sharedPref.getBoolean("notified", false);

            if (notificatienet && !notified) {
                mNotificationManager.notify(2, mBuilder.build());
                editor.putBoolean("notified", true);
            }
            Log.i("Uitval", "ja");
        } else {
            mNotificationManager.cancel(2);
            editor.putBoolean("notified", false);
        }
        editor.apply();
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonMain = jsonObject.getJSONObject("result");
        return jsonMain.getString("content");
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
