package com.koenhabets.school.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.SchoolApp;
import com.koenhabets.school.api.TokenRequest;

public class LoginActivity extends AppCompatActivity {
    EditText editText_zermelo;

    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(this);

        editText_zermelo = findViewById(R.id.editTextZermelo);
    }
    public void login(View view){

        TokenRequest tokenRequest = new TokenRequest(editText_zermelo.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("Logged-in", true);
                editor.putString("zermeloAccessToken", response);
                editor.apply();
                Intent intent = new Intent(SchoolApp.getContext(), DrawerActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());//// TODO: 8/31/2017 add message for user
                SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
            }
        });
        requestQueue.add(tokenRequest);

    }
}
