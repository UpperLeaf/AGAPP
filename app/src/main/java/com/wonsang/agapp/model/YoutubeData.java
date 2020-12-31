package com.wonsang.agapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class YoutubeData {
    private LocalDateTime publishedAt;
    private String videoId;
    private String title;
    private String channelTitle;
    private String description;
    private String previewImageUrl;

    YoutubeData(LocalDateTime publishedAt, String videoId, String title, String channelTitle, String description, String previewImageUrl){
        this.publishedAt = publishedAt;
        this.videoId = videoId;
        this.title = title;
        this.channelTitle = channelTitle;
        this.description = description;
        this.previewImageUrl = previewImageUrl;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    public String getChannelTitle() {
        return channelTitle;
    }
    public String getDescription() {
        return description;
    }
    public String getPreviewImageUrl() {
        return previewImageUrl;
    }
    public String getTitle() {
        return title;
    }
    public String getVideoId() {
        return videoId;
    }

    public static class Builder {
        private LocalDateTime publishedAt;
        private String videoId;
        private String title;
        private String channelTitle;
        private String description;
        private String previewImageUrl;

        public Builder publishedAt(String publishedAt) {
            this.publishedAt = LocalDateTime.parse(publishedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            return this;
        }
        public Builder videoId(String videoId){
            this.videoId = videoId;
            return this;
        }
        public Builder title(String title){
            this.title = title;
            return this;
        }
        public Builder channelTitle(String channelTitle){
            this.channelTitle = channelTitle;
            return this;
        }
        public Builder description(String description){
            this.description = description;
            return this;
        }
        public Builder previewImageUrl(String previewImageUrl){
            this.previewImageUrl = previewImageUrl;
            return this;
        }

        public YoutubeData build() {
            return new YoutubeData(publishedAt, videoId, title, channelTitle, description, previewImageUrl);
        }
    }
}
