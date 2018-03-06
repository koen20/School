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
import com.koenhabets.school.api.som.AccountRequest;
import com.koenhabets.school.api.som.SchoolRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextZermelo;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextSchool;
    private AutoCompleteTextView autoCompleteSom;

    private RequestQueue requestQueue;
    private boolean zermeloLogin;
    private boolean somLogin;
    private JSONArray jsonArraySchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(this);

        editTextZermelo = findViewById(R.id.editTextZermelo);
        editTextUsername = findViewById(R.id.editTextSomUsername);
        editTextPassword = findViewById(R.id.editTextSomPassword);
        editTextSchool = findViewById(R.id.editTextSchool);
        autoCompleteSom = findViewById(R.id.autoCompleteSom);

        SchoolRequest request = new SchoolRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    jsonArraySchool = new JSONArray(response).getJSONObject(0).getJSONArray("instellingen");
                    String[] arr = new String[jsonArraySchool.length()];
                    for (int i = 0; i < arr.length; i++) {
                        JSONObject item = jsonArraySchool.getJSONObject(i);
                        arr[i] = item.getString("naam");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item, arr);

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

        String uuid = "";
        for (int i = 0; i < jsonArraySchool.length(); i++) {
            try {
                JSONObject schoolSom = jsonArraySchool.getJSONObject(i);
                if(Objects.equals(schoolSom.getString("naam"), autoCompleteSom.getText().toString())){
                    uuid = schoolSom.getString("uuid");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AccessTokenRequest accessTokenRequest = new AccessTokenRequest(editTextUsername.getText().toString(),
                editTextPassword.getText().toString(), uuid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Logged-in", true);
                    editor.putString("somAccessToken", jsonObject.getString("access_token"));
                    editor.putString("somRefreshToken", jsonObject.getString("refresh_token"));
                    editor.putString("somApiUrl", jsonObject.getString("somtoday_api_url"));
                    editor.apply();
                    somLogin = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (somLogin && zermeloLogin) {
                    getAccountId();
                    Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                somLogin = false;
                Log.e("error", "" + error.getMessage());
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
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

    private void getAccountId(){
        SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        String somApiUrl =  sharedPref.getString("somApiUrl", "");
        String somAccessToken = sharedPref.getString("somAccessToken", "");

        AccountRequest request = new AccountRequest(somAccessToken, somApiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("persoon").getJSONArray("links").getJSONObject(0);
                    editor.putString("somId", jsonObject.getString("id"));
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }
}
