package com.koenhabets.school.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.adapters.TimeTableAdapter;
import com.koenhabets.school.api.AppointmentsRequest;
import com.koenhabets.school.api.TimeTableItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeTableFragment extends Fragment {

    private List<TimeTableItem> timeTableItem = new ArrayList<>();
    private TimeTableAdapter adapter;
    ListView listView;
    int day;
    RequestQueue requestQueue;
    long start;
    long end;

    public TimeTableFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
        listView = rootView.findViewById(R.id.listView2);
        requestQueue = Volley.newRequestQueue(getContext());

        adapter = new TimeTableAdapter(getContext(), timeTableItem);
        listView.setAdapter(adapter);

        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);

        getCalendar(getStartOfDay(day), getEndOfDay(day));

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextDay();
            }
        });
        FloatingActionButton fab2 = rootView.findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevDay();
            }
        });
        return rootView;
    }

    private long getStartOfDay(int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, d);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        start = cal.getTimeInMillis() / 1000;
        return cal.getTimeInMillis() / 1000;
    }

    private long getEndOfDay(int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, d);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        end = cal.getTimeInMillis() / 1000;
        return cal.getTimeInMillis() / 1000;
    }

    private void nextDay() {
        start = start + 86400;
        end = end + 86400;
        getCalendar(start, end);
    }

    private void prevDay() {
        start = start - 86400;
        end = end - 86400;
        getCalendar(start, end);
    }

    public void getCalendar(final long startTime, long endTime) {
        JSONObject jsonObject = readSchedule();
        try {
            String response = jsonObject.getString(Long.toString(startTime));
            parseResponse(response);
        } catch (JSONException e) {
            timeTableItem.clear();
            adapter.notifyDataSetChanged();
        } catch (NullPointerException ignored) {
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String requestToken = sharedPref.getString("zermeloAccessToken", "no request token");
        AppointmentsRequest request = new AppointmentsRequest(requestToken, startTime, endTime, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                parseResponse(response);
                addDayToFile(startTime, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errrooor", "jaaa");
            }
        });

        requestQueue.add(request);
    }

    private void parseResponse(String response) {
        timeTableItem.clear();
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
                            if (lastHour != lesson.getInt("startTimeSlot")) {
                                JSONArray subjects = lesson.getJSONArray("subjects");
                                JSONArray locations = lesson.getJSONArray("locations");
                                TimeTableItem item = new TimeTableItem(subjects.getString(0), locations.getInt(0), lesson.getInt("startTimeSlot"), lesson.getBoolean("cancelled"));
                                timeTableItem.add(item);
                            }
                            lastHour = lesson.getInt("startTimeSlot");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    private void addDayToFile(long day, String dayString) {
        JSONObject jsonObject = readSchedule();
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        try {
            jsonObject.put(Long.toString(day), dayString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveSchedule(jsonObject);
    }

    private void saveSchedule(JSONObject schedule) {
        FileOutputStream outputStream;
        try {
            outputStream = getContext().openFileOutput("appointment", Context.MODE_PRIVATE);
            outputStream.write(schedule.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject readSchedule() {
        JSONObject jsonObject = null;
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    getContext().openFileInput("appointment")));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();
            while ((inputString = inputReader.readLine()) != null) {
                stringBuffer.append(inputString + "\n");
            }
            try {
                jsonObject = new JSONObject(stringBuffer.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {
        }
        if (jsonObject != null) {
            Log.i("read", jsonObject.toString());
        }
        return jsonObject;
    }
}
