package com.koenhabets.school.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.api.som.GradeItem;
import com.koenhabets.school.api.som.GradesRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GradeFragment extends Fragment {
    RequestQueue requestQueue;
    private List<GradeItem> gradeItems = new ArrayList<>();


    public GradeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_homework, container, false);

        requestQueue = Volley.newRequestQueue(getContext());

        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);

        GradesRequest request = new GradesRequest(sharedPref.getString("somAccessToken", ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                parseResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);

        return rootView;
    }

    private void parseResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
