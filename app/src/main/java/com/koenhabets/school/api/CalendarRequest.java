package com.koenhabets.school.api;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

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

public class CalendarRequest extends Request<String> {

    private static String url = "https://api.scholica.com/2.0/communities/1/calendar/schedule";

    private Response.Listener<String> responListener;
    private String requestToken;
    private String timeStamp;

    public CalendarRequest(String requestToken,
                           String timeStamp,
                           Response.Listener<String> responseListener,
                           Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.requestToken = requestToken;
        this.timeStamp = timeStamp;
        this.responListener = responseListener;
    }

    public static String parseResponse(String response, String timeStamp) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonMain = jsonObject.getJSONObject("result");
        SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(timeStamp, jsonObject.toString());
        editor.apply();
        JSONArray jsonArray = jsonMain.getJSONArray("items");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SchoolApp.getContext());
        mBuilder.setSmallIcon(R.drawable.ic_calendar_black_24dp);
        mBuilder.setContentTitle("Rooster");
        mBuilder.setOngoing(true);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String resultString = "";

        for (int i = 0; i < jsonArray.length(); i++) {
            int uur = i + 1;
            String title = uur + ". Onbekend";
            String lokaal = "";
            JSONObject vak = jsonArray.getJSONObject(i);

            if (vak.has("title") && vak.has("subtitle")) {
                title = vak.getString("title");
                lokaal = vak.getString("subtitle");
            } else if (vak.has("type")) {
                if (vak.getString("type").equals("divider")) {

                }
            }

            resultString += title + " " + lokaal + "\n";
            inboxStyle.addLine(title + " " + lokaal);
        }
        NotificationManager mNotificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setStyle(inboxStyle);

        boolean notificatiecalendar = sharedPref.getBoolean("notificatie-calendar", true);

        if (notificatiecalendar) {
            mNotificationManager.notify(1, mBuilder.build());
        }

        return resultString;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("token", requestToken);
        params.put("time", timeStamp);
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
            String resultString = parseResponse(data, timeStamp);
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
