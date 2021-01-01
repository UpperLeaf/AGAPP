package com.wonsang.agapp.listener;

import com.android.volley.Response;
import com.wonsang.agapp.YoutubeDataProvider;
import com.wonsang.agapp.model.YoutubeData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class YoutubeDataResponseListener implements Response.Listener<JSONObject> {
    private final YoutubeDataProvider youtubeDataProvider;
    private String query;

    public YoutubeDataResponseListener(YoutubeDataProvider youtubeDataProvider, String query){
        this.youtubeDataProvider = youtubeDataProvider;
        this.query = query;
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
                builder.searchValue(query);
                YoutubeData youtubeData = builder.build();
                youtubeData.setPublishedAt(LocalDateTime.now());
                data.add(youtubeData);
            }
            youtubeDataProvider.getAllChannelDataById(data);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}