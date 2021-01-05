package com.wonsang.agapp.dao;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.wonsang.agapp.model.YoutubeData;

@Database(entities = {YoutubeData.class}, version = 1)
@TypeConverters({RoomTypeConverter.class})
public abstract class YoutubeDatabase extends RoomDatabase {
    private static YoutubeDatabase youtubeDatabase;
    public abstract YoutubeDataDao youtubeDataDao();

    public static YoutubeDatabase getInstance(Context context) {
        if(youtubeDatabase == null){
            youtubeDatabase = Room.databaseBuilder(context.getApplicationContext(), YoutubeDatabase.class, "youtube-database")
                    .allowMainThreadQueries()
                    .build();
        }
        return youtubeDatabase;
    }
}
