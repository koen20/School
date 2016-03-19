package com.koenhabets.school;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.api.NetpresenterRequest;

public class Main3Activity extends AppCompatActivity {
    TextView textView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        textView = (TextView) findViewById(R.id.textView4);

        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");


        NetpresenterRequest netpresenterRequest = new NetpresenterRequest(requestToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setText(Html.fromHtml(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(netpresenterRequest);
    }
}
