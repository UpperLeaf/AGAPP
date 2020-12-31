package com.wonsang.agapp.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.paging.PositionalDataSource;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wonsang.agapp.MainActivity;
import com.wonsang.agapp.R;
import com.wonsang.agapp.model.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GalleryAdapter adapter;
    private List<ImageModel> imageModels;

    public static final int CAMERA_INTENT_REQUEST_CODE = 100;



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
    }

    private void initAdapter() {
        imageModels = getAllImages();
        adapter = new GalleryAdapter(imageModels, getContext());
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
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
        //TODO Cursor Null Check
        while(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            imageModels.add(new ImageModel(imageUri));
        }
        cursor.close();
        return imageModels;
    }

    public void notifyDataChanged() {
        adapter.imageModels = getAllImages();
        adapter.notifyDataSetChanged();
    }

    static class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder>{
        private List<ImageModel> imageModels;
        private Context context;
        private final int CARD_IMAGE_NUMBER = 2;

        GalleryAdapter(List<ImageModel> imageModels, Context context) {
            this.imageModels = imageModels;
            this.context = context;
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
                    Glide.with(context)
                            .load(imageModels.get(position * CARD_IMAGE_NUMBER + i).getPath())
                            .centerCrop()
                            .into(imageViews.get(i));
                }
            }
        }

        @Override
        public int getItemCount() {
            return imageModels.size() / CARD_IMAGE_NUMBER + 1 ;
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

