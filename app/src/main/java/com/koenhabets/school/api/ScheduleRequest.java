package com.koenhabets.school.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.koenhabets.school.ScheduleItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by koen on 23-1-16.
 */
public class ScheduleRequest extends Request<List<ScheduleItem>> {

    private static final String BASE_URL = "https://api.scholica.com/2.0/communities/1/" +
            "calendar/schedule?token=%s&time=%s";
    private static final String TOKEN = "8c6e2b829353908b5f3abd40e375a8ace4f2";

    private Response.Listener<List<ScheduleItem>> listener;

    private static String getUrl(String token, String timeStamp) {
        return String.format(BASE_URL, token, timeStamp);
    }

    public ScheduleRequest(String timeStamp,
                           Response.Listener<List<ScheduleItem>> listener,
                           Response.ErrorListener errorListener) {
        super(Method.POST, getUrl(TOKEN, timeStamp), errorListener);
        this.listener = listener;
    }

    private List<ScheduleItem> parseJson(String result) throws JSONException {
        List<ScheduleItem> items = new ArrayList<>();

        JSONObject response = new JSONObject(result);
        JSONObject jsonMain = response.getJSONObject("result");

        JSONArray jsonArray = jsonMain.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject vak = jsonArray.getJSONObject(i);
            String title = vak.getString("title");
            String lokaal = vak.getString("subtitle");
            items.add(new ScheduleItem(title, lokaal));
        }

        return items;

    }


    @Override
    protected Response<List<ScheduleItem>> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, "UTF-8");

            List<ScheduleItem> scheduleItems = parseJson(data);
            return Response.success(scheduleItems, null);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(List<ScheduleItem> response) {
        listener.onResponse(response);
    }
}
