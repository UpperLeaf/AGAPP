package com.wonsang.agapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.wonsang.agapp.fragment.AddressFragment;
import com.wonsang.agapp.fragment.GalleryFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayoutFragmentAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 pager2;

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

    public void initializeTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                pager2.setCurrentItem(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Ignored
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Ignored
            }
        });
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

//class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
//
//    private String[] address;
//
//    MyAdapter(String[] address) {
//        this.address = address;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        TextView v = new TextView(parent.getContext());
//        MyViewHolder viewHolder = new MyViewHolder(v);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        TextView view = holder.getTextView();
//        view.setText(address[position]);
//    }
//
//    @Override
//    public int getItemCount() {
//        return address.length;
//    }
//
//}
//
//class MyViewHolder extends RecyclerView.ViewHolder {
//
//    private TextView textView;
//
//    public TextView getTextView() {
//        return textView;
//    }
//
//    public MyViewHolder(@NonNull TextView itemView) {
//        super(itemView);
//        this.textView = itemView;
//    }
//}