package com.wonsang.agapp;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wonsang.agapp.model.YoutubeData;

import java.util.ArrayList;
import java.util.List;

public class YoutubeDataProvider {

    private final String apiKey;
    private final RequestQueue requestQueue;
    private StringBuilder urlBuilder;

    public YoutubeDataProvider(String requestUrl, String key, Context context) {
        this.apiKey = key;
        this.requestQueue = Volley.newRequestQueue(context);
        this.urlBuilder = createBuilder(requestUrl);
    }

    private StringBuilder createBuilder(String requestUrl) {
        StringBuilder builder = new StringBuilder(requestUrl);
        builder.append("key=" + apiKey)
                .append("&part=snippet")
                .append("&regionCode=KR")
                .append("&type=video");
        return builder;
    }

    public List<YoutubeData> getDataByTitle(String query) {
        List<YoutubeData> youtubeData = new ArrayList<>();
        StringBuilder uri = new StringBuilder(urlBuilder);
        uri.append("&q=" + query);

        StringRequest request = new StringRequest(Request.Method.GET, uri.toString() , new YoutubeDataResponseListener(), new ErrorListener());

        requestQueue.add(request);
        return youtubeData;
    }

    static class YoutubeDataResponseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {

        }
    }

    static class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(error);
        }
    }

}