package com.wonsang.agapp.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wonsang.agapp.fragment.GalleryFragment;

public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private GalleryFragment fragment;

    public RecyclerViewScrollListener(GalleryFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(!recyclerView.canScrollVertically(1) && fragment.loadMoreData()){
            fragment.notifyDataChanged();
        }
    }
}
