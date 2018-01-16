package com.koenhabets.school.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.activities.GradesActivity;
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
    JSONArray jsonArraySubjects = new JSONArray();

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView textView = view.findViewById(R.id.textViewGradeSubject);
                String subject = textView.getText().toString();
                JSONObject jsonObject1 = new JSONObject();
                for (int i = 0; i < jsonArraySubjects.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArraySubjects.getJSONObject(i);
                        String sub = jsonObject.getString("subject");
                        if(Objects.equals(subject, sub)){
                            jsonObject1 = jsonObject;
                        }
                    } catch (JSONException ignored){

                    }
                }
                Intent intent = new Intent(getContext(), GradesActivity.class);
                intent.putExtra("jsonObject", jsonObject1.toString());
                startActivity(intent);
            }
        });

        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        GradesRequest request = new GradesRequest(sharedPref.getString("somAccessToken", ""), "0-100", new Response.Listener<String>() {
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
        GradesRequest request2 = new GradesRequest(sharedPref.getString("somAccessToken", ""), "101-200", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                parseResponse(response);
                try {
                    for (int d = 0; d < jsonArraySubjects.length(); d++) {
                        JSONObject jsonObject1 = jsonArraySubjects.getJSONObject(d);
                        String subject = jsonObject1.getString("subject");
                        String grade = jsonObject1.getString("grade");
                        GradeItem gradeItem = new GradeItem(grade, subject, "", 0, 1, "", "");
                        gradeItems.add(gradeItem);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException ignored) {
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request2);

        return rootView;
    }

    private void parseResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                JSONObject subjectJson = item.getJSONObject("vak");
                String grade = "";
                int weight = 0;
                String description = "";
                try {
                    grade = item.getString("resultaat");
                } catch (Exception ignored) {
                }
                try {
                    description = item.getString("omschrijving");
                } catch (Exception ignored) {
                }
                String subject = subjectJson.getString("naam");
                String datum = item.getString("datumInvoer");
                int periode = item.getInt("periode");
                String type = item.getString("type");
                try {
                    weight = item.getInt("weging");
                } catch (Exception ignored) {
                }
                if (Objects.equals(type, "RapportGemiddeldeKolom")) {
                    if (!checkSubject(subject, jsonArraySubjects)) {
                        if(periode == 4) {
                            JSONArray jsonArray1 = new JSONArray();
                            JSONObject subjectItem = new JSONObject();//todo improve grades
                            subjectItem.put("subject", subject);
                            subjectItem.put("grade", grade);
                            subjectItem.put("subjectGrades", jsonArray1);
                            jsonArraySubjects.put(subjectItem);
                        }
                    }
                } else if (Objects.equals(type, "Toetskolom")) {
                    for (int k = 0; k < jsonArraySubjects.length(); k++) {
                        JSONObject jsonObject1 = jsonArraySubjects.getJSONObject(k);
                        String sub = jsonObject1.getString("subject");
                        if (Objects.equals(sub, subject)) {
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("subjectGrades");
                            JSONObject gradeItem = new JSONObject();
                            gradeItem.put("subject", subject);
                            gradeItem.put("periode", periode);
                            gradeItem.put("grade", grade);
                            gradeItem.put("description", description);
                            gradeItem.put("datumInvoer", datum);
                            gradeItem.put("weight", weight);
                            gradeItem.put("type", type);
                            jsonArray1.put(gradeItem);
                        }
                    }

                }
            }
            Log.i("subjects", jsonArraySubjects.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkSubject(String subject, JSONArray jsonArray) {
        boolean exists = false;
        for (int d = 0; d < jsonArray.length(); d++) {
            try {
                JSONObject jsonObject1 = jsonArray.getJSONObject(d);
                String sub = jsonObject1.getString("subject");
                if (Objects.equals(subject.trim(), sub.trim())) {
                    exists = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return exists;
    }
}
