package com.wonsang.agapp.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wonsang.agapp.R;
import com.wonsang.agapp.listener.RecyclerViewScrollListener;
import com.wonsang.agapp.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GalleryAdapter adapter;
    private List<ImageModel> imageModels;

    public static final int CAMERA_INTENT_REQUEST_CODE = 100;
    private final int PAGING_SIZE = 10;
    private int size = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageModels = getAllImages();

        initRecyclerView(view);
        initAdapter();
        initCamera(view);
    }

    private void initRecyclerView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.gallery_recycler_view);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener(this));
    }

    private void initAdapter() {
        imageModels = getAllImages();
        adapter = new GalleryAdapter(imageModels, getContext(), layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initCamera(@NonNull View view) {
        Button cameraButton = (Button) view.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getActivity().startActivityForResult(intent, CAMERA_INTENT_REQUEST_CODE);
        });
    }


    private List<ImageModel> getImages(int startPosition, int requestSize) {
        return imageModels.subList(startPosition, startPosition + requestSize);
    }

    private List<ImageModel> getAllImages() {
        List<ImageModel> imageModels = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC limit " + size);
        //TODO Cursor Null Check
        cursor.moveToPosition(size - PAGING_SIZE - 1);
        while(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            imageModels.add(new ImageModel(imageUri));
        }
        cursor.close();
        return imageModels;
    }

    public void notifyDataChanged() {
        List<ImageModel> models = getAllImages();
        int startPosition = adapter.imageModels.size();
        adapter.imageModels.addAll(models);
        adapter.notifyItemRangeInserted(startPosition, models.size());
    }

    public boolean loadMoreData() {
        size += PAGING_SIZE;
        List<ImageModel> data = getAllImages();
        if(data.size() == 0)
            return false;
        Toast.makeText(getContext(), "데이터 가져오는 중.", Toast.LENGTH_SHORT).show();
        return true;
    }

    static class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder>{
        private List<ImageModel> imageModels;
        private Context context;
        private RecyclerView.LayoutManager layoutManager;
        private final int CARD_IMAGE_NUMBER = 2;


        GalleryAdapter(List<ImageModel> imageModels, Context context , RecyclerView.LayoutManager layoutManager) {
            this.imageModels = imageModels;
            this.context = context;
            this.layoutManager = layoutManager;
        }

        public void setImageModels(List<ImageModel> imageModels) {
            this.imageModels = imageModels;
        }

        @NonNull
        @Override
        public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.galllery_card, parent,false);
            return new GalleryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
            List<ImageView> imageViews = holder.getImageViews();
            for(int i = 0; i < CARD_IMAGE_NUMBER; i++){
                if (imageModels.size() > position * CARD_IMAGE_NUMBER + i){
                    imageViews.get(i).setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(imageModels.get(position * CARD_IMAGE_NUMBER + i).getPath())
                            .centerCrop()
                            .into(imageViews.get(i));
                }
                else {
                    imageViews.get(i).setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return (int)Math.ceil(imageModels.size() / (double)CARD_IMAGE_NUMBER);
        }
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        List<ImageView> imageViews;
        public GalleryViewHolder(@NonNull View view) {
            super(view);
            imageViews = new ArrayList<>();
            imageViews.add(view.findViewById(R.id.gallery_imageView_item1));
            imageViews.add(view.findViewById(R.id.gallery_imageView_item2));
        }
        public List<ImageView> getImageViews() {
            return imageViews;
        }
    }
}

