package com.koenhabets.school;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.api.BackgroudUpdateService;
import com.koenhabets.school.api.CalendarRequest;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        final NotificationCompat.Builder mBuilder;
        Log.i("Alarm", "Started");

        RequestQueue requestQueue;
        SharedPreferences sharedPref = context.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");
        Log.i("RequestToken", requestToken);
        requestQueue = Volley.newRequestQueue(context);
        mBuilder = new NotificationCompat.Builder(context);
        String url = "https://api.scholica.com/2.0/communities/1/module";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        boolean b = response.contains("H31");

                        if(b){
                            SharedPreferences sharedPref = context.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                            String noti = sharedPref.getString("notification", "false");
                            if (Objects.equals(noti, "false")){
                                mBuilder.setVibrate(new long[]{50, 50, 50, 50, 50, 50, 50, 50, 50, 50});
                            }
                            mBuilder.setSmallIcon(R.drawable.ic_stat_action_list);
                            mBuilder.setContentTitle("JAAAA");
                            mBuilder.setOngoing(true);
                            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(2, mBuilder.build());

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("notification", "true");
                            editor.apply();

                            Log.i("Uitval", "ja");
                        } else {
                            SharedPreferences sharedPref = context.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("notification", "false");
                            editor.apply();
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(2);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("path", "/netpresenter");
                params.put("token", requestToken);
                return params;
            }
        };
        requestQueue.add(postRequest);
        Long tsLong = getStartOfDayInMillis() / 1000;
        final String ts = tsLong.toString();
        CalendarRequest request = new CalendarRequest(requestToken, ts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(request);


    }
    public long getStartOfDayInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}