package com.wonsang.agapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.wonsang.agapp.fragment.AddressFragment;
import com.wonsang.agapp.fragment.GalleryFragment;
import com.wonsang.agapp.fragment.SMSFragment;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayoutFragmentAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 pager2;

    private GalleryFragment galleryFragment;
    private AddressFragment addressFragment;
    private SMSFragment SMSFragment;

    private static final int ADDRESS_POSITION = 0;
    private static final int GALLERY_POSITION = 1;
    private static final int SMS_POSITION = 2;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.adapter = new TabLayoutFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        this.pager2 = findViewById(R.id.pager);
        this.tabLayout = findViewById(R.id.tab_layout);

        addressFragment = new AddressFragment();
        galleryFragment = new GalleryFragment();
        SMSFragment = new SMSFragment();

        this.adapter.addFragment(addressFragment);
        this.adapter.addFragment(galleryFragment);
        this.adapter.addFragment(SMSFragment);
        this.pager2.setAdapter(adapter);


        initializeTabLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GalleryFragment.CAMERA_INTENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap photo= (Bitmap)data.getExtras().get("data");
            saveImage(photo);
            galleryFragment.notifyDataChanged();
        }
    }

    private void saveImage(Bitmap bitmap){
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, new Date().toString(), getString(R.string.app_name));
    }

    private void initializeTabLayout() {
        new TabLayoutMediator(tabLayout, pager2, (tab, position) -> {
            switch (position){
                case ADDRESS_POSITION :
                    tab.setText("연락처");
                    break;
                case GALLERY_POSITION :
                    tab.setText("사진 갤러리");
                    break;
                case SMS_POSITION :
                    tab.setText("SMS 전송");
                    break;
            }
        }).attach();
    }

    public static class TabLayoutFragmentAdapter extends FragmentStateAdapter {

        private List<Fragment> fragments;

        public TabLayoutFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            fragments = new ArrayList<>();
        }

        public void addFragment(Fragment fragment) {
            this.fragments.add(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return this.fragments.size();
        }
    }
}
