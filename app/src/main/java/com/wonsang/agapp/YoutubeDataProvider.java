package com.wonsang.agapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wonsang.agapp.fragment.YoutubeFragment;
import com.wonsang.agapp.model.YoutubeData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YoutubeDataProvider {

    private final String apiKey;
    private final RequestQueue requestQueue;
    private final YoutubeFragment youtubeFragment;
    private StringBuilder urlBuilder;

    public YoutubeDataProvider(String requestUrl, String key, Context context, YoutubeFragment fragment) {
        this.apiKey = key;
        this.requestQueue = Volley.newRequestQueue(context);
        this.urlBuilder = createBuilder(requestUrl);
        this.youtubeFragment = fragment;
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

        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new YoutubeDataResponseListener(youtubeFragment), new ErrorListener());
        requestQueue.add(request);
        return youtubeData;
    }

    static class YoutubeDataResponseListener implements Response.Listener<JSONObject> {
        private final YoutubeFragment youtubeFragment;

        YoutubeDataResponseListener(YoutubeFragment youtubeFragment){
            this.youtubeFragment = youtubeFragment;
        }

        @Override
        public void onResponse(JSONObject response){
            List<YoutubeData> data = new ArrayList<>();
            try {
                JSONArray jsonArray = response.getJSONArray("items");
                for(int i = 0; i < jsonArray.length(); i++){
                    YoutubeData.Builder builder = new YoutubeData.Builder();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    builder.videoId(jsonObject.getJSONObject("id").getString("videoId"));

                    JSONObject snippet = jsonObject.getJSONObject("snippet");
                    builder.publishedAt(snippet.getString("publishedAt"));
                    builder.channelTitle(snippet.getString("channelTitle"));
                    builder.title(snippet.getString("title"));
                    builder.description(snippet.getString("description"));
                    builder.previewImageUrl(snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url"));
                    data.add(builder.build());
                }
                youtubeFragment.setYoutubeDataFetch(data);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    static class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(error);
        }
    }

}