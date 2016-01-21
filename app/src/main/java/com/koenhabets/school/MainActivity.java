package com.koenhabets.school;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.textView);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long tsLong = System.currentTimeMillis()/1000;
                final String ts = tsLong.toString();
                Calendar now = Calendar.getInstance();
                final int date = now.get(Calendar.DATE);
                //date = date.toString();
                Log.i("date", date + "");
                textView.setText("");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://api.scholica.com/2.0/communities/1/calendar/schedule?token=01cbc2c77d08f40a5885a26e5b11f658b093&timestamp=" + ts,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("d", response + "d");
                                    JSONObject jsonMain = response.getJSONObject("result");
                                    String timestamp = jsonMain.getString("timestamp");

                                    SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();

                                    editor.putString(date + "", response.toString());
                                    editor.commit();

                                    JSONArray jsonArray = jsonMain.getJSONArray("items");
                                    for(int i = 0 ; i <jsonArray.length();i++ ) {
                                        JSONObject vak = jsonArray.getJSONObject(i);
                                        String title = vak.getString("title");
                                        String lokaal = vak.getString("subtitle");
                                        textView.append(title + " " + lokaal + "\n");
                                    }


                                } catch (JSONException e) {
                                        e.printStackTrace();
                                }
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
                SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                String result = sharedPref.getString(date + "", "no");
                Log.i("result", result);
                if (result != "no") {
                    Log.i("Stored", result);
                    try {
                        JSONObject response = new JSONObject(result);
                        JSONObject jsonMain = response.getJSONObject("result");
                        String timestamp = jsonMain.getString("timestamp");

                        JSONArray jsonArray = jsonMain.getJSONArray("items");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject vak = jsonArray.getJSONObject(i);
                            String title = vak.getString("title");
                            String lokaal = vak.getString("subtitle");
                            textView.append(title + " " + lokaal + "\n");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    requestQueue.add(jsonObjectRequest);
                }
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
}
