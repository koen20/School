package com.koenhabets.school;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.text.Html;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.api.AppointmentsRequest;
import com.koenhabets.school.fragments.TimeTableFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobUpdate extends JobService {
    private int day;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i("JOB", "Start");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        long da = 0;
        if (cal.get(Calendar.HOUR_OF_DAY) > 16) {
            da = 86400;
        }
        final long startOfDay = getStartOfDay(day) + da;
        final long endOfDay = getEndOfDay(day) + da;
        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String requestToken = sharedPref.getString("zermeloAccessToken", "no request token");
        String school = sharedPref.getString("school", "bernardinuscollege");
        final SharedPreferences prefs = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        AppointmentsRequest request = new AppointmentsRequest(requestToken, school, startOfDay, endOfDay, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = TimeTableFragment.readSchedule(getApplicationContext());
                try {
                    JSONArray appointments = TimeTableFragment.parseResponse(response, startOfDay, getApplicationContext());
                    JSONArray oldAppointments = jsonObject.getJSONArray(Long.toString(startOfDay));
                    checkScheduleChange(appointments, oldAppointments);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                parseResponse(response);
                Date dt = new Date();

                prefs.edit().putLong("lastRun", dt.getTime()).apply();
                jobFinished(params, false);
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

        boolean disabled = false;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        long interval = 1600000;//30 minutes
        if (hour > 15 || hour < 7) {
            interval = 3600000 * 3;//3 hours
        }
        Date date = new Date();
        long l = prefs.getLong("lastRun", 0);
        if (date.getTime() - l > interval){
            if (Objects.equals(weekDay, "Friday") && cal.get(Calendar.HOUR_OF_DAY) > 16) {
                disabled = true;
            }
            if (!Objects.equals(weekDay, "Saturday") && !Objects.equals(weekDay, "Sunday") && !disabled) {
                Log.i("Job", "Getting schedule");
                requestQueue.add(request);
            } else {
                NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(555);
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void checkScheduleChange(JSONArray appointments, JSONArray oldAppointments) throws JSONException {
        Log.i("old" ,oldAppointments.toString());
        Log.i("new", appointments.toString());
        for (int i = 0; i < appointments.length(); i++){
            JSONObject newAppointment = appointments.getJSONObject(i);
            for (int k = 0; k < oldAppointments.length(); k++){
                JSONObject oldAppointment = oldAppointments.getJSONObject(k);
                if (newAppointment.getInt("id") == oldAppointment.getInt("id")){
                    if(newAppointment.getBoolean("cancelled") != oldAppointment.getBoolean("cancelled")){
                        if(newAppointment.getBoolean("cancelled")){
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "scheduleChange")
                                    .setSmallIcon(R.drawable.ic_time_table_black_24dp)
                                    .setContentTitle("Uitval")
                                    .setContentText("Het " + newAppointment.getInt("startTimeSlot") + "e uur val uit.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(ThreadLocalRandom.current().nextInt(1, 500 + 1), mBuilder.build());
                        }
                    }
                }
            }
        }

    }

    private void parseResponse(String response) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int priority = 1;
        if(hour >= 7 && hour < 17){
            priority = 3;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "schedule")
                        .setSmallIcon(R.drawable.ic_time_table_black_24dp)
                        .setContentTitle("Rooster")
                        .setPriority(priority)
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
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }

    private long getEndOfDay(int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, d);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }
}
