package com.wonsang.agapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wonsang.agapp.R;
import com.wonsang.agapp.YoutubeDataProvider;
import com.wonsang.agapp.YoutubePlayListActivity;
import com.wonsang.agapp.YoutubePlayerActivity;
import com.wonsang.agapp.dao.YoutubeDataDao;
import com.wonsang.agapp.dao.YoutubeDatabase;
import com.wonsang.agapp.dialog.ChannelInfoDialog;
import com.wonsang.agapp.model.ImageModel;
import com.wonsang.agapp.model.YoutubeData;
import com.wonsang.agapp.observer.YoutubeDataManager;

import org.w3c.dom.Text;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;


public class YoutubeFragment extends Fragment implements Observer {

    private SearchView searchView;
    private SearchViewTextListener textListener;
    private RecyclerView recyclerView;
    private YoutubeDataProvider youtubeDataProvider;
    private YoutubeAdapter youtubeAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton actionButton;
    private FloatingActionButton playListButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.youtube_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.search_view);
        youtubeDataProvider = YoutubeDataProvider.createInstance(getString(R.string.youtube_api_key), getContext());


        recyclerView = view.findViewById(R.id.youtube_recycler_view);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        youtubeAdapter = new YoutubeAdapter(getContext(), youtubeDataProvider);
        recyclerView.setAdapter(youtubeAdapter);

        searchView = view.findViewById(R.id.search_view);
        textListener = new SearchViewTextListener(youtubeDataProvider, youtubeAdapter);
        searchView.setOnQueryTextListener(textListener);

        actionButton = view.findViewById(R.id.will_watch_list_fab);
        actionButton.setOnClickListener(v -> {
            youtubeAdapter.initAsWillWatchYoutubeData();
            textListener.setQuery("");
        });

        playListButton = view.findViewById(R.id.play_list_fab);
        playListButton.setOnClickListener(v -> {
            Context context = getContext();
            ArrayList<YoutubeData> data = (ArrayList<YoutubeData>)youtubeDataProvider.findAllPlayListOrderByDesc();
            Intent intent = new Intent(context, YoutubePlayListActivity.class);
            intent.putExtra("youtubeData", data);
            context.startActivity(intent);
        });

        YoutubeDataManager.getInstance().addObserver(this);
    }

    @Override
    public void onResume() {
        reload();
        super.onResume();
    }

    public void reload(){
        String query = textListener.getQuery();
        ((YoutubeAdapter)recyclerView.getAdapter()).reload(query);
    }

    @Override
    public void update(Observable o, Object org) {
        String query = (String)org;
        List<YoutubeData> youtubeData = youtubeDataProvider.findAllContainsTitle(query);
        YoutubeAdapter adapter = Objects.requireNonNull((YoutubeAdapter) recyclerView.getAdapter());
        adapter.youtubeData = youtubeData;
        adapter.notifyDataSetChanged();
    }

    static class SearchViewTextListener implements SearchView.OnQueryTextListener {
        private final YoutubeDataProvider dataProvider;
        private final YoutubeAdapter adapter;
        private String query;


        SearchViewTextListener(YoutubeDataProvider youtubeDataProvider, YoutubeAdapter adapter) {
            this.dataProvider = youtubeDataProvider;
            this.adapter = adapter;
            this.query = "";
        }

        public String getQuery() {
            return query;
        }
        public void setQuery(String query) {
            this.query = query;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            this.query = query;
            dataProvider.getDataByTitle(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }

    static class YoutubeAdapter extends RecyclerView.Adapter<YoutubeViewHolder> {
        private List<YoutubeData> youtubeData;
        private YoutubeDataProvider dataProvider;
        private Context context;

        YoutubeAdapter(Context context, YoutubeDataProvider youtubeDataProvider) {
            this.context = context;
            this.dataProvider = youtubeDataProvider;
            this.youtubeData = youtubeDataProvider.getWillWatchYoutubeData();
        }

        public void initAsWillWatchYoutubeData() {
            this.youtubeData = dataProvider.getWillWatchYoutubeData();
            notifyDataSetChanged();
        }


        public void reload(String query) {
            if(query.isEmpty())
                this.youtubeData = dataProvider.getWillWatchYoutubeData();
            else
                this.youtubeData = dataProvider.findAllContainsTitle(query);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public YoutubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.youtube_card, parent,false);
            return new YoutubeViewHolder(view, context);
        }

        @Override
        public void onBindViewHolder(@NonNull YoutubeViewHolder holder, int position) {
            YoutubeData data = youtubeData.get(position);
            holder.title.setText(data.getTitle());
            holder.channelTitle.setText(data.getChannelTitle());
            holder.publishedAt.setText(data.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss")));
            holder.setYoutubeData(data);
            Glide.with(context).load(data.getPreviewImageUrl()).into(holder.imageView);
            Glide.with(context).load(data.getChannelImageUrl()).into(holder.channelImage);
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, YoutubePlayerActivity.class);
                intent.putExtra("videoId", data.getVideoId());
                intent.putExtra("publishedAt", data.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss")));
                intent.putExtra("videoTitle", data.getTitle());
                intent.putExtra("description", data.getDescription());
                intent.putExtra("channelImageUrl", data.getChannelImageUrl());
                intent.putExtra("isWillWatch", data.isWillWatch());
                intent.putExtra("isPlayList", data.isFavoriteList());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return youtubeData.size();
        }
    }

    static class YoutubeViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView channelTitle;
        private TextView publishedAt;
        private ImageView channelImage;
        private ImageView imageView;
        private ChannelButtonListener listener;

        public void setYoutubeData(YoutubeData data){
            listener.setYoutubeData(data);
        }

        public YoutubeViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.title = itemView.findViewById(R.id.youtube_title);
            this.channelTitle = itemView.findViewById(R.id.youtube_channel_title);
            this.publishedAt = itemView.findViewById(R.id.youtube_published_at);
            this.imageView = itemView.findViewById(R.id.youtube_preview);
            this.channelImage = itemView.findViewById(R.id.youtube_channel_image);
            this.listener = new ChannelButtonListener(context);
            this.channelImage.setOnClickListener(listener);
        }
    }

    static class ChannelButtonListener implements View.OnClickListener {
        private YoutubeData youtubeData;
        private Context context;

        ChannelButtonListener(Context context){
            this.context = context;
        }

        public void setYoutubeData(YoutubeData youtubeData) {
            this.youtubeData = youtubeData;
        }

        @Override
        public void onClick(View v) {
            ChannelInfoDialog dialog = new ChannelInfoDialog(context, youtubeData);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
        }
    }
}