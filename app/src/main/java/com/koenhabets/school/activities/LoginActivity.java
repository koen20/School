package com.koenhabets.school.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.SchoolApp;
import com.koenhabets.school.api.TokenRequest;
import com.koenhabets.school.api.som.AccessTokenRequest;
import com.koenhabets.school.api.som.SchoolRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextZermelo;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextSchool;
    private AutoCompleteTextView autoCompleteSom;

    private RequestQueue requestQueue;
    private boolean zermeloLogin;
    private boolean somLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(this);

        editTextZermelo = findViewById(R.id.editTextZermelo);
        editTextUsername = findViewById(R.id.editTextSomUsername);
        editTextPassword = findViewById(R.id.editTextSomPassword);
        editTextSchool = findViewById(R.id.editTextSchool);
        autoCompleteSom = findViewById(R.id.autoCompleteTextView);

        SchoolRequest request = new SchoolRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONArray jsonArray = new JSONArray(response).getJSONObject(0).getJSONArray("instellingen");
                    String[] arr = new String[jsonArray.length()];
                    for (int i = 0; i < arr.length; i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        arr[i] = item.getString("naam");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (this, android.R.layout.select_dialog_item, arr);

                    autoCompleteSom.setThreshold(2);
                    autoCompleteSom.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errrooor", "jaaa");
            }
        });

        requestQueue.add(request);
    }

    public void login(View view) {
        final String school = editTextSchool.getText().toString();
        TokenRequest tokenRequest = new TokenRequest(editTextZermelo.getText().toString(), school, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                zermeloLogin = true;
                SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("Logged-in", true);
                editor.putString("zermeloAccessToken", response);
                editor.putString("school", school);
                editor.apply();
                if (somLogin && zermeloLogin) {
                    Intent intent = new Intent(SchoolApp.getContext(), DrawerActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                zermeloLogin = false;
                Log.e("error", "" + error.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle(getString(R.string.login_failed));
                alertDialog.setMessage(getString(R.string.incorrect_zermelo));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
            }
        });
        requestQueue.add(tokenRequest);

        AccessTokenRequest accessTokenRequest = new AccessTokenRequest(editTextUsername.getText().toString(),
                editTextPassword.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Logged-in", true);
                    editor.putString("somAccessToken", jsonObject.getString("access_token"));
                    editor.putString("somRefreshToken", jsonObject.getString("refresh_token"));
                    editor.apply();
                    somLogin = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (somLogin && zermeloLogin) {
                    Intent intent = new Intent(SchoolApp.getContext(), DrawerActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                somLogin = false;
                Log.e("error", "" + error.getMessage());
                SharedPreferences sharedPref = SchoolApp.getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle(getString(R.string.login_failed));
                alertDialog.setMessage(getString(R.string.incorrect_som));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        requestQueue.add(accessTokenRequest);
    }
}
