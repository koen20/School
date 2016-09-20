package com.koenhabets.school;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.api.CalendarRequest;
import com.koenhabets.school.api.GradesRequest;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Objects;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;


        for (int i = 0; i < N; i++) {
            RequestQueue requestQueue;
            requestQueue = Volley.newRequestQueue(context);
            int appWidgetId = appWidgetIds[i];
            final SharedPreferences sharedPref = context.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
            final String requestToken = sharedPref.getString("request_token", "no request token");
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int day = now.get(Calendar.DAY_OF_MONTH);
            if (hour > 15) {
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
                    Log.i("responseee", response);
                    views.setTextViewText(R.id.textView_widget, sharedPref.getString("calnow", ""));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", "" + error.getMessage());
                }
            });
            String result = sharedPref.getString(ts, "no");
            if (!Objects.equals(result, "no")) {
                Log.i("Stored", result);
                Log.i("calnow", sharedPref.getString("calnow", ""));
                views.setTextViewText(R.id.textView_widget, sharedPref.getString("calnow", ""));
            } else {
                requestQueue.add(requestCalendar);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
