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
    EditText editText_username;
    EditText editText_password;
    EditText editText_class;
    TextView textView;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(this);
        editText_username = (EditText) findViewById(R.id.editText2);
        editText_password = (EditText) findViewById(R.id.editText);
        editText_class = (EditText) findViewById(R.id.editText3);
        textView = (TextView) findViewById(R.id.textView6);

    }
    public void login(View view){
        SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", editText_username.getText().toString());
        editor.putString("password", editText_password.getText().toString());
        editor.putString("class", editText_class.getText().toString());
        editor.apply();
        TokenRequest tokenRequest = new TokenRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //if (response == "200"){
                    SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Logged-in", true);
                    editor.commit();
                    Intent intent = new Intent(SchoolApp.getContext(), DrawerActivity.class);
                    startActivity(intent);
                /*} else {
                    textView.setText(R.string.incorrect);
                    SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.commit();
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(tokenRequest);

    }
}
