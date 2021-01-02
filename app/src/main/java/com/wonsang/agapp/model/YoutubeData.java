package com.wonsang.agapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity(tableName = "youtube_data", indices = {@Index(value = "video_id", unique = true)})
public class YoutubeData{

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "search_value")
    private String searchValue;

    @ColumnInfo(name = "published_at")
    private LocalDateTime publishedAt;

    @ColumnInfo(name = "column_published_at")
    private LocalDateTime columnPublishedAt;

    @ColumnInfo(name = "video_id")
    private String videoId;

    @ColumnInfo(name = "video_title")
    private String title;

    @ColumnInfo(name = "channel_id")
    private String channelId;

    @ColumnInfo(name = "channel_title")
    private String channelTitle;

    @ColumnInfo(name = "video_description")
    private String description;

    @ColumnInfo(name = "video_image_url")
    private String previewImageUrl;

    @ColumnInfo(name = "channel_published_at")
    private LocalDateTime channelPublishedAt;

    @ColumnInfo(name = "channel_image_url")
    private String channelImageUrl;

    @ColumnInfo(name = "channel_description")
    private String channelDescription;


    public YoutubeData(LocalDateTime publishedAt, String videoId, String title, String channelTitle, String description, String previewImageUrl, String channelId, String searchValue){
        this.publishedAt = publishedAt;
        this.videoId = videoId;
        this.title = title;
        this.channelTitle = channelTitle;
        this.description = description;
        this.previewImageUrl = previewImageUrl;
        this.channelId = channelId;
        this.searchValue = searchValue;
    }

    public void setColumnPublishedAt(LocalDateTime now) {
        this.columnPublishedAt = now;
    }
    public void setChannelPublishedAt(String channelPublishedAt) {
        try {
            this.channelPublishedAt = LocalDateTime.parse(channelPublishedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        }catch (DateTimeParseException e){
            this.channelPublishedAt = LocalDateTime.parse(channelPublishedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"));
        }
    }
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public void setChannelPublishedAt(LocalDateTime channelPublishedAt) {
        this.channelPublishedAt = channelPublishedAt;
    }
    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }
    public void setChannelImageUrl(String channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }
    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public LocalDateTime getColumnPublishedAt() {
        return columnPublishedAt;
    }
    public long getId() {
        return id;
    }
    public String getSearchValue() {
        return searchValue;
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
    public String getChannelId() { return channelId; }
    public String getChannelDescription() {
        return channelDescription;
    }
    public LocalDateTime getChannelPublishedAt() {
        return channelPublishedAt;
    }
    public String getChannelImageUrl() {
        return channelImageUrl;
    }

    public static class Builder {
        private LocalDateTime publishedAt;
        private String channelId;
        private String videoId;
        private String title;
        private String channelTitle;
        private String description;
        private String previewImageUrl;
        private String searchValue;

        public Builder searchValue(String searchValue){
            this.searchValue = searchValue;
            return this;
        }
        public Builder publishedAt(String publishedAt) {
            this.publishedAt = LocalDateTime.parse(publishedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            return this;
        }
        public Builder channelId(String channelId){
            this.channelId = channelId;
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
            return new YoutubeData(publishedAt, videoId, title, channelTitle, description, previewImageUrl, channelId, searchValue);
        }
    }
}
