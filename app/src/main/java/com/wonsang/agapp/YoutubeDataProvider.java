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

    private static final String channelRequestUrl = "https://www.googleapis.com/youtube/v3/channels?";
    private static final String searchRequestUrl = "https://www.googleapis.com/youtube/v3/search?";

    public YoutubeDataProvider(String key, Context context, YoutubeFragment fragment) {
        this.apiKey = key;
        this.requestQueue = Volley.newRequestQueue(context);
        this.youtubeFragment = fragment;
    }

    private StringBuilder createUrlBuilder(String requestUrl) {
        StringBuilder builder = new StringBuilder(requestUrl);
        builder.append("key=" + apiKey)
                .append("&part=snippet")
                .append("&regionCode=KR");
        return builder;
    }


    public List<YoutubeData> getDataByTitle(String query) {
        List<YoutubeData> youtubeData = new ArrayList<>();
        StringBuilder uri = createUrlBuilder(searchRequestUrl);
        uri.append("&q=" + query).append("&type=video");
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new YoutubeDataResponseListener(youtubeFragment,this), new ErrorListener());
        requestQueue.add(request);
        return youtubeData;
    }

    public List<YoutubeData> getAllChannelDataById(List<YoutubeData> youtubeData){
        youtubeData.forEach((data) -> {
            String channelId = data.getChannelId();
            StringBuilder uri = createUrlBuilder(channelRequestUrl);
            uri.append("&id=" + channelId);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new YoutubeChannelResponseListener(youtubeFragment, data), new ErrorListener());
            requestQueue.add(request);
        });
        return youtubeData;
    }
    static class YoutubeChannelResponseListener implements Response.Listener<JSONObject> {
        private YoutubeData youtubeData;
        private YoutubeFragment youtubeFragment;

        YoutubeChannelResponseListener(YoutubeFragment youtubeFragment, YoutubeData youtubeData) {
            this.youtubeFragment = youtubeFragment;
            this.youtubeData = youtubeData;
        }

        @Override
        public void onResponse(JSONObject response) {
            try {
                JSONArray items = response.getJSONArray("items");
                JSONObject snippet = items.getJSONObject(0).getJSONObject("snippet");
                youtubeData.setChannelDescription(snippet.getString("description"));
                youtubeData.setChannelPublishedAt(snippet.getString("publishedAt"));
                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                youtubeData.setChannelImageUrl(thumbnails.getJSONObject("medium").getString("url"));
                youtubeFragment.setYoutubeDataFetch(youtubeData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    static class YoutubeDataResponseListener implements Response.Listener<JSONObject> {
        private final YoutubeDataProvider youtubeDataProvider;
        private final YoutubeFragment youtubeFragment;

        YoutubeDataResponseListener(YoutubeFragment youtubeFragment, YoutubeDataProvider youtubeDataProvider){
            this.youtubeDataProvider = youtubeDataProvider;
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
                    builder.channelId(snippet.getString("channelId"));
                    builder.title(snippet.getString("title"));
                    builder.description(snippet.getString("description"));
                    builder.previewImageUrl(snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url"));

                    data.add(builder.build());
                }
                youtubeDataProvider.getAllChannelDataById(data);
                youtubeFragment.notifySuccessDataFetched();
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