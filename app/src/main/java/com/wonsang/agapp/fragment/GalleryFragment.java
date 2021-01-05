package com.wonsang.agapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wonsang.agapp.R;
import com.wonsang.agapp.dialog.ImageDialog;
import com.wonsang.agapp.listener.RecyclerViewScrollListener;
import com.wonsang.agapp.model.ImageModel;
import com.wonsang.agapp.utils.ContentInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ContentInfoProvider contentInfoProvider;
    private GalleryAdapter adapter;
    private List<ImageModel> imageModels;

    public static final int CAMERA_INTENT_REQUEST_CODE = 100;
    private final int PAGING_SIZE = 10;
    private int size;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentInfoProvider = new ContentInfoProvider();
        initRecyclerView(view);
        initAdapter();
    }


    private void initRecyclerView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.gallery_recycler_view);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener(this));
    }

    private void initAdapter() {
        size = 0;
        imageModels = getPagingImages();
        size = imageModels.size();
        adapter = new GalleryAdapter(imageModels, getContext());
        recyclerView.setAdapter(adapter);
    }

    private List<ImageModel> getPagingImages() {
        return contentInfoProvider.getImages(getContext().getContentResolver(), size, PAGING_SIZE);
    }


    public void notifyDataChanged() {
        List<ImageModel> models = getPagingImages();
        int startPosition = adapter.imageModels.size();
        adapter.imageModels.addAll(models);
        adapter.notifyItemRangeInserted(startPosition, models.size());
    }

    public boolean loadMoreData() {
        size += PAGING_SIZE;
        List<ImageModel> data = getPagingImages();
        if(data.size() == 0)
            return false;
        Toast.makeText(getContext(), "데이터 가져오는 중.", Toast.LENGTH_SHORT).show();
        return true;
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
                    imageViews.get(i).setVisibility(View.VISIBLE);
                    Uri imageUri = imageModels.get(position * CARD_IMAGE_NUMBER + i).getPath();
                    Glide.with(context)
                            .load(imageUri)
                            .centerCrop()
                            .into(imageViews.get(i));
                    imageViews.get(i).setOnClickListener(v -> {
                        Dialog dialog = new ImageDialog(context, imageUri);
                        dialog.setCancelable(true);
                        dialog.show();
                    });
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

