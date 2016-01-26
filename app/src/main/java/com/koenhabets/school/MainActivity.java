package com.koenhabets.school;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.api.ScheduleRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    RequestQueue requestQueue;
    public int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.textView);

        Calendar cal = Calendar.getInstance();
        currentDay = cal.get(Calendar.DAY_OF_MONTH);
        Log.i("a", System.currentTimeMillis() / 1000 + "");
        Log.i("b", getStartOfDayInMillis() / 1000 + "");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refresh();

            }
        });
    }

    private void refresh() {
        textView.setText("");
        Long tsLong = getStartOfDayInMillis() / 1000;
        String ts = tsLong.toString();
        ScheduleRequest request = new ScheduleRequest(ts,
                new Response.Listener<List<ScheduleItem>>() {
                    @Override
                    public void onResponse(List<ScheduleItem> response) {
                        String text = "";
                        for (ScheduleItem item : response) {
                            text += item.toString();
                        }
                        textView.setText(text);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        Log.e("error", error.getMessage());
                    }
                }
        );
        tsLong = getStartOfDayInMillis() / 1000;
        ts = tsLong.toString();
        Log.i("timestamp", ts);
        requestQueue.add(request);
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

    public void dayNext(View view) {
        currentDay++;
        Log.i("Currentday", currentDay + "");
        textView.append(currentDay + "\n");
    }

    public void dayPrev(View view) {
        currentDay--;
        Log.i("Currentday", currentDay + "");
        textView.append(currentDay + "\n");
    }
}
