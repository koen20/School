package com.koenhabets.school.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.EmptyViewLongPressListener;
import com.alamkanak.weekview.EventClickListener;
import com.alamkanak.weekview.EventLongPressListener;
import com.alamkanak.weekview.MonthChangeListener;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.api.TimeTableEvent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;

public class TimeTableWeekFragment extends Fragment implements EventClickListener<TimeTableEvent>, MonthChangeListener<TimeTableEvent>,
        EventLongPressListener<TimeTableEvent>, EmptyViewLongPressListener {
    RequestQueue requestQueue;
    WeekView mWeekView;
    List<WeekViewDisplayable<TimeTableEvent>> events = new ArrayList<>();
    boolean eventsLoaded = false;

    public TimeTableWeekFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_table_week, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        requestQueue = Volley.newRequestQueue(getContext());
        mWeekView = rootView.findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setEmptyViewLongPressListener(this);

        String res = getAppointments(getStartOfWeek(0), getEndOfWeek(0) + 604800);
        parseResponse(res, 0);

        return rootView;
    }

    private String getAppointments(long startTime, long endTime) {
        Log.i("start", startTime + "");
        Log.i("end", endTime + "");

        BufferedReader reader = null;
        StringBuilder stringBuilder = null;
        try {
            SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
            String requestToken = sharedPref.getString("zermeloAccessToken", "no request token");
            String school = sharedPref.getString("school", "bernardinuscollege");
            URL url;
            url = new URL("https://" + school + ".zportal.nl/api/v3/appointments?user=~me&access_token=" + requestToken + "&start=" + startTime + "&end=" + endTime);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            connection.setReadTimeout(10000);
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public JSONArray parseResponse(String response, long startTime) {
        events.clear();
        JSONArray jsonArrayDay = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonResp = jsonObject.getJSONObject("response");
            JSONArray jsonArray = jsonResp.getJSONArray("data");

            //int lastHour = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject lesson = jsonArray.getJSONObject(i);
                    if (lesson.getBoolean("valid")) {
                        //if (lastHour != lesson.getInt("startTimeSlot")) {
                        JSONArray subjects = lesson.getJSONArray("subjects");
                        JSONArray locations = lesson.getJSONArray("locations");
                        String subject = "";
                        String location = "";
                        try {
                            subject = subjects.getString(0);
                            location = locations.getString(0);
                        } catch (Exception ignored) {

                        }
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("id", lesson.getInt("id"));
                        jsonObject1.put("cancelled", lesson.getBoolean("cancelled"));
                        jsonObject1.put("modified", lesson.getBoolean("modified"));
                        jsonObject1.put("subject", subject);
                        jsonObject1.put("location", location);
                        jsonObject1.put("changeDescription", lesson.getString("changeDescription"));
                        jsonObject1.put("startTimeSlot", lesson.getInt("startTimeSlot"));
                        jsonArrayDay.put(jsonObject1);
                        Calendar calStart = Calendar.getInstance();
                        Calendar calEnd = Calendar.getInstance();
                        calStart.setTimeInMillis(lesson.getLong("start") * 1000);
                        Date date = new Date();
                        Date date2 = new Date();
                        date.setTime(lesson.getLong("end") * 1000);
                        date2.setTime(lesson.getLong("start") * 1000);
                        calEnd.setTime(date);
                        calStart.setTime(date2);
                        Log.i("dag", calStart.toString());
                        Log.i("aso8efihdj", calStart.get(Calendar.DAY_OF_WEEK) + "");
                        int color = Color.parseColor("#d3d3d3");
                        if (lesson.getBoolean("modified")){
                            color = Color.parseColor("#FF9800");
                        }
                        if (lesson.getBoolean("cancelled")){
                            color = Color.parseColor("#E57373");
                        }
                        TimeTableEvent item = new TimeTableEvent(lesson.getInt("id"), subject, location, calStart, calEnd, color);
                        events.add(item);
                    }
                    //lastHour = lesson.getInt("startTimeSlot");
                    //}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArrayDay;
    }

    @Override
    public void onEmptyViewLongPress(@NotNull Calendar calendar) {

    }

    @Override
    public void onEventClick(TimeTableEvent timeTableItem, @NotNull RectF rectF) {

    }

    @Override
    public void onEventLongPress(TimeTableEvent timeTableItem, @NotNull RectF rectF) {

    }

    @NotNull
    @Override
    public List<WeekViewDisplayable<TimeTableEvent>> onMonthChange(@NotNull Calendar calendar, @NotNull Calendar calendar1) {
        List<WeekViewDisplayable<TimeTableEvent>> e = new ArrayList<>();
        if (!eventsLoaded) {
            e = events;
            eventsLoaded = true;
        }
        return e;
    }

    private long getStartOfWeek(int week) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }

    private long getEndOfWeek(int week) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 7);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }
}
