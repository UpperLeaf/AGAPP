package com.wonsang.agapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wonsang.agapp.dao.YoutubeDataDao;
import com.wonsang.agapp.dao.YoutubeDatabase;
import com.wonsang.agapp.fragment.YoutubeFragment;
import com.wonsang.agapp.model.YoutubeData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class YoutubeDataProvider {

    private final String apiKey;
    private final RequestQueue requestQueue;
    private final YoutubeFragment youtubeFragment;
    private final YoutubeDataDao youtubeDataDao;

    private static final String channelRequestUrl = "https://www.googleapis.com/youtube/v3/channels?";
    private static final String searchRequestUrl = "https://www.googleapis.com/youtube/v3/search?";

    public YoutubeDataProvider(String key, Context context, YoutubeFragment fragment) {
        this.apiKey = key;
        this.requestQueue = Volley.newRequestQueue(context);
        this.youtubeFragment = fragment;
        this.youtubeDataDao = YoutubeDatabase.getInstance(context).youtubeDataDao();
    }

    public YoutubeData getLatestYoutubeData() {
        return youtubeDataDao.getLatest();
    }

    private StringBuilder createUrlBuilder(String requestUrl) {
        StringBuilder builder = new StringBuilder(requestUrl);
        builder.append("key=" + apiKey)
                .append("&part=snippet")
                .append("&regionCode=KR");
        return builder;
    }

    public void insertYoutubeDataAll(List<YoutubeData> youtubeData){
        youtubeDataDao.insertAll(youtubeData);
    }

    public void insertYoutubeData(YoutubeData youtubeData){
        this.youtubeDataDao.insert(youtubeData);
    }

    public List<YoutubeData> findBySearchValue(String searchValue){
        return youtubeDataDao.getAllBySearch(searchValue);
    }

    public void getDataByTitle(String query) {
        if(!youtubeDataDao.isExist(query)){
            requestData(query);
        }
        else{
            YoutubeData latest = youtubeDataDao.getLatestBySearch(query);
            if(latest.getPublishedAt().isBefore(LocalDateTime.now().minusHours(1))){
                requestData(query);
            }
            else {
                List<YoutubeData> youtubeData = youtubeDataDao.getAllBySearch(query);
                youtubeFragment.notifySuccessDataFetched(youtubeData);
            }
        }
    }

    public void requestData(String query){
        StringBuilder uri = createUrlBuilder(searchRequestUrl);
        uri.append("&q=" + query).append("&type=video");
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new YoutubeDataResponseListener(youtubeFragment,this, query), new ErrorListener());
        requestQueue.add(request);
    }

    public List<YoutubeData> getAllChannelDataById(List<YoutubeData> youtubeData){
        StringBuilder uri = createUrlBuilder(channelRequestUrl);
        uri.append("&id=");
        youtubeData.forEach((data) -> {
            String channelId = data.getChannelId();
            uri.append(channelId + ",");
        });
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new YoutubeChannelResponseListener(youtubeFragment, this, youtubeData), new ErrorListener());
        requestQueue.add(request);
        return youtubeData;
    }

    static class YoutubeChannelResponseListener implements Response.Listener<JSONObject> {
        private List<YoutubeData> youtubeDatas;
        private YoutubeDataProvider youtubeDataProvider;
        private YoutubeFragment youtubeFragment;

        YoutubeChannelResponseListener(YoutubeFragment youtubeFragment, YoutubeDataProvider youtubeDataProvider, List<YoutubeData> youtubeData) {
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
    static class YoutubeDataResponseListener implements Response.Listener<JSONObject> {
        private final YoutubeDataProvider youtubeDataProvider;
        private final YoutubeFragment youtubeFragment;

        private String query;

        YoutubeDataResponseListener(YoutubeFragment youtubeFragment, YoutubeDataProvider youtubeDataProvider, String query){
            this.youtubeDataProvider = youtubeDataProvider;
            this.youtubeFragment = youtubeFragment;
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
    static class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(error);
        }
    }

}