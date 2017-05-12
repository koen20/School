package com.koenhabets.school.api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Objects;


public class BackgroudUpdateService extends IntentService {

    public static final String ACTION_REFRESH = "com.koenhabets.school.api.action.REFRESH";


    public BackgroudUpdateService() {
        super("BackgroudUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH.equals(action)) {
                handleActionRefresh();
            }
        }
    }

    private void handleActionRefresh() {
        Log.d(this.getClass().getSimpleName(), "Started service");
        RequestQueue requestQueue;
        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");
        requestQueue = Volley.newRequestQueue(this);

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int day = now.get(Calendar.DAY_OF_MONTH);
        if (hour > 16) {
            day += 1;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Long tsLong = calendar.getTimeInMillis() / 1000;
        final String ts = tsLong.toString();
        CalendarRequest requestCalendar = new CalendarRequest(requestToken, ts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });

        requestQueue.add(requestCalendar);

        NetpresenterRequest netpresenterRequest = new NetpresenterRequest(requestToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        //requestQueue.add(netpresenterRequest);

        GradesRequest requestGrades = new GradesRequest(requestToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        //requestQueue.add(requestGrades);

    }
}
