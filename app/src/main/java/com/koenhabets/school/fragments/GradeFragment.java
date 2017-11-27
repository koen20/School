package com.koenhabets.school.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.adapters.GradesAdapter;
import com.koenhabets.school.api.som.GradeItem;
import com.koenhabets.school.api.som.GradesRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GradeFragment extends Fragment {
    RequestQueue requestQueue;
    private List<GradeItem> gradeItems = new ArrayList<>();
    ListView listView;
    GradesAdapter adapter;

    public GradeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grade, container, false);

        requestQueue = Volley.newRequestQueue(getContext());
        listView = rootView.findViewById(R.id.listViewGrades);
        adapter = new GradesAdapter(getContext(), gradeItems);
        listView.setAdapter(adapter);

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

    private void parseResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                JSONObject subjectJson = item.getJSONObject("vak");
                double grade = 0;
                try {
                    grade = item.getDouble("resultaat");
                } catch (Exception ignored) {
                }
                String subject = subjectJson.getString("naam");
                String datum = item.getString("datumInvoer");
                int periode = item.getInt("periode");
                String type = item.getString("type");
                //if (Objects.equals(type, "Toetskolom")) {
                    GradeItem gradeItem = new GradeItem(grade, subject + type, datum, 0, periode, type);
                    gradeItems.add(gradeItem);
                //}
            }
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
