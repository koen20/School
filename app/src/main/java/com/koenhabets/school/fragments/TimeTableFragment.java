package com.koenhabets.school.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.activities.TimeTableActivity;
import com.koenhabets.school.adapters.TimeTableAdapter;
import com.koenhabets.school.api.CalendarRequest;
import com.koenhabets.school.api.TimeTableItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TimeTableFragment extends Fragment {

    int currentDay;
    RequestQueue requestQueue;
    TextView textView5;
    ListView listView;
    String re;
    private TimeTableAdapter adapter;
    private List<TimeTableItem> timeTableItem = new ArrayList<>();

    public TimeTableFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
        textView5 = (TextView) rootView.findViewById(R.id.textView5);
        listView = (ListView) rootView.findViewById(R.id.listView2);

        adapter = new TimeTableAdapter(getContext(), timeTableItem);
        listView.setAdapter(adapter);

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        currentDay = now.get(Calendar.DAY_OF_MONTH);
        if (hour > 15) {
            currentDay += 1;
        }

        getCalendar();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getContext(), TimeTableActivity.class);
                intent.putExtra("subject", position);
                intent.putExtra("response", re);
                intent.putExtra("subject2", timeTableItem.get(position).getSubject().substring(3));
                intent.putExtra("date", timeTableItem.get(position).getDate());
                startActivity(intent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextDay();
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevDay();
            }
        });
        return rootView;
    }

    public void getCalendar() {
        requestQueue = Volley.newRequestQueue(getContext());
        final SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");
        Long tsLong = getStartOfDayInMillis() / 1000;
        final String ts = tsLong.toString();
        textView5.setText(getString(R.string.Currentday) + currentDay);
        Log.i("Timestamp", ts + "");
        String result = sharedPref.getString(ts, "no");
        if (!Objects.equals(result, "no")) {
            Log.i("Stored", result);
            re = result;
            ParseResponse(result);
        }
        CalendarRequest request = new CalendarRequest(requestToken, ts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ParseResponse(response);
                re = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String result = sharedPref.getString(ts, "no");
                if (!Objects.equals(result, "no")) {
                    Log.i("Stored", result);
                    re = result;
                    ParseResponse(result);
                }
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
        calendar.set(Calendar.DAY_OF_MONTH, currentDay);
        return calendar.getTimeInMillis();
    }

    public void nextDay() {
        currentDay++;
        if (currentDay > 31) {
            currentDay = 1;
        }
        getCalendar();
    }

    public void prevDay() {
        currentDay--;
        if (currentDay < 1) {
            currentDay = 31;
        }
        getCalendar();
    }

    public void ParseResponse(String response) {
        timeTableItem.clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonMain = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonMain.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                int uur = i + 1;
                String subject = uur + getString(R.string.Onbekend);
                String lokaal = "";
                String date = "";
                boolean homework = false;
                JSONObject vak = jsonArray.getJSONObject(i);
                try {
                    JSONObject todos = vak.getJSONObject("todos");
                    homework = !Objects.equals(todos.toString(), "");
                } catch (JSONException e) {

                }

                if (vak.has("title") && vak.has("subtitle")) {
                    subject = vak.getString("title");
                    lokaal = vak.getString("subtitle");
                    date = vak.getString("start");
                }
                TimeTableItem item = new TimeTableItem(subject, lokaal, homework, date);
                timeTableItem.add(item);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
