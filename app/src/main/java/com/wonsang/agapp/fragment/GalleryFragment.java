package com.wonsang.agapp.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wonsang.agapp.R;
import com.wonsang.agapp.model.ImageModel;

import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.gallery_recycler_view);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new GalleryAdapter(getAllImages(), getContext()));
    }

    private List<ImageModel> getAllImages() {
        List<ImageModel> imageModels = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        //TODO Cursor Null Check
        while(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            imageModels.add(new ImageModel(imageUri));
        }
        cursor.close();
        return imageModels;
    }

    static class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder>{
        private List<ImageModel> imageModels;
        private Context context;

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
            ImageView imageView = holder.getImageView();
            Glide.with(context)
                .load(imageModels.get(position).getPath())
                .centerCrop()
                .into(imageView);
        }

        @Override
        public int getItemCount() {
            return imageModels.size();
        }
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GalleryViewHolder(@NonNull View view) {
            super(view);
            this.imageView = view.findViewById(R.id.gallery_imageView);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}
