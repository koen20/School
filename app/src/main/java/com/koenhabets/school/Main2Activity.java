package com.koenhabets.school;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.api.CalendarRequest;
import com.koenhabets.school.api.GradesRequest;

public class Main2Activity extends AppCompatActivity {
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");

        GradesRequest request = new GradesRequest(requestToken, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("asdf", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(request);
    }
}
