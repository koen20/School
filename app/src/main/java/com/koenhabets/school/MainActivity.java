package com.koenhabets.school;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String access_token = "470d7d90cae6e34f36bc9110026a4370e8864551b0e7e7b33263163562c362a3d68f1937";
    String username = "407332";
    String password = "---";

    private TextView textView;
    private TextView textView2;
    RequestQueue requestQueue;
    public String requestToken;
    int currentDay;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

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

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.e("error", error.getMessage());
        }
    };

    public long getStartOfDayInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, currentDay);
        return calendar.getTimeInMillis();
    }

    public void PJson(String result) {
        textView.setText("");
        textView2.setText("Current day: " + currentDay);
        Log.i("PJsonResult", result);
        Long tsLong = getStartOfDayInMillis() / 1000;
        final String ts = tsLong.toString();
        try {
            JSONObject response = new JSONObject(result);
            JSONObject jsonMain = response.getJSONObject("result");
            SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString(ts, response.toString());
            editor.apply();
            JSONArray jsonArray = jsonMain.getJSONArray("items");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.ic_stat_action_list);
            mBuilder.setContentTitle("Rooster");
            mBuilder.setOngoing(true);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            for (int i = 0; i < jsonArray.length(); i++) {
                int uur = i + 1;
                String title = uur + ". Onbekend";
                String lokaal = "";
                JSONObject vak = jsonArray.getJSONObject(i);
                try{
                    title = vak.getString("title");
                    lokaal = vak.getString("subtitle");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                textView.append(title + " " + lokaal + "\n");
                inboxStyle.addLine(title + " " + lokaal);
            }
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.setStyle(inboxStyle);
            mNotificationManager.notify(1, mBuilder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCalendar(){
        Long tsLong = getStartOfDayInMillis() / 1000;
        final String ts = tsLong.toString();
        String url = "https://api.scholica.com/2.0/communities/1/calendar/schedule";
        Log.i("Timestamp", ts + "");
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        PJson(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        Log.e("error", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("token", requestToken);
                params.put("time", ts);
                return params;
            }
        };
        SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String result = sharedPref.getString(ts, "no");
        if (!Objects.equals(result, "no")) {
            Log.i("Stored", result);
            PJson(result);
        } else {
            requestQueue.add(postRequest);
        }
    }

    public void getToken(){
        String url = "https://api.scholica.com/2.0/communities/1/authenticate";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try{
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
    public void startAlarm(){
        Log.i("Alarm", "set");
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }
}