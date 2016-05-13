package com.koenhabets.school.api;


import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
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

public class GradesRequest extends Request<String> {

    private static String url = "https://api.scholica.com/2.0/communities/1/module";

    private Response.Listener<String> responListener;
    private String requestToken;

    static String[] subjects = {
            "Aardrijkskunde", "Duitse taal", "Economie", "Engelse taal", "Franse taal",
            "Geschiedenis", "Levensbeschouwing", "Muziek", "Nederlandse taal", "Scheikunde",
            "Wiskunde", "Natuurkunde", "Biologie", "Lichamelijke opvoeding", "Beeldende vorming"};

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
        String resultStringr = "";
        String looset;
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("grades")) {
            double loose = jsonObject.getDouble("loose");
            JSONObject jsonMain = jsonObject.getJSONObject("grades");

            for (String subject : subjects) {
                JSONObject vak = jsonMain.getJSONObject(subject);
                double avg = vak.getDouble("avg");
                if (avg < 6) {
                    resultString += subject + ": " + "<font color=red>" + avg + "</font><br>";
                } else {
                    resultString += subject + ": " + "<font color=green>" + avg + "</font><br>";
                }
                resultStringr = resultStringr + avg;
            }
            if (loose > 4) {
                looset = "<br>" + "Verlisepunten: " + "<font color=red>" + loose + "</font>";
                resultString += looset;
            } else if (loose > 2 & loose < 4) {
                looset = "<br>" + "Verlisepunten: " + "<font color=#FF9800>" + loose + "</font>";
                resultString += looset;
            } else {
                looset = "<br>" + "Verlisepunten: " + "<font color=green>" + loose + "</font>";
                resultString += looset;
            }
            SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
            String resultStringOld = sharedPref.getString("grades", "no grades");
            if (resultStringOld.equals("no grades")) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("grades", resultStringr);
                editor.apply();
            }

            Log.d("old", resultStringOld);
            Log.d("new", resultStringr);
            if (!resultStringr.equals(resultStringOld)) {
                Log.i("grades", "Nieuw cijfer");
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SchoolApp.getContext());
                mBuilder.setSmallIcon(R.drawable.ic_grades_black_24dp);
                mBuilder.setContentTitle("Nieuw cijfer");
                mBuilder.setContentText(Html.fromHtml(looset));
                mBuilder.setVibrate(new long[]{50, 50, 50, 50, 50, 50, 50, 50, 50, 50});
                NotificationManager mNotificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                boolean notificatiecijfer = sharedPref.getBoolean("notificatie-cijfers", true);

                if (notificatiecijfer) {
                    mNotificationManager.notify(3, mBuilder.build());
                }
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("grades", resultStringr);
            editor.apply();
        } else if (jsonObject.has("announcement")) {
            resultString = jsonObject.getString("announcement");
        }
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

