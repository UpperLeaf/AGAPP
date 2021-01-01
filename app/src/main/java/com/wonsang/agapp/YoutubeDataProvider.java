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
import com.wonsang.agapp.listener.YoutubeChannelResponseListener;
import com.wonsang.agapp.listener.YoutubeDataResponseListener;
import com.wonsang.agapp.model.YoutubeData;
import com.wonsang.agapp.observer.YoutubeDataManager;


import java.time.LocalDateTime;
import java.util.List;

public class YoutubeDataProvider {

    private final String apiKey;
    private final RequestQueue requestQueue;
    private final YoutubeDataDao youtubeDataDao;

    private static final String channelRequestUrl = "https://www.googleapis.com/youtube/v3/channels?";
    private static final String searchRequestUrl = "https://www.googleapis.com/youtube/v3/search?";

    public YoutubeDataProvider(String key, Context context) {
        this.apiKey = key;
        this.requestQueue = Volley.newRequestQueue(context);
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
            else
                YoutubeDataManager.getInstance().notifyDataFetched(query);
        }
    }

    public void requestData(String query){
        StringBuilder uri = createUrlBuilder(searchRequestUrl);
        uri.append("&q=" + query).append("&type=video");
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET,
                        uri.toString(),
                        null,
                        new YoutubeDataResponseListener(this, query),
                        new ErrorListener());
        requestQueue.add(request);
    }

    public List<YoutubeData> getAllChannelDataById(List<YoutubeData> youtubeData, String query){
        StringBuilder uri = createUrlBuilder(channelRequestUrl);
        uri.append("&id=");
        youtubeData.forEach((data) -> {
            String channelId = data.getChannelId();
            uri.append(channelId + ",");
        });
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, uri.toString(),
                null,
                new YoutubeChannelResponseListener(this, youtubeData, query),
                new ErrorListener());
        requestQueue.add(request);
        return youtubeData;
    }


    static class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(error);
        }
    }

}