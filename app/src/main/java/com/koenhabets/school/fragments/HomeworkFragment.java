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
import com.koenhabets.school.adapters.HomeworkAdapter;
import com.koenhabets.school.api.som.HomeworkItem;
import com.koenhabets.school.api.som.HomeworkRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeworkFragment extends Fragment {
    private List<HomeworkItem> homeworkItems = new ArrayList<>();
    ListView listView;
    RequestQueue requestQueue;
    HomeworkAdapter adapter;

    public HomeworkFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_homework, container, false);

        listView = rootView.findViewById(R.id.listViewHomework);
        requestQueue = Volley.newRequestQueue(getContext());

        adapter = new HomeworkAdapter(getContext(), homeworkItems);
        listView.setAdapter(adapter);

        getHomework();
        return rootView;
    }

    private void getHomework() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);

        HomeworkRequest request = new HomeworkRequest(sharedPref.getString("somAccessToken", ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    parseResponse(response);
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

    private void parseResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject task = jsonArray.getJSONObject(i);
            JSONObject subjectJson = task.getJSONObject("vak");
            JSONObject studiewijzerItem = task.getJSONObject("studiewijzerItem");

            String subject = subjectJson.getString("afkorting");
            String date = task.getString("datumTijd");
            String description = "";
            try {
                description = studiewijzerItem.getString("opdrachtBeschrijving");
            } catch (JSONException ignored){
            }
            String taskSubject = studiewijzerItem.getString("onderwerp");
            HomeworkItem item = new HomeworkItem(date, subject, description, taskSubject);
            homeworkItems.add(item);
        }
        adapter.notifyDataSetChanged();
    }
}
