package com.koenhabets.school.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.AbsenceComparator;
import com.koenhabets.school.HomeworkComparator;
import com.koenhabets.school.R;
import com.koenhabets.school.adapters.AbsenceAdapter;
import com.koenhabets.school.api.som.AbsenceItem;
import com.koenhabets.school.api.som.AbsentieRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;

public class AbsenceFragment extends Fragment {
    private List<AbsenceItem> absenceItems = new ArrayList<>();
    private ListView listView;
    private RequestQueue requestQueue;
    private AbsenceAdapter adapter;

    public AbsenceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.absence_fragment, container, false);

        listView = rootView.findViewById(R.id.listViewAbsence);
        requestQueue = Volley.newRequestQueue(getContext());

        adapter = new AbsenceAdapter(getContext(), absenceItems);
        listView.setAdapter(adapter);
        getAbsence();
        return rootView;
    }

    private void getAbsence() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String somApiUrl = sharedPref.getString("somApiUrl", "");
        String somAccessToken = sharedPref.getString("somAccessToken", "");

        AbsentieRequest request = new AbsentieRequest(somAccessToken, somApiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            try {
                JSONObject absenceObject = jsonArray.getJSONObject(i);
                int beginLesuur = 0;
                if (absenceObject.has("beginLesuur")){
                    beginLesuur = absenceObject.getInt("beginLesuur");
                }
                int eindLesuur = 0;
                if (absenceObject.has("eindLesuur")){
                    eindLesuur = absenceObject.getInt("eindLesuur");
                }
                String eigenaar = "";
                if (absenceObject.getJSONObject("eigenaar").has("afkorting")){
                    eigenaar = absenceObject.getJSONObject("eigenaar").getString("afkorting");
                }
                String opmerkingen = "";
                if (absenceObject.has("opmerkingen")){
                    opmerkingen = absenceObject.getString("opmerkingen");
                }
                boolean geoorloofd = false;
                if (absenceObject.getJSONObject("absentieReden").has("geoorloofd")){
                    geoorloofd = absenceObject.getJSONObject("absentieReden").getBoolean("geoorloofd");
                }
                String omschrijving = "";
                if (absenceObject.getJSONObject("absentieReden").has("omschrijving")){
                    omschrijving = absenceObject.getJSONObject("absentieReden").getString("omschrijving");
                }
                String beginDatumTijd = "";
                if (absenceObject.has("beginDatumTijd")){
                    beginDatumTijd = absenceObject.getString("beginDatumTijd");
                }
                AbsenceItem absenceItem = new AbsenceItem(omschrijving, beginLesuur, eindLesuur, geoorloofd, opmerkingen, eigenaar, beginDatumTijd);
                absenceItems.add(absenceItem);
            } catch (JSONException ignored) {

            }
        }
        AbsenceComparator absenceComparator = new AbsenceComparator();
        Collections.sort(absenceItems, absenceComparator);
        adapter.notifyDataSetChanged();
    }
}
