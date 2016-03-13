package com.koenhabets.school;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.api.CalendarRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public String requestToken;
    String access_token = "470d7d90cae6e34f36bc9110026a4370e8864551b0e7e7b33263163562c362a3d68f1937";
    String username = "407332";
    String password = "---";
    RequestQueue requestQueue;
    int currentDay;
    private TextView textView;
    private TextView textView2;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.e("error", error.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        Calendar cal = Calendar.getInstance();
        currentDay = cal.get(Calendar.DAY_OF_MONTH);
        getToken();
        setSupportActionBar(toolbar);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        startAlarm();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCalendar();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void getCalendar() {
        Long tsLong = getStartOfDayInMillis() / 1000;
        final String ts = tsLong.toString();
        Log.i("Timestamp", ts + "");
        CalendarRequest request = new CalendarRequest(requestToken, ts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView2.setText("Current day: " + currentDay);
                textView.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                Log.e("error", error.getMessage());
            }
        });

        SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String result = sharedPref.getString(ts, "no");
        if (!Objects.equals(result, "no")) {
            Log.i("Stored", result);
            String resultString = null;
            try {
                resultString = CalendarRequest.parseResponse(result, ts);
                textView.setText(resultString);
                textView2.setText("Current day: " + currentDay);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            requestQueue.add(request);
        }
    }

    public void getToken() {
        String url = "https://api.scholica.com/2.0/communities/1/authenticate";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject responsetoken = new JSONObject(response);
                            JSONObject jsonMain = responsetoken.getJSONObject("result");
                            requestToken = jsonMain.getString("request_token");
                            Log.i("requestToken", requestToken);
                            getCalendar();

                            SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("request_token", requestToken);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("access_token", access_token);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    public void nextDay(View view) {
        currentDay++;
        getCalendar();
    }

    public void prevDay(View view) {
        currentDay--;
        getCalendar();
    }

    public void startAlarm() {
        Log.i("Alarm", "set");
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }
}