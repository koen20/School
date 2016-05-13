package com.koenhabets.school.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.SchoolApp;
import com.koenhabets.school.api.CalendarRequest;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Objects;

public class TimeTableFragment extends Fragment {

    private TextView textViewTimeTable;
    int currentDay;
    RequestQueue requestQueue;
    TextView textView5;

    public TimeTableFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);

        textViewTimeTable = (TextView) rootView.findViewById(R.id.textViewTimeTable);
        textView5 = (TextView) rootView.findViewById(R.id.textView5);
        Button button5 = (Button) rootView.findViewById(R.id.button5);
        Button button6 = (Button) rootView.findViewById(R.id.button6);

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        currentDay = now.get(Calendar.DAY_OF_MONTH);
        if (hour > 15) {
            currentDay += 1;
        }

        getCalendar();

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevDay();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextDay();
            }
        });

        return rootView;
    }

    public void getCalendar() {
        requestQueue = Volley.newRequestQueue(SchoolApp.getContext());
        SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");
        Long tsLong = getStartOfDayInMillis() / 1000;
        final String ts = tsLong.toString();
        textView5.setText(getString(R.string.Currentday) + currentDay);
        Log.i("Timestamp", ts + "");
        CalendarRequest request = new CalendarRequest(requestToken, ts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                textViewTimeTable.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        String result = sharedPref.getString(ts, "no");
        if (!Objects.equals(result, "no")) {
            Log.i("Stored", result);
            String resultString = null;
            try {
                resultString = CalendarRequest.parseResponse(result, ts);
                textViewTimeTable.setText(resultString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            requestQueue.add(request);
        }
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
}
