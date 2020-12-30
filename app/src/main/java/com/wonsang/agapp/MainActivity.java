package com.wonsang.agapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.wonsang.agapp.fragment.AddressFragment;
import com.wonsang.agapp.fragment.GalleryFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayoutFragmentAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 pager2;

    private static final int ADDRESS_POSITION = 0;
    private static final int GALLERY_POSITION = 1;

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

        this.adapter.addFragment(new AddressFragment());
        this.adapter.addFragment(new GalleryFragment());

        this.pager2.setAdapter(adapter);

        initializeTabLayout();
    }

    private void initializeTabLayout() {
        new TabLayoutMediator(tabLayout, pager2, (tab, position) -> {
            switch (position){
                case ADDRESS_POSITION :
                    tab.setText("주소록");
                    break;
                case GALLERY_POSITION :
                    tab.setText("사진 갤러리");
                    break;
            }
            pager2.setCurrentItem(position);
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
