package com.wonsang.agapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import kr.co.prnd.YouTubePlayerView;

public class YoutubePlayerActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private TextView videoTitle;
    private TextView videoDescription;
    private TextView videoPublishedAt;
    private ImageView channelImage;
    private YoutubePlayerStateChangedListener listener;
    private Activity activity = this;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        listener.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_player_main);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        Intent intent = getIntent();

        videoTitle = findViewById(R.id.player_video_title);
        videoTitle.setText(intent.getStringExtra("videoTitle"));

        videoDescription = findViewById(R.id.player_video_description);
        videoDescription.setText(intent.getStringExtra("description"));

        channelImage = findViewById(R.id.player_channel_image);
        Glide.with(this).load(intent.getStringExtra("channelImageUrl")).into(channelImage);

        videoPublishedAt = findViewById(R.id.player_video_published_at);
        videoPublishedAt.setText(intent.getStringExtra("publishedAt"));

        startVideo(intent);
    }

    private void startVideo(Intent intent) {
        String videoId = intent.getStringExtra("videoId");

        youTubePlayerView.play(videoId, new YouTubePlayerView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                listener = new YoutubePlayerStateChangedListener(youTubePlayer);
                youTubePlayer.setPlayerStateChangeListener(listener);
                youTubePlayer.setOnFullscreenListener(new YoutubePlayerFullScreenListener(activity));
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(), "비디오를 재생할 수 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
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
            youTubePlayer.play();
        }
        @Override
        public void onAdStarted() {

        }
        @Override
        public void onVideoStarted() {

        }
        @Override
        public void onVideoEnded() {

        }
        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }

        public void onConfigurationChanged(Configuration configuration){
            if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                youTubePlayer.setFullscreen(true);
            }
        }
    }
    static class YoutubePlayerFullScreenListener implements YouTubePlayer.OnFullscreenListener {
        private Activity activity;
        private YoutubePlayerFullScreenListener(Activity activity){
            this.activity = activity;
        }
        @Override
        public void onFullscreen(boolean isFullScreen) {
            if(!isFullScreen){
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }
}
