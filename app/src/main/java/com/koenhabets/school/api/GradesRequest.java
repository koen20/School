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

public class GradesRequest extends Request<String> {

    private static String url = "https://api.scholica.com/2.0/communities/1/module?json=1&term=2";

    private Response.Listener<String> responListener;
    private String requestToken;

    static String[] test = {"Aardrijkskunde", "Duitse taal", "Economie", "Engelse taal", "Franse taal", "Geschiedenis", "Levensbeschouwing", "Muziek", "Nederlandse taal", "Scheikunde", "Wiskunde", "Natuurkunde", "Biologie", "Lichamelijke opvoeding", "Beeldende vorming"};

    public GradesRequest(String requestToken,
                         Response.Listener<String> responseListener,
                         Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.requestToken = requestToken;
        this.responListener = responseListener;
    }

    public static String parseResponse(String response) throws JSONException {
        Log.i("Grades", response);

        String resultString = "";
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonMain = jsonObject.getJSONObject("grades");
        for (int i = 0; i < test.length; i++) {
            JSONObject vak = jsonMain.getJSONObject(test[i]);
            String avg = vak.getString("avg");
            resultString += test[i] + ": " + avg + "\n";
        }
        SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String resultStringOld = sharedPref.getString("grades", "no grades");
        if (resultStringOld == "no grades") {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("grades", resultString);
            editor.apply();
        }
        Log.d("old", resultStringOld);
        Log.d("new", resultString);
        if (!Objects.equals(resultString, resultStringOld)) {
            Log.i("grades", "Nieuw cijfer");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SchoolApp.getContext());
            mBuilder.setSmallIcon(R.drawable.ic_stat_action_list);
            mBuilder.setContentTitle("Nieuw cijfer");
            NotificationManager mNotificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(3, mBuilder.build());
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("grades", resultString);
        editor.apply();
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
