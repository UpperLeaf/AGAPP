package com.wonsang.agapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import kr.co.prnd.YouTubePlayerView;

public class YoutubePlayerActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private TextView videoTitle;
    private TextView videoDescription;
    private TextView videoPublishedAt;
    private ImageView channelImage;
    private Button willWatchVideoButton;
    private Button playListButton;
    private YoutubeDataProvider youtubeDataProvider;
    private Activity activity = this;

    private YoutubePlayerStateChangedListener listener;
    private YoutubeVideoAddWillWatchListener addWillWatchListener;
    private YoutubeVideoRemoveWillWatchListener removeWillWatchListener;
    private YoutubeVideoAddPlayListListener addPlayListListener;
    private YoutubeVideoRemovePlayListListener removePlayListListener;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        listener.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

        youtubeDataProvider = YoutubeDataProvider.getInstance();

        initListener(intent);
        startVideo(intent);
    }

    private void initListener(Intent intent) {
        boolean isWillWatch = intent.getExtras().getBoolean("isWillWatch");
        willWatchVideoButton = findViewById(R.id.will_video_list_button);
        addWillWatchListener = new YoutubeVideoAddWillWatchListener(youtubeDataProvider, intent.getStringExtra("videoId"), getApplicationContext());
        removeWillWatchListener = new YoutubeVideoRemoveWillWatchListener(youtubeDataProvider, intent.getStringExtra("videoId"), getApplicationContext());
        addWillWatchListener.setRemoveWillWatchListener(removeWillWatchListener);
        removeWillWatchListener.setAddWillWatchListener(addWillWatchListener);

        if(isWillWatch){
            willWatchVideoButton.setText("내가 볼 동영상 목록에서 삭제하기");
            willWatchVideoButton.setOnClickListener(removeWillWatchListener);
        }
        else {
            willWatchVideoButton.setText("내가 볼 동영상 목록에 추가하기");
            willWatchVideoButton.setOnClickListener(addWillWatchListener);
        }

        boolean isPlayList = intent.getExtras().getBoolean("isPlayList");
        playListButton = findViewById(R.id.play_list_button);
        addPlayListListener = new YoutubeVideoAddPlayListListener(youtubeDataProvider, intent.getStringExtra("videoId"), getApplicationContext());
        removePlayListListener = new YoutubeVideoRemovePlayListListener(youtubeDataProvider, intent.getStringExtra("videoId"), getApplicationContext());
        addPlayListListener.setRemovePlayListListener(removePlayListListener);
        removePlayListListener.setAddPlayListListener(addPlayListListener);

        if(!isPlayList){
            playListButton.setText("재생목록에 추가하기");
            playListButton.setOnClickListener(addPlayListListener);
        }else {
            playListButton.setText("재생목록에서 삭제하기");
            playListButton.setOnClickListener(removePlayListListener);
        }
    }

    private void startVideo(Intent intent) {
        String videoId = intent.getStringExtra("videoId");

        youTubePlayerView.play(videoId, new YouTubePlayerView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                listener = new YoutubePlayerStateChangedListener(youTubePlayer, youtubeDataProvider, videoId);
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
        private YoutubeDataProvider dataProvider;
        private String videoId;
        YoutubePlayerStateChangedListener(YouTubePlayer youTubePlayer, YoutubeDataProvider youtubeDataProvider, String videoId){
            this.youTubePlayer = youTubePlayer;
            this.dataProvider = youtubeDataProvider;
            this.videoId = videoId;
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
            dataProvider.updateRemoveWillWatchVideo(videoId);
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
    static class YoutubeVideoAddWillWatchListener implements View.OnClickListener {
        private YoutubeDataProvider youtubeDataProvider;
        private YoutubeVideoRemoveWillWatchListener removeWillWatchListener;
        private String videoId;
        private Context context;

        YoutubeVideoAddWillWatchListener(YoutubeDataProvider youtubeDataProvider, String videoId, Context context){
            this.youtubeDataProvider =youtubeDataProvider;
            this.videoId = videoId;
            this.context = context;
        }

        public void setRemoveWillWatchListener(YoutubeVideoRemoveWillWatchListener removeWillWatchListener) {
            this.removeWillWatchListener = removeWillWatchListener;
        }

        @Override
        public void onClick(View v) {
            youtubeDataProvider.updateAddWillWatchVideo(videoId);
            Toast.makeText(context, "내가 볼 동영상 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            ((Button)v).setText("내가 볼 동영상 목록에서 삭제하기");
            v.setOnClickListener(removeWillWatchListener);
        }
    }
    static class YoutubeVideoRemoveWillWatchListener implements View.OnClickListener {
        private YoutubeDataProvider youtubeDataProvider;
        private String videoId;
        private Context context;
        private YoutubeVideoAddWillWatchListener addWillWatchListener;

        YoutubeVideoRemoveWillWatchListener(YoutubeDataProvider youtubeDataProvider, String videoId, Context context){
            this.youtubeDataProvider = youtubeDataProvider;
            this.videoId = videoId;
            this.context = context;
        }

        public void setAddWillWatchListener(YoutubeVideoAddWillWatchListener addWillWatchListener) {
            this.addWillWatchListener = addWillWatchListener;
        }

        @Override
        public void onClick(View v) {
            youtubeDataProvider.updateRemoveWillWatchVideo(videoId);
            Toast.makeText(context, "내가 볼 동영상 목록에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            ((Button)v).setText("내가 볼 동영상 목록에 추가하기");
            v.setOnClickListener(addWillWatchListener);
        }
    }
    static class YoutubeVideoAddPlayListListener implements View.OnClickListener {
        private YoutubeDataProvider youtubeDataProvider;
        private YoutubeVideoRemovePlayListListener removePlayListListener;
        private String videoId;
        private Context context;

        YoutubeVideoAddPlayListListener(YoutubeDataProvider youtubeDataProvider, String videoId, Context context) {
            this.youtubeDataProvider =youtubeDataProvider;
            this.videoId = videoId;
            this.context = context;
        }
        public void setRemovePlayListListener(YoutubeVideoRemovePlayListListener removePlayListListener) {
            this.removePlayListListener = removePlayListListener;
        }

        @Override
        public void onClick(View v) {
            youtubeDataProvider.updateAddPlayList(videoId);
            Toast.makeText(context, "재생목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            ((Button)v).setText("재생목록에서 삭제하기");
            v.setOnClickListener(removePlayListListener);
        }
    }
    static class YoutubeVideoRemovePlayListListener implements View.OnClickListener {
        private YoutubeDataProvider youtubeDataProvider;
        private YoutubeVideoAddPlayListListener addPlayListListener;
        private String videoId;
        private Context context;

        YoutubeVideoRemovePlayListListener(YoutubeDataProvider dataProvider, String videoId, Context context){
            this.youtubeDataProvider = dataProvider;
            this.videoId = videoId;
            this.context = context;
        }
        public void setAddPlayListListener(YoutubeVideoAddPlayListListener addPlayListListener) {
            this.addPlayListListener = addPlayListListener;
        }

        @Override
        public void onClick(View v) {
            youtubeDataProvider.updateRemovePlayList(videoId);
            Toast.makeText(context, "재생목록에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            ((Button)v).setText("재생목록에 추가하기");
            v.setOnClickListener(addPlayListListener);
        }
    }
}
