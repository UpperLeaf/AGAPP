package com.wonsang.agapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.wonsang.agapp.model.YoutubeData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kr.co.prnd.YouTubePlayerView;

public class YoutubePlayListActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private List<YoutubeData> youtubeDataList;
    private YoutubePlayerStateChangedListener listener;
    private RecyclerView playListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_playlist);
        youTubePlayerView = findViewById(R.id.youtube_playlist_view);
        youtubeDataList = (ArrayList<YoutubeData>)getIntent().getSerializableExtra("youtubeData");

        List<String> videoIds = youtubeDataList.stream().map(YoutubeData::getVideoId).collect(Collectors.toList());

        youTubePlayerView.play(videoIds.get(0), new YouTubePlayerView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
                player.loadVideos(videoIds);
                listener = new YoutubePlayerStateChangedListener(player);
                player.setPlayerStateChangeListener(listener);
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        });

        playListView = findViewById(R.id.play_list_recyclerview);
        playListView.setAdapter(new PlayListAdapter(this, youtubeDataList));
        playListView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        listener.onConfigurationChanged(newConfig);
    }
    static class YoutubePlayerStateChangedListener implements YouTubePlayer.PlayerStateChangeListener{
        private YouTubePlayer youTubePlayer;

        YoutubePlayerStateChangedListener(YouTubePlayer youTubePlayer){
            this.youTubePlayer = youTubePlayer;
        }
        @Override
        public void onLoading() {

        }
        @Override
        public void onLoaded(String s) {
        }
        @Override
        public void onAdStarted() {

        }
        @Override
        public void onVideoStarted() {

        }
        @Override
        public void onVideoEnded(){

        }
        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            System.out.println(errorReason);
        }
        public void onConfigurationChanged(Configuration configuration){
            if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                youTubePlayer.setFullscreen(true);
            }
        }
    }

    static class PlayListAdapter extends RecyclerView.Adapter<PlayListViewHolder> {
        private Context context;
        private List<YoutubeData> youtubeData;

        PlayListAdapter(Context context, List<YoutubeData> youtubeData){
            this.context= context;
            this.youtubeData = youtubeData;
        }

        @NonNull
        @Override
        public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent,false);
            return new PlayListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayListViewHolder holder, int position) {
            holder.title.setText(youtubeData.get(position).getTitle());
            Glide.with(context).load(youtubeData.get(position).getPreviewImageUrl()).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return youtubeData.size();
        }
    }
    static class PlayListViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;
        private CardView cardView;
        public ImageView getImage() {
            return image;
        }
        public TextView getTitle() {
            return title;
        }

        public PlayListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.play_list_item_title);
            this.image = itemView.findViewById(R.id.play_list_item_image);
            this.cardView = itemView.findViewById(R.id.play_list_item_card);
        }
    }
}
