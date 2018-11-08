package com.koenhabets.school.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TimeTableFragment extends Fragment {

    private List<TimeTableItem> timeTableItem = new ArrayList<>();
    private TimeTableAdapter adapter;
    private ListView listView;
    private int day;
    private RequestQueue requestQueue;
    private long start;
    private long end;
    private TextView textView;

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

        textView = rootView.findViewById(R.id.textView5);

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
        String weekDayy;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        weekDayy = dayFormat.format(start * 1000);

        if (Objects.equals(weekDayy, "Friday")) {
            start = start + 86400 * 3;
            end = end + 86400 * 3;
        } else {
            start = start + 86400;
            end = end + 86400;
        }

        getCalendar(start, end);
    }

    private void prevDay() {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        weekDay = dayFormat.format(start * 1000);

        if (Objects.equals(weekDay, "Monday")) {
            start = start - 86400 * 3;
            end = end - 86400 * 3;
        } else {
            start = start - 86400;
            end = end - 86400;
        }
        getCalendar(start, end);
    }

    private void getCalendar(final long startTime, long endTime) {
        Date dateObj = new Date(startTime * 1000);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy EEEE", Locale.US);
        String dateString = df.format(dateObj);

        textView.setText(dateString);

        JSONObject jsonObject = readSchedule(getContext());
        try {
            JSONArray response = jsonObject.getJSONArray(Long.toString(startTime));
            proccessReponse(response);
        } catch (JSONException e) {
            timeTableItem.clear();
            adapter.notifyDataSetChanged();
        } catch (NullPointerException ignored) {
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String requestToken = sharedPref.getString("zermeloAccessToken", "no request token");
        String school = sharedPref.getString("school", "bernardinuscollege");
        AppointmentsRequest request = new AppointmentsRequest(requestToken, school, startTime, endTime, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                JSONArray jsonArray = parseResponse(response, startTime, getContext());
                proccessReponse(jsonArray);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errrooor", "jaaa");
            }
        });

        requestQueue.add(request);
    }

    public static JSONArray parseResponse(String response, long startTime, Context context) {
        JSONArray jsonArrayDay = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonResp = jsonObject.getJSONObject("response");
            JSONArray jsonArray = jsonResp.getJSONArray("data");

            int lastHour = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject lesson = jsonArray.getJSONObject(i);
                    if (lesson.getBoolean("valid")) {
                        if (lastHour != lesson.getInt("startTimeSlot")) {
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
                        }
                        lastHour = lesson.getInt("startTimeSlot");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        addDayToFile(startTime, jsonArrayDay, context);
        return jsonArrayDay;
    }

    private void proccessReponse(JSONArray jsonArray) {
        timeTableItem.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject lesson = jsonArray.getJSONObject(i);
                TimeTableItem item = new TimeTableItem(lesson.getString("subject"), lesson.getString("location"), lesson.getInt("startTimeSlot"),
                        lesson.getBoolean("cancelled"), lesson.getBoolean("modified"), lesson.getString("changeDescription"));
                timeTableItem.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Collections.sort(timeTableItem);
        adapter.notifyDataSetChanged();
    }

    public static void addDayToFile(long day, JSONArray appointments, Context context) {
        JSONObject jsonObject = readSchedule(context);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        try {
            jsonObject.put(Long.toString(day), appointments);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveSchedule(jsonObject, context);
    }

    public static void saveSchedule(JSONObject schedule, Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("appointments", Context.MODE_PRIVATE);
            outputStream.write(schedule.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readSchedule(Context context) {
        JSONObject jsonObject = null;
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    context.openFileInput("appointments")));
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
