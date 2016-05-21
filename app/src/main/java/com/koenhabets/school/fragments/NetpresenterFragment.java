package com.koenhabets.school.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.SchoolApp;
import com.koenhabets.school.api.NetpresenterRequest;

public class NetpresenterFragment extends Fragment {
    RequestQueue requestQueue;
    TextView textView;

    public NetpresenterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_netpresenter, container, false);

        textView = (TextView) rootView.findViewById(R.id.textViewNetpresenter);
        requestQueue = Volley.newRequestQueue(getContext());

        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");

        NetpresenterRequest netpresenterRequest = new NetpresenterRequest(requestToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setText(Html.fromHtml(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(netpresenterRequest);

        return rootView;
    }
}