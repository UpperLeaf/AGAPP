package com.wonsang.agapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.wonsang.agapp.model.YoutubeData;

import java.util.List;

@Dao
public interface YoutubeDataDao {

    @Query("SELECT * from youtube_data")
    List<YoutubeData> getAll();

    @Query("SELECT * from youtube_data WHERE search_value = :search")
    List<YoutubeData> getAllBySearch(String search);

    @Query("SELECT EXISTS(SELECT * from youtube_data WHERE video_title LIKE :title)")
    Boolean isExistLikeTitle(String title);

    @Query("SELECT COUNT(id) FROM youtube_data WHERE video_title LIKE :title")
    int getCountByLikeTitle(String title);

    @Query("SELECT * FROM youtube_data WHERE video_title LIKE :title ORDER BY column_published_at DESC LIMIT 1")
    YoutubeData findOneByLikeTitleOrderByColumnPublishedAtDesc(String title);

    @Query("SELECT * FROM youtube_data WHERE search_value = :search ORDER BY column_published_at DESC LIMIT 1")
    YoutubeData getLatestBySearch(String search);

    @Query("SELECT * FROM youtube_data WHERE is_will_watch = 1 ORDER BY published_at DESC")
    List<YoutubeData> findAllByWillWatchOrderByPublishedAtDesc();

    @Query("SELECT * FROM youtube_data WHERE is_favorite_list = 1 ORDER BY published_at DESC")
    List<YoutubeData> findAllByPlayListOrderByPublishedAtDesc();

    @Query("SELECT * FROM youtube_data ORDER BY column_published_at DESC LIMIT 1")
    YoutubeData getLatest();

    @Query("SELECT * FROM YOUTUBE_DATA WHERE video_title LIKE :title ORDER BY published_at DESC")
    List<YoutubeData> findAllByContainsTitle(String title);

    @Query("UPDATE youtube_data SET is_will_watch = :willWatch WHERE video_id = :videoId")
    void updateWillWatch(String videoId, int willWatch);

    @Query("UPDATE youtube_data SET is_favorite_list = :isPlayList WHERE video_id = :videoId")
    void updatePlayList(String videoId, int isPlayList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(YoutubeData youtubeData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<YoutubeData> youtubeData);

    @Delete
    void delete(YoutubeData youtubeData);

    @Delete
    void deleteAll(List<YoutubeData> youtubeData);


}
