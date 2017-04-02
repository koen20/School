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
import com.koenhabets.school.api.GradeItem;
import com.koenhabets.school.api.GradesRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GradesFragment extends Fragment {
    static String[] subjects = {
            "Aardrijkskunde", "Duitse taal", "Economie", "Engelse taal", "Franse taal",
            "Geschiedenis", "Levensbeschouwing", "Muziek", "Nederlandse taal", "Scheikunde",
            "Wiskunde", "Natuurkunde", "Biologie", "Lichamelijke opvoeding", "Beeldende vorming",
            "Informatica", "Maatschappijleer", "Nederlandse taal en literatuur", "Wiskunde B",
            "Wiskunde A", "Engelse taal en literatuur", "Culturele en kunstzinnige vorming"};
    String result;
    private RequestQueue requestQueue;
    private ListView listView;
    private GradesAdapter adapter;
    private List<GradeItem> gradeItems = new ArrayList<>();

    public GradesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grades, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        requestQueue = Volley.newRequestQueue(getContext());
        adapter = new GradesAdapter(getContext(), gradeItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.textView_subject);
                String subject = textView.getText() + "";
                Intent intent = new Intent(getContext(), GradesActivity.class);
                intent.putExtra("subject", subject);
                intent.putExtra("response", result);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");

        GradesRequest request = new GradesRequest(requestToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("grades", response);
                result = response;
                try {
                    parseResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(request);
        return rootView;
    }

    public void parseResponse(String response) throws JSONException {
        gradeItems.clear();
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonMain = jsonObject.getJSONObject("grades");
        for (String subject : subjects) {
            try {
                JSONObject vak = jsonMain.getJSONObject(subject);
                double avg = vak.getDouble("avg");
                GradeItem item = new GradeItem(subject, avg);
                gradeItems.add(item);
            } catch (JSONException e) {
                //e.printStackTrace();
            }

        }

        GradeItem item = new GradeItem("Verliespunten", jsonObject.getDouble("loose"));
        gradeItems.add(item);
        GradeItem itemAvg = new GradeItem("Gemiddelde", jsonObject.getDouble("average"));
        gradeItems.add(itemAvg);
        adapter.notifyDataSetChanged();
    }
}
