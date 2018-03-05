package com.koenhabets.school.api;


import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class BackgroundUpdateService extends IntentService {

    private int day;

    public BackgroundUpdateService() {
        super("BackgroundUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("BackgroundUpdateService", "Start");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        long da = 0;
        if (cal.get(Calendar.HOUR_OF_DAY) > 16) {
            da = 86400;
        }
        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String requestToken = sharedPref.getString("zermeloAccessToken", "no request token");
        String school = sharedPref.getString("school", "bernardinuscollege");
        AppointmentsRequest request = new AppointmentsRequest(requestToken, school, getStartOfDay(day) + da, getEndOfDay(day) + da, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("getCalendar", error.toString());
            }
        });

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());
        if (!Objects.equals(weekDay, "Saturday") && !Objects.equals(weekDay, "Sunday")) {
            requestQueue.add(request);
        } else {
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(555);
        }

    }

    private void parseResponse(String response) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "schedule")
                        .setSmallIcon(R.drawable.ic_time_table_black_24dp)
                        .setContentTitle("Rooster")
                        .setChannelId("schedule");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonResp = jsonObject.getJSONObject("response");
            JSONArray jsonArray = jsonResp.getJSONArray("data");

            int lastHour = 0;

            for (int w = 1; w < 12; w++) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject lesson = jsonArray.getJSONObject(i);
                        if (lesson.getInt("startTimeSlot") == w) {
                            if (lesson.getBoolean("valid")) {
                                if (lastHour != lesson.getInt("startTimeSlot")) {
                                    JSONArray subjects = lesson.getJSONArray("subjects");
                                    JSONArray locations = lesson.getJSONArray("locations");
                                    if (lesson.getBoolean("cancelled")) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            inboxStyle.addLine(Html.fromHtml("<del>" + lesson.getInt("startTimeSlot") + ". " + subjects.getString(0) + " " + locations.getString(0) + "</del>", Html.FROM_HTML_MODE_LEGACY));
                                        } else {
                                            inboxStyle.addLine(Html.fromHtml("<del>" + lesson.getInt("startTimeSlot") + ". " + subjects.getString(0) + " " + locations.getString(0) + "</del>"));
                                        }
                                    } else {
                                        inboxStyle.addLine(lesson.getInt("startTimeSlot") + ". " + subjects.getString(0) + " " + locations.getString(0));
                                    }
                                }
                                lastHour = lesson.getInt("startTimeSlot");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mBuilder.setStyle(inboxStyle);
        mBuilder.setOngoing(true);
        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("scheduleNotifcation", true)) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(555, mBuilder.build());
        }
    }

    private long getStartOfDay(int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, d);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        return cal.getTimeInMillis() / 1000;
    }

    private long getEndOfDay(int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, d);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        return cal.getTimeInMillis() / 1000;
    }
}
