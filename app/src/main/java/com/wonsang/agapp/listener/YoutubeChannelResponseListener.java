package com.wonsang.agapp.listener;

import com.android.volley.Response;
import com.wonsang.agapp.YoutubeDataProvider;
import com.wonsang.agapp.fragment.YoutubeFragment;
import com.wonsang.agapp.model.YoutubeData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class YoutubeChannelResponseListener implements Response.Listener<JSONObject> {
    private List<YoutubeData> youtubeDatas;
    private YoutubeDataProvider youtubeDataProvider;
    private YoutubeFragment youtubeFragment;

    public YoutubeChannelResponseListener(YoutubeFragment youtubeFragment, YoutubeDataProvider youtubeDataProvider, List<YoutubeData> youtubeData) {
        this.youtubeFragment = youtubeFragment;
        this.youtubeDatas = youtubeData;
        this.youtubeDataProvider = youtubeDataProvider;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray items = response.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject snippet = items.getJSONObject(i).getJSONObject("snippet");
                YoutubeData youtubeData;
                for(int j = 0; j < youtubeDatas.size(); j++){
                    if(youtubeDatas.get(j).getChannelId().equals(items.getJSONObject(i).getString("id"))){
                        youtubeData = youtubeDatas.get(j);
                        youtubeData.setChannelDescription(snippet.getString("description"));
                        youtubeData.setChannelPublishedAt(snippet.getString("publishedAt"));
                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                        youtubeData.setChannelImageUrl(thumbnails.getJSONObject("medium").getString("url"));
                    }
                }
            }
            youtubeDataProvider.insertYoutubeDataAll(youtubeDatas);
            youtubeFragment.notifySuccessDataFetched(youtubeDatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
