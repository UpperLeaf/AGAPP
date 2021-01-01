package com.wonsang.agapp.observer;

import java.util.Observable;
import java.util.Observer;

public class YoutubeDataManager extends Observable {

    private static final YoutubeDataManager youtubeDataManager = new YoutubeDataManager();

    private YoutubeDataManager() {};

    public static YoutubeDataManager getInstance() {
        return youtubeDataManager;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

    public void notifyDataFetched(String query) {
        setChanged();
        notifyObservers(query);
    }
}
